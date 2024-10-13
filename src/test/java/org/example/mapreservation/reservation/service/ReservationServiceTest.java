package org.example.mapreservation.reservation.service;

import lombok.extern.slf4j.Slf4j;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.infrastructure.CustomerJpaRepository;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.infrastructure.HairShopJpaRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.dto.CreateHairShopReservationResponse;
import org.example.mapreservation.reservation.dto.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.dto.HairShopReservationDto;
import org.example.mapreservation.reservation.dto.HairShopReservationStatusGetRequest;
import org.example.mapreservation.reservation.dto.ReservationStatus;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    HairShopJpaRepository hairShopRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    CustomerJpaRepository customerRepository;
    @Autowired
    ReservationService reservationService;
    @Autowired
    HairShopReservationRepository reservationRepository;

    Customer customer;
    HairShop hairShop;

    @BeforeEach
    void beforeEach() {
        log.info("beforeEach 시작");
        cleanUp();
        setInitData();
        log.info("beforeEach 끝");
    }

    private void cleanUp() {
        reservationRepository.deleteAll();
        customerRepository.deleteAll();
        hairShopRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    private void setInitData() {
        Owner owner = new Owner("주인1");
        ownerRepository.save(owner);
        HairShop hairShop = new HairShop("샵1", new Address("거리1", "101호"), owner);
        hairShopRepository.save(hairShop);
        this.hairShop = hairShop;
        Customer customer = Customer.builder()
                .email("abc@gmail.com")
                .password("12345678")
                .build();        customerRepository.save(customer);
        this.customer = customer;
    }

    @Test
    void givenValidReservationTime_thenReservationSucceeds() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, Month.APRIL, 10, 13, 30);
        LocalDateTime currentTime = reservationTime.minusHours(2);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        // when
        CreateHairShopReservationResponse response = reservationService.createHairShopReservation(hairShop.getId(), customer.getEmail(), currentTime, request);

        // then
        Optional<HairShopReservation> foundReservation = reservationRepository.findById(response.reservationId());
        assertThat(foundReservation).isNotEmpty();
        assertThat(foundReservation.get().getHairShop().getId()).isEqualTo(hairShop.getId());
        assertThat(foundReservation.get().getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(foundReservation.get().getReservationTime()).isEqualTo(reservationTime);
    }

    @DisplayName("같은 헤어샵, 같은 날짜로 예약을 중복 시도하면 한 건만 예약된다.")
    @Test
    void givenMultipleSameReservation_thenOnlyOneSucceeds() throws InterruptedException {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, Month.APRIL, 10, 13, 30);
        LocalDateTime currentTime = reservationTime.minusHours(2);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        // when - 동일한 예약 정보로 동시에 예약 시도
        int threadCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        AtomicReference<Long> reservationId = new AtomicReference<>();
        CopyOnWriteArrayList<Exception> exceptions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            es.submit(() -> {
                try {
                    CreateHairShopReservationResponse response = reservationService.createHairShopReservation(hairShop.getId(), customer.getEmail(), currentTime, request);
                    reservationId.set(response.reservationId());
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then - 실패한 예약 검증
        assertThat(exceptions.size()).isEqualTo(threadCount - 1);
        assertThat(exceptions).allMatch(e -> e.getClass().equals(CustomException.class));
        // then - 성공된 예약 검증
        Long id = reservationId.get();
        Optional<HairShopReservation> foundReservation = reservationRepository.findById(id);
        assertThat(foundReservation).isNotEmpty();
        assertThat(foundReservation.get().getHairShop().getId()).isEqualTo(hairShop.getId());
        assertThat(foundReservation.get().getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(foundReservation.get().getReservationTime()).isEqualTo(reservationTime);
    }

    @DisplayName("고객은 헤어샵의 특정 날짜의 예약 현황을 조회할 수 있다.")
    @Test
    void getHairShopReservationStatus() {
        // given - 한 헤어샵에 여러 시간으로 예약 진행
        List<LocalDateTime> reservationDateTimes = List.of(
                LocalDateTime.of(2024, Month.AUGUST, 3, 13, 30),
                LocalDateTime.of(2024, Month.AUGUST, 4, 13, 30),
                LocalDateTime.of(2024, Month.AUGUST, 4, 15, 30),
                LocalDateTime.of(2024, Month.AUGUST, 5, 15, 30)
        );
        List<HairShopReservationCreateRequest> requests = reservationDateTimes.stream()
                .map(HairShopReservationCreateRequest::new).toList();
        LocalDateTime now = reservationDateTimes.get(0).minusHours(1);
        requests.forEach(r -> reservationService.createHairShopReservation(hairShop.getId(), customer.getEmail(), now, r));

        // when - 해당 헤어샵의 특정 날짜로 예약 현황 조회
        LocalDate targetDate = reservationDateTimes.get(1).toLocalDate();
        HairShopReservationStatusGetRequest statusGetRequest =
                new HairShopReservationStatusGetRequest(targetDate);
        ReservationStatus status = reservationService.getHairShopReservationStatus(hairShop.getId(), statusGetRequest);

        // then - 해당 날짜의 예약 현황 리턴
        assertThat(status.date()).isEqualTo(targetDate);
        assertThat(status.timeAndStatuses().size()).isEqualTo(2);
        assertThat(status.timeAndStatuses().stream().map(ReservationStatus.TimeAndStatus::time)).isEqualTo(
                List.of(reservationDateTimes.get(1).toLocalTime(), reservationDateTimes.get(2).toLocalTime()));
    }

    @DisplayName("고객은 자신이 한 특정 헤어샵 예약을 조회할 수 있다.")
    @Test
    void getHairShopReservation() {
        // given - 한 헤어샵에 여러 시간으로 예약 진행
        List<LocalDateTime> reservationDateTimes = List.of(
                LocalDateTime.of(2024, Month.AUGUST, 3, 13, 30),
                LocalDateTime.of(2024, Month.AUGUST, 4, 13, 30),
                LocalDateTime.of(2024, Month.AUGUST, 4, 15, 30),
                LocalDateTime.of(2024, Month.AUGUST, 5, 15, 30)
        );
        List<HairShopReservationCreateRequest> requests = reservationDateTimes.stream()
                .map(HairShopReservationCreateRequest::new).toList();
        LocalDateTime now = reservationDateTimes.get(0).minusHours(1);
        List<Long> reservationIds = new ArrayList<>();
        requests.forEach(r -> {
            Long id = reservationService.createHairShopReservation(hairShop.getId(), customer.getEmail(), now, r).reservationId();
            reservationIds.add(id);
        });

        // when - 특정 예약 조회
        Long reservationId = reservationIds.get(0);
        HairShopReservationDto foundReservationDto = reservationService.getHairShopReservation(reservationId, customer.getEmail());

        // then - 예약 확인
        assertThat(foundReservationDto.hairShopResponse().shopId()).isEqualTo(hairShop.getId());
        assertThat(foundReservationDto.reservationId()).isEqualTo(reservationId);
        assertThat(foundReservationDto.username()).isEqualTo(customer.getEmail());
        assertThat(foundReservationDto.reservationTime()).isEqualTo(reservationDateTimes.get(0));
    }

    @DisplayName("고객은 자신의 예약 리스트를 조회할 수 있다.")
    @Test
    void getHairShopReservations() {
        // given - 한 헤어샵에 여러 시간으로 예약 진행
        List<LocalDateTime> reservationDateTimes = List.of(
                LocalDateTime.of(2024, Month.AUGUST, 3, 13, 30),
                LocalDateTime.of(2024, Month.AUGUST, 4, 13, 30),
                LocalDateTime.of(2024, Month.AUGUST, 4, 15, 30),
                LocalDateTime.of(2024, Month.AUGUST, 5, 15, 30)
        );
        List<HairShopReservationCreateRequest> requests = reservationDateTimes.stream()
                .map(HairShopReservationCreateRequest::new).toList();
        LocalDateTime now = reservationDateTimes.get(0).minusHours(1);
        requests.forEach(r -> {
            reservationService.createHairShopReservation(hairShop.getId(), customer.getEmail(), now, r);
        });

        // when - 특정 예약 조회
        int pageNumber = 0;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "reservationTime")
        );
        Slice<HairShopReservationDto> foundReservationDtos = reservationService.getHairShopReservations(customer.getEmail(), pageable);

        // then - 예약 확인
        assertThat(foundReservationDtos.getNumber()).isEqualTo(pageNumber);
        assertThat(foundReservationDtos.getSize()).isEqualTo(pageSize);
        assertThat(foundReservationDtos.getContent().get(0).reservationTime()).isEqualTo(reservationDateTimes.get(3));
        assertThat(foundReservationDtos.getContent().get(1).reservationTime()).isEqualTo(reservationDateTimes.get(2));
        assertThat(foundReservationDtos.hasNext()).isEqualTo(true);
    }

}
