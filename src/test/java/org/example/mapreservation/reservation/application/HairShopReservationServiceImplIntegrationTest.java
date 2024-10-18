package org.example.mapreservation.reservation.application;

import org.example.mapreservation.customer.application.repository.CustomerRepository;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.hairshop.application.repository.HairShopRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.application.repository.HairShopReservationRepository;
import org.example.mapreservation.reservation.application.service.TimeProvider;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationResponse;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SqlGroup({
        @Sql(value = "/sql/delete-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/owner-and-hairshop-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/customer-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@SpringBootTest
class HairShopReservationServiceImplIntegrationTest {

    @Autowired
    private HairShopReservationServiceImpl hairShopReservationServiceImpl;
    @Autowired
    private HairShopReservationRepository hairShopReservationRepository;
    @Autowired
    private HairShopRepository hairShopRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @MockBean
    private TimeProvider timeProvider;

    HairShop persistedHairShop;
    Customer persistedCustomer;

    @BeforeEach
    void setUp() {
        persistedHairShop = hairShopRepository.findById(1L).orElseThrow();
        persistedCustomer = customerRepository.findByEmail("abc@gmail.com").orElseThrow();
    }

    /**
     * <pre style="font-size: 11px;">
     * 사용자는 헤어샵 시술을 예약할 수 있다.
     * Given
     * - 예약 시간: 2024.10.16 10시 00분
     * - 예약 신청 시간: 2024.10.16 8시 00분
     * When: 예약 신청
     * Then: 예약 성공
     * </pre>
     */
    @Test
    void 사용자는_헤어샵_시술을_예약할_수_있다() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 16, 10, 0);
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 16, 9, 0);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        when(timeProvider.getCurrentDateTime()).thenReturn(currentTime);

        // when
        HairShopReservationCreateResponse response = hairShopReservationServiceImpl
                .createReservation(1L, "abc@gmail.com", request);

        // then
        HairShopReservation hairShopReservation = hairShopReservationRepository.findById(response.reservationId()).orElseThrow();
        assertThat(hairShopReservation.getHairShop().getId()).isEqualTo(1L);
        assertThat(hairShopReservation.getCustomer().getId()).isEqualTo(1L);
        assertThat(hairShopReservation.getReservationTime())
                .isEqualTo(LocalDateTime.of(2024, 10, 16, 10, 0));
        assertThat(hairShopReservation.getReservationStatus()).isEqualTo(HairShopReservation.Status.RESERVED);
    }

    /**
     * <pre style="font-size: 11px;">
     * 헤어샵 예약 동시성 테스트
     * 같은 헤어샵에 같은 시간으로 예약을 중복 시도하면 한 건만 예약된다.
     * Given
     * - 예약 시간: 2024.10.16 10시 00분
     * - 예약 신청 시간: 2024.10.16 8시 00분
     * When: 100명이 헤당 슬롯에 동시에 예약 신청
     * Then: 정확히 한 건만 예약 성공, 나머지는 예약 실패해야 함
     * </pre>
     */
    @Test
    void 같은_헤어샵_같은_시간으로_예약을_중복_시도하면_한_건만_예약된다() throws InterruptedException {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 16, 10, 0);
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 16, 9, 0);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        when(timeProvider.getCurrentDateTime()).thenReturn(currentTime);

        // when - 동일한 예약 정보로 동시에 예약 시도
        int threadCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        AtomicReference<Long> reservationId = new AtomicReference<>();
        CopyOnWriteArrayList<Exception> exceptions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            es.submit(() -> {
                try {
                    HairShopReservationCreateResponse response = hairShopReservationServiceImpl
                            .createReservation(1L, "abc@gmail.com", request);
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
        assertThat(exceptions).allMatch(e ->
                e.getClass().equals(CustomException.class)
                        && e.getMessage().equals("이미 예약된 시간입니다."));
        // then - 성공된 예약 검증
        Long id = reservationId.get();
        HairShopReservation hairShopReservation = hairShopReservationRepository.findById(id).orElseThrow();
        assertThat(hairShopReservation.getHairShop().getId()).isEqualTo(1L);
        assertThat(hairShopReservation.getCustomer().getId()).isEqualTo(1L);
        assertThat(hairShopReservation.getReservationTime())
                .isEqualTo(LocalDateTime.of(2024, 10, 16, 10, 0));
    }

    /**
     * <pre style="font-size: 11px;">
     * 예약 시간은 예약 신청 시간보다 미래여야 한다. 아니라면 예외를 발생시킨다.
     * Given: 예약 시간과 예약 신청 시간 준비
     * - 예약 시간: 2024.10.16 10시 00분
     * - 예약 신청 시간(현재 시간): 2024.10.16 10시 1분
     * - 즉, 사용자가 10시 1분에 10시 00분 시간으로 예약을 신청하려 한다
     * When: 예약 진행
     * Then: 예외 발생
     * </pre>
     */
    @Test
    void 예약_시간은_예약을_신청하는_시간보다_미래여야_한다_아니라면_예외를_발생시킨다() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 16, 10, 0);
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 16, 10, 1);
        when(timeProvider.getCurrentDateTime()).thenReturn(currentTime);

        // when, then
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);
        assertThatThrownBy(() -> hairShopReservationServiceImpl
                .createReservation(1L, "abc@gmail.com", request))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 지나간 시간으로 헤어샵을 예약할 수 없습니다.");
    }

    /**
     * <pre style="font-size: 11px;">
     * 예약 시간은 0분 아니면 30분이어야 한다. 아니라면 예외를 발생시킨다.
     * Given
     * - 예약 시간: 2024.10.16 10시 1분
     * When: 예약 진행
     * Then: 예외 발생
     * </pre>
     */
    @Test
    void 예약_시간은_0분_아니면_30분이어야_한다_아니라면_예외를_발생시킨다() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 16, 10, 1);
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 16, 9, 0);
        when(timeProvider.getCurrentDateTime()).thenReturn(currentTime);

        // when, then
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);
        assertThatThrownBy(() -> hairShopReservationServiceImpl
                .createReservation(1L, "abc@gmail.com", request))
                .isInstanceOf(CustomException.class)
                .hasMessage("예약 시간은 0분 또는 30분이어야 합니다.");
    }

    /**
     * <pre style="font-size: 11px;">
     * 예약 가능한 시간을 둘러보기 위해, 누구나 원하는 헤어샵의 특정 날짜의 예약 현황을 조회할 수 있다.
     * Given: 한 헤어샵의 2024.10.16의 예약 현황은 다음과 같다.
     * - 10:00 예약
     * - 14:30 예약
     * - 15:30 예약 후 취소
     * When: 해당 헤어샵의 2024.10.16 예약 현황 조회
     * Then:
     * - 10:00, 14:00에 예약 중임을 리턴
     * - 나머지 시간은 예약 중이 아님을 리턴
     * </pre>
     */
    @Test
    void 원하는_헤어샵의_특정_날짜의_예약_현황을_조회할_수_있다() {
        // given
        List<HairShopReservation> reservations = List.of(
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 10, 0)),
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 14, 30)),
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 15, 30))
        );
        reservations.get(2).cancel(LocalDateTime.of(2024, 10, 16, 15, 30).minusDays(1));
        reservations = hairShopReservationRepository.saveAll(reservations);

        // when
        ReservationStatus status = hairShopReservationServiceImpl
                .getReservationStatus(persistedHairShop.getId(), LocalDate.of(2024, 10, 16));

        // then - 해당 날짜의 예약 현황 리턴
        assertThat(status.date()).isEqualTo(LocalDate.of(2024, 10, 16));
        assertThat(status.reservedTimes().size()).isEqualTo(2);
        assertThat(status.reservedTimes()).containsExactlyInAnyOrder(
                LocalTime.of(10, 0),
                LocalTime.of(14, 30)
        );
    }

    /**
     * <pre style="font-size: 11px;">
     * 유저는 특정 예약에 대한 상세 정보를 조회할 수 있다
     * Given
     * - 성공적인 예약
     * - 예약 id
     * When: 예약에 대한 정보 요청
     * Then: 예약에 대한 정보 제공
     * </pre>
     */
    @Test
    void 유저는_특정_예약에_대한_상세_정보를_조회할_수_있다() {
        // given
        HairShopReservation hairShopReservation =
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 10, 0));
        Long reservationId = hairShopReservationRepository.save(hairShopReservation).getId();

        // when
        HairShopReservationResponse response =
                hairShopReservationServiceImpl.getReservation(reservationId, "abc@gmail.com");

        // then
        assertThat(response.reservationId()).isEqualTo(reservationId);
        assertThat(response.username()).isEqualTo("abc@gmail.com");
        assertThat(response.status()).isEqualTo(HairShopReservation.Status.RESERVED);
        assertThat(response.reservationTime()).isEqualTo(
                LocalDateTime.of(2024, 10, 16, 10, 0));
        assertThat(response.hairShopResponse().shopId()).isEqualTo(persistedHairShop.getId());
    }

    /**
     * <pre style="font-size: 11px;">
     * 유저는 존재하지 않는 예약이나, 다른 사람의 예약에 대한 상세 정보를 조회할 수 없다.
     * Given
     * - 다른 사람의 예약 존재
     * When
     * - 해당 예약 조회 시도
     * Then
     * - 조회 불가
     * </pre>
     */
    @Test
    void 유저는_존재하지_않는_예약이나_다른_사람의_예약에_대한_상세_정보를_조회할_수_없다() {
        // given
        Customer anotherCustomer = new Customer(1L, "zxc@gmail.com", "Password1!");
        customerRepository.save(anotherCustomer);
        HairShopReservation reservationByAnotherCustomer = HairShopReservation.builder()
                .reservationTime(LocalDateTime.of(2024, 10, 16, 10, 0))
                .hairShop(persistedHairShop)
                .customer(anotherCustomer)
                .build();
        Long reservationId = hairShopReservationRepository.save(reservationByAnotherCustomer).getId();

        // when, then
        assertThatThrownBy(() -> hairShopReservationServiceImpl
                .getReservation(reservationId, "abc@gmail.com"))
                .isInstanceOf(CustomException.class)
                .hasMessage("일치하는 예약이 없습니다.");
    }

    /**
     * <pre style="font-size: 11px;">
     * 유저는 자신의 예약 리스트를 슬라이스로 조회할 수 있다.
     * Given
     * - 유저는 다음과 같은 예약 존재
     *      - 2024.10.16 10:00
     *      - 2024.10.16 14:30
     *      - 2024.10.16 15:30
     *      - 2024.10.16 17:30
     * - 다음의 페이지 정보 제공
     *      - 페이지 번호 0
     *      - 페이지 크기 2
     *      - 예약 시간을 기준으로 내림차순
     * When: 예약 조회 시도
     * Then: 다음의 예약 리스트(슬라이스)를 제공받음
     * - 2024.10.16 17:30 의 예약 정보
     * - 2024.10.16 15:30 의 예약 정보
     * </pre>
     */
    @Test
    void 유저는_자신의_예약_리스트를_조회할_수_있다() {
        // given
        List<HairShopReservation> reservations = List.of(
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 10, 0)),
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 14, 30)),
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 15, 30)),
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 17, 30))
        );
        hairShopReservationRepository.saveAll(reservations);

        int pageNumber = 0;
        int pageSize = 2;
        Sort sort = Sort.by(Sort.Direction.DESC, "reservationTime");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // when
        Slice<HairShopReservationResponse> responseSlice =
                hairShopReservationServiceImpl.getReservations("abc@gmail.com", pageable);

        // then
        assertThat(responseSlice.getNumber()).isEqualTo(0);
        assertThat(responseSlice.getSize()).isEqualTo(2);
        assertThat(responseSlice.getContent().get(0).reservationTime())
                .isEqualTo(LocalDateTime.of(2024, 10, 16, 17, 30));
        assertThat(responseSlice.getContent().get(1).reservationTime())
                .isEqualTo(LocalDateTime.of(2024, 10, 16, 15, 30));
        assertThat(responseSlice.hasNext()).isEqualTo(true);
    }

    /**
     * <pre style="font-size: 11px;">
     * 유저는 예약 시간 30분 전까지는 예약을 취소할 수 있다.
     * Given
     * - 유저는 다음과 같은 예약 존재
     *      - 2024.10.16 14:30
     * When: 2024.10.16 14:00에 해당 예약 취소 시도
     * Then: 예약 취소 성공
     * </pre>
     */
    @Test
    void 유저는_예약_시간_30분_전까지는_예약을_취소할_수_있다() {
        // given
        HairShopReservation hairShopReservation =
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 14, 30));
        Long reservationId = hairShopReservationRepository.save(hairShopReservation).getId();

        // when
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 10, 16, 14, 0);
        hairShopReservationServiceImpl.cancelReservation(reservationId, "abc@gmail.com", currentDateTime);

        // then
        HairShopReservation foundReservation = hairShopReservationRepository.findById(reservationId).orElseThrow();
        assertThat(foundReservation.getReservationStatus()).isEqualTo(HairShopReservation.Status.CANCELLED);
    }

    /**
     * <pre style="font-size: 11px;">
     * 유저는 예약 시간 29분 전부터는 예약을 취소할 수 없다.
     * Given
     * - 유저는 다음과 같은 예약 존재
     *      - 2024.10.16 14:30
     * When: 2024.10.16 14:01에 해당 예약 취소 시도
     * Then:
     * - 예약 취소 실패
     * - 예약 상태가 변하지 않음
     * </pre>
     */
    @Test
    void 유저는_예약_시간_29분_전부터는_예약을_취소할_수_없다() {
        // given
        HairShopReservation hairShopReservation =
                createHairShopReservation(LocalDateTime.of(2024, 10, 16, 14, 30));
        Long reservationId = hairShopReservationRepository.save(hairShopReservation).getId();

        // when, then
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 10, 16, 14, 1);
        assertThatThrownBy(() -> hairShopReservationServiceImpl
                .cancelReservation(reservationId, "abc@gmail.com", currentDateTime))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 지나간 예약이므로 취소할 수 없습니다.: 최소 30분 전에 취소할 수 있습니다.");

        // then
        HairShopReservation foundReservation = hairShopReservationRepository.findById(reservationId).orElseThrow();
        assertThat(foundReservation.getReservationStatus()).isEqualTo(HairShopReservation.Status.RESERVED);
    }

    private HairShopReservation createHairShopReservation(LocalDateTime reservationDateTime) {
        return HairShopReservation.builder()
                .reservationTime(reservationDateTime)
                .hairShop(persistedHairShop)
                .customer(persistedCustomer)
                .build();
    }
}
