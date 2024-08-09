package org.example.mapreservation.reservation.service;

import lombok.extern.slf4j.Slf4j;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.repository.CustomerRepository;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.dto.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.time.Month;
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
    HairShopRepository hairShopRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    CustomerRepository customerRepository;
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
        Customer customer = new Customer("abc@gmail.com", "12345678");
        customerRepository.save(customer);
        this.customer = customer;
    }

    @Test
    void givenValidReservationTime_thenReservationSucceeds() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, Month.APRIL, 10, 13, 30);
        LocalDateTime currentTime = reservationTime.minusHours(2);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        // when
        Long reservationId = reservationService.createHairShopReservation(hairShop.getId(), customer.getEmail(), currentTime, request);

        // then
        Optional<HairShopReservation> foundReservation = reservationRepository.findById(reservationId);
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
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        AtomicReference<Long> reservationId = new AtomicReference<>();
        CopyOnWriteArrayList<Exception> exceptions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            es.submit(() -> {
                try {
                    Long id = reservationService.createHairShopReservation(hairShop.getId(), customer.getEmail(), currentTime, request);
                    reservationId.set(id);
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
        assertThat(exceptions).allMatch(e -> e.getClass().equals(
                DataIntegrityViolationException.class) || e.getClass().equals(CustomException.class));
        // then - 성공된 예약 검증
        Long id = reservationId.get();
        Optional<HairShopReservation> foundReservation = reservationRepository.findById(id);
        assertThat(foundReservation).isNotEmpty();
        assertThat(foundReservation.get().getHairShop().getId()).isEqualTo(hairShop.getId());
        assertThat(foundReservation.get().getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(foundReservation.get().getReservationTime()).isEqualTo(reservationTime);
    }

}
