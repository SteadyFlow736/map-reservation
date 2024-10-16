package org.example.mapreservation.reservation.application;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.application.repository.CustomerRepository;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.hairshop.application.repository.HairShopRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.reservation.application.repository.HairShopReservationRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationResponse;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class HairShopReservationServiceImplTest {

    HairShopReservationRepository hairShopReservationRepository;
    HairShopRepository hairShopRepository;
    CustomerRepository customerRepository;
    RedissonClient redissonClient;
    TransactionTemplate transactionTemplate;

    HairShopReservationServiceImpl hairShopReservationService;

    Owner persistedOwner;
    HairShop persistedHairShop;
    Customer persistedCustomer;

    @BeforeEach
    void setUp() {
        injectMocks();
        initField();
    }

    private void injectMocks() {
        hairShopReservationRepository = mock(HairShopReservationRepository.class);
        hairShopRepository = mock(HairShopRepository.class);
        customerRepository = mock(CustomerRepository.class);
        redissonClient = mock(RedissonClient.class);
        transactionTemplate = mock(TransactionTemplate.class);

        hairShopReservationService = new HairShopReservationServiceImpl(
                hairShopReservationRepository,
                hairShopRepository,
                customerRepository,
                redissonClient,
                transactionTemplate);
    }

    private void initField() {
        persistedOwner = new Owner(1L, "주인1");
        persistedHairShop = HairShop.builder()
                .id(1L)
                .name("헤어샵1")
                .owner(persistedOwner)
                .address(new Address("도로주소", "상세주소"))
                .longitude("10.0")
                .latitude("20.0")
                .imageUrls(List.of("url1", "url2", "url3"))
                .build();
        persistedCustomer = Customer.builder()
                .id(1L)
                .email("abc@gmail.com")
                .password("Password1!")
                .build();
    }


    @Test
    void 동시성_고려하여_헤어샵_예약할_수_있다() throws InterruptedException {
        // given
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 14, 19, 0);
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 15, 19, 0);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        // given - collaborators
        Long createReservationId = 33L;
        boolean locked = true;
        RLock lock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(locked);
        when(transactionTemplate.execute(any())).thenReturn(createReservationId);

        ArgumentCaptor<String> lockKeyCaptor = ArgumentCaptor.forClass(String.class);

        // when
        HairShopReservationCreateResponse response =
                hairShopReservationService.createReservation(1L, "abc@gmail.com", currentTime, request);

        // then
        verify(redissonClient, times(1)).getLock(lockKeyCaptor.capture());
        String lockKey = lockKeyCaptor.getValue();
        assertThat(lockKey).isEqualTo("1:2024-10-15T19:00");
        assertThat(response.reservationId()).isEqualTo(33L);
    }

    @Test
    void 헤어샵_예약_시_예약_슬롯의_락킹을_잡지_못하면_예외가_던져진다() throws InterruptedException {
        // given
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 14, 19, 0);
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 15, 19, 0);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        // given - collaborators
        boolean locked = false;
        RLock lock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(locked);

        // when, then
        assertThatThrownBy(() ->
                hairShopReservationService.createReservation(1L, "abc@gmail.com", currentTime, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("일시적인 오류가 발생했습니다.: 잠시 후 다시 시도해 주세요.");
    }

    @Test
    void 헤어샵_하루_예약_현황을_조회할_수_있다() {
        // given
        LocalDate targetDate = LocalDate.of(2024, 10, 14);
        List<HairShopReservation> reservations = List.of(
                new HairShopReservation(null, persistedCustomer, persistedHairShop, targetDate.atTime(13, 0)),
                new HairShopReservation(null, persistedCustomer, persistedHairShop, targetDate.atTime(13, 30)),
                new HairShopReservation(null, persistedCustomer, persistedHairShop, targetDate.atTime(14, 0)),
                new HairShopReservation(null, persistedCustomer, persistedHairShop, targetDate.atTime(14, 30))
        );
        when(hairShopRepository.findById(persistedHairShop.getId()))
                .thenReturn(Optional.of(persistedHairShop));
        when(hairShopReservationRepository.findByHairShopAndTargetDate(persistedHairShop, targetDate))
                .thenReturn(reservations);

        // when
        ReservationStatus reservationStatus = hairShopReservationService
                .getReservationStatus(1L, LocalDate.of(2024, 10, 14));

        // then
        assertThat(reservationStatus.date()).isEqualTo(LocalDate.of(2024, 10, 14));
        assertThat(reservationStatus.openingTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(reservationStatus.closingTime()).isEqualTo(LocalTime.of(20, 0));
        assertThat(reservationStatus.reservedTimes()).containsExactlyInAnyOrder(
                LocalTime.of(13, 0),
                LocalTime.of(13, 30),
                LocalTime.of(14, 0),
                LocalTime.of(14, 30)
        );
    }

    @Test
    void 헤어샵_하루_예약_현황을_조회_시_헤어샵이_검색되지_않는다면_예외가_던져진다() {
        // given
        Long shopId = 41L;
        when(hairShopRepository.findById(shopId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> hairShopReservationService
                .getReservationStatus(41L, LocalDate.of(2024, 10, 14)))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 헤어샵입니다.");
    }

    @Test
    void 유저는_예약_아이디로_예약을_상세_조회할_수_있다() {
        // given
        Long reservationId = 41L;
        HairShopReservation hairShopReservation = HairShopReservation.builder()
                .id(reservationId)
                .customer(persistedCustomer)
                .hairShop(persistedHairShop)
                .reservationTime(LocalDateTime.of(2024, 10, 14, 19, 0))
                .build();
        String username = "abc@gmail.com";
        when(hairShopReservationRepository
                .findByIdAndCustomerEmail(reservationId, username))
                .thenReturn(Optional.of(hairShopReservation));

        // when
        HairShopReservationResponse response =
                hairShopReservationService.getReservation(41L, "abc@gmail.com");

        // then
        assertThat(response.reservationId()).isEqualTo(41L);
        assertThat(response.username()).isEqualTo("abc@gmail.com");
        assertThat(response.hairShopResponse().shopId()).isEqualTo(1L);
        assertThat(response.hairShopResponse().shopName()).isEqualTo("헤어샵1");
        assertThat(response.hairShopResponse().longitude()).isEqualTo("10.0");
        assertThat(response.hairShopResponse().latitude()).isEqualTo("20.0");
        assertThat(response.hairShopResponse().images()).containsExactlyInAnyOrder("url1", "url2", "url3");
        assertThat(response.reservationTime()).isEqualTo(
                LocalDateTime.of(2024, 10, 14, 19, 0));
        assertThat(response.status()).isEqualTo(HairShopReservation.Status.RESERVED);
    }

    @Test
    void 유저의_것이_아니거나_없는_예약_아이디로_예약_조회_시_예외가_던져진다() {
        // given
        Long reservationId = 41L;
        String username = "abc@gmail.com";
        when(hairShopReservationRepository
                .findByIdAndCustomerEmail(reservationId, username))
                .thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> hairShopReservationService.getReservation(41L, "abc@gmail.com"));
    }

    @Test
    void 유저는_자신의_헤어샵_예약_내역을_확인할_수_있다() {
        // given
        List<HairShopReservation> reservations = List.of(
                HairShopReservation.builder()
                        .id(1L)
                        .customer(persistedCustomer)
                        .hairShop(persistedHairShop)
                        .reservationTime(LocalDateTime.of(2024, 10, 14, 12, 0))
                        .build(),
                HairShopReservation.builder()
                        .id(2L)
                        .customer(persistedCustomer)
                        .hairShop(persistedHairShop)
                        .reservationTime(LocalDateTime.of(2024, 10, 14, 12, 30))
                        .build()
        );
        Slice<HairShopReservation> hairShopReservationSlice = new SliceImpl<>(reservations);

        // given - collaborators
        Pageable pageable = PageRequest.of(0, 2,
                Sort.by(Sort.Direction.ASC, "reservationTime")
        );
        when(hairShopReservationRepository.findByCustomerEmail("abc@gmail.com", pageable))
                .thenReturn(hairShopReservationSlice);

        // when
        Slice<HairShopReservationResponse> responseSlice =
                hairShopReservationService.getReservations("abc@gmail.com", pageable);

        // then
        assertThat(responseSlice.getContent().size()).isEqualTo(2);
        assertThat(responseSlice.getContent().get(0).reservationId()).isEqualTo(1L);
        assertThat(responseSlice.getContent().get(1).reservationId()).isEqualTo(2L);
    }

    @Test
    void 유저는_예약을_취소할_수_있다() {
        // given
        HairShopReservation hairShopReservation = mock(HairShopReservation.class);
        when(hairShopReservationRepository.findByIdAndCustomerEmail(41L, "abc@gmail.com"))
                .thenReturn(Optional.of(hairShopReservation));

        // when
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 14, 19, 0);
        hairShopReservationService.cancelReservation(41L, "abc@gmail.com", currentTime);

        // then
        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(hairShopReservation, times(1)).cancel(captor.capture());
        assertThat(captor.getValue()).isEqualTo(currentTime);
    }

    @Test
    void 예약_취소_시도_시_예약_검색이_안된다면_예외를_던진다() {
        // given
        when(hairShopReservationRepository.findByIdAndCustomerEmail(41L, "abc@gmail.com"))
                .thenThrow(CustomException.class);

        // when, then
        assertThatThrownBy(() -> hairShopReservationService
                .cancelReservation(41L, "abc@gmail.com", LocalDateTime.now()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void 예약_시간_임박_하여_예약_취소_시도_시_예약_취소_불가_예외를_던진다() {
        // given
        HairShopReservation hairShopReservation = mock(HairShopReservation.class);
        when(hairShopReservationRepository.findByIdAndCustomerEmail(41L, "abc@gmail.com"))
                .thenReturn(Optional.of(hairShopReservation));
        doThrow(CustomException.class).when(hairShopReservation).cancel(any());

        // when, then
        assertThatThrownBy(() -> hairShopReservationService
                .cancelReservation(41L, "abc@gmail.com", LocalDateTime.now()))
                .isInstanceOf(CustomException.class);
    }

}
