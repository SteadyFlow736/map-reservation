package org.example.mapreservation.reservation.application;

import org.example.mapreservation.customer.application.repository.CustomerRepository;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.hairshop.application.repository.HairShopRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.application.repository.HairShopReservationRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

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

    HairShop persistedHairShop;
    Customer persistedCustomer;

    @BeforeEach
    void setUp() {
        persistedHairShop = hairShopRepository.findById(1L).orElseThrow();
        persistedCustomer = customerRepository.findByEmail("abc@gmail.com").orElseThrow();
    }

    /**
     * Feature: 사용자는 헤어샵 시술을 예약할 수 있다.
     * <p>
     * Given<br>
     * - 예약 시간: 2024.10.16 10시 00분<br>
     * - 예약 신청 시간: 2024.10.16 8시 00분<br>
     * When: 예약 신청<br>
     * Then: 예약 성공<br>
     */
    @Test
    void 사용자는_헤어샵_시술을_예약할_수_있다() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 16, 10, 0);
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 16, 9, 0);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        // when
        HairShopReservationCreateResponse response = hairShopReservationServiceImpl
                .createReservation(1L, "abc@gmail.com", currentTime, request);

        // then
        HairShopReservation hairShopReservation = hairShopReservationRepository.findById(response.reservationId()).orElseThrow();
        assertThat(hairShopReservation.getHairShop().getId()).isEqualTo(1L);
        assertThat(hairShopReservation.getCustomer().getId()).isEqualTo(1L);
        assertThat(hairShopReservation.getReservationTime())
                .isEqualTo(LocalDateTime.of(2024, 10, 16, 10, 0));
        assertThat(hairShopReservation.getReservationStatus()).isEqualTo(HairShopReservation.Status.RESERVED);
    }

    /**
     * 헤어샵 예약 동시성 테스트<br>
     * 같은 헤어샵에 같은 시간으로 예약을 중복 시도하면 한 건만 예약된다.
     * <p>
     * Given<br>
     * - 예약 시간: 2024.10.16 10시 00분
     * - 예약 신청 시간: 2024.10.16 8시 00분<br>
     * When: 100명이 헤당 슬롯에 동시에 예약 신청<br>
     * Then: 정확히 한 건만 예약 성공, 나머지는 예약 실패해야 함
     */
    @Test
    void 같은_헤어샵_같은_시간으로_예약을_중복_시도하면_한_건만_예약된다() throws InterruptedException {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 16, 10, 0);
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 16, 9, 0);
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
                    HairShopReservationCreateResponse response = hairShopReservationServiceImpl
                            .createReservation(1L, "abc@gmail.com", currentTime, request);
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
     * 예약 시간은 예약 신청 시간보다 미래여야 한다. 아니라면 예외를 발생시킨다.
     * <p>
     * Given: 예약 시간과 예약 신청 시간 준비<br>
     * - 예약 시간: 2024.10.16 10시 00분<br>
     * - 예약 신청 시간(현재 시간): 2024.10.16 10시 1분<br>
     * - 즉, 사용자가 10시 1분에 10시 00분 시간으로 예약을 신청하려 한다<br>
     * When: 예약 진행<br>
     * Then: 예외 발생<br>
     */
    @Test
    void 예약_시간은_예약을_신청하는_시간보다_미래여야_한다_아니라면_예외를_발생시킨다() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 16, 10, 0);
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 16, 10, 1);

        // when, then
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);
        assertThatThrownBy(() -> hairShopReservationServiceImpl
                .createReservation(1L, "abc@gmail.com", currentTime, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 지나간 시간으로 헤어샵을 예약할 수 없습니다.");
    }

    /**
     * 예약 시간은 0분 아니면 30분이어야 한다. 아니라면 예외를 발생시킨다.
     * <p>
     * Given<br>
     * - 예약 시간: 2024.10.16 10시 1분<br>
     * When: 예약 진행<br>
     * Then: 예외 발생<br>
     */
    @Test
    void 예약_시간은_0분_아니면_30분이어야_한다_아니라면_예외를_발생시킨다() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 16, 10, 1);
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 16, 9, 0);

        // when, then
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);
        assertThatThrownBy(() -> hairShopReservationServiceImpl
                .createReservation(1L, "abc@gmail.com", currentTime, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("예약 시간은 0분 또는 30분이어야 합니다.");
    }

    /**
     * 헤어샵을 예약하기 위해, 누구나 원하는 헤어샵의 특정 날짜의 예약 현황을 조회할 수 있다.
     * <p>
     * Given: 한 헤어샵의 2024.10.16의 예약 현황은 다음과 같다.<br>
     * - 10:00 예약<br>
     * - 14:30 예약<br>
     * - 15:30 예약 후 취소<br>
     * When: 해당 헤어샵의 2024.10.16 예약 현황 조회<br>
     * Then:<br>
     * - 10:00, 14:00에 예약 중임을 리턴<br>
     * - 나머지 시간은 예약 중이 아님을 리턴<br>
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
    }

    private HairShopReservation createHairShopReservation(LocalDateTime reservationDateTime) {
        return HairShopReservation.builder()
                .reservationTime(reservationDateTime)
                .hairShop(persistedHairShop)
                .customer(persistedCustomer)
                .build();
    }
}
