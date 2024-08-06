package org.example.mapreservation.reservation.service;

import lombok.extern.slf4j.Slf4j;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.repository.CustomerRepository;
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

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        Owner owner = new Owner("주인1");
        ownerRepository.save(owner);
        HairShop hairShop = new HairShop("샵1", new Address("거리1", "101호"), owner);
        hairShopRepository.save(hairShop);
        this.hairShop = hairShop;
        Customer customer = new Customer("abc@gmail.com", "12345678");
        customerRepository.save(customer);
        this.customer = customer;
        log.info("beforeEach 끝");
    }

    @Test
    void givenValidReservationTime_thenReservationSucceeds() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, Month.APRIL, 10, 13, 30);
        LocalDateTime currentTime = reservationTime.minusHours(2);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        // when
        Long reservationId = reservationService.createHairShopReservationOptimistic(hairShop.getId(), customer.getEmail(), currentTime, request);

        // then
        Optional<HairShopReservation> foundReservation = reservationRepository.findById(reservationId);
        assertThat(foundReservation).isNotEmpty();
        assertThat(foundReservation.get().getHairShop().getId()).isEqualTo(hairShop.getId());
        assertThat(foundReservation.get().getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(foundReservation.get().getReservationTime()).isEqualTo(reservationTime);
    }

    @DisplayName("예약 성공하면 헤어샵의 version이 1 증가한다.")
    @Test
    void givenReservationSucceeds_thenHairShopVersionIncreases() {
        // given
        int version = hairShop.getVersion();
        LocalDateTime reservationTime = LocalDateTime.of(2024, Month.APRIL, 10, 13, 30);
        LocalDateTime currentTime = reservationTime.minusHours(2);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);


        // when
        reservationService.createHairShopReservationOptimistic(hairShop.getId(), customer.getEmail(), currentTime, request);

        // then
        HairShop updatedHairShop = hairShopRepository.findById(hairShop.getId()).orElseThrow();
        assertThat(updatedHairShop.getVersion()).isEqualTo(version + 1);
    }

    // TODO: 데드락 발생! 원인 찾고 해결하기!
    @Test
    void test() throws InterruptedException {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, Month.APRIL, 10, 13, 30);
        LocalDateTime currentTime = reservationTime.minusHours(2);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            es.submit(() -> {
                try {
                    reservationService.createHairShopReservationOptimistic(hairShop.getId(), customer.getEmail(), currentTime, request);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

}
