package org.example.mapreservation.reservation.repository;

import jakarta.persistence.EntityManager;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.repository.CustomerRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class HairShopReservationRepositoryTest {

    @Autowired
    HairShopReservationRepository hairShopReservationRepository;
    @Autowired
    HairShopRepository hairShopRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    EntityManager em;

    Customer customer;
    HairShop hairShop;

    @BeforeEach
    void beforeEach() {
        Owner owner = new Owner("주인1");
        ownerRepository.save(owner);
        HairShop hairShop = new HairShop("샵1", new Address("거리1", "101호"), owner);
        hairShopRepository.save(hairShop);
        this.hairShop = hairShop;

        Customer customer = new Customer("abc@gmail.com", "12345678");
        customerRepository.save(customer);
        this.customer = customer;
    }

    @Transactional
    @Test
    void findByHairShopAndReservationTimeTest() {
        // given
        LocalDateTime reservationTime = LocalDateTime.of(2024, Month.APRIL, 10, 13, 30);
        HairShopReservation hairShopReservation = new HairShopReservation(customer, hairShop, reservationTime);
        hairShopReservationRepository.save(hairShopReservation);
        em.flush();
        em.clear();

        // when
        Optional<HairShopReservation> reservation = hairShopReservationRepository.findByHairShopAndReservationTime(hairShop, reservationTime);

        // then
        assertThat(reservation).isNotEmpty();
        assertThat(reservation.get().getId()).isEqualTo(hairShopReservation.getId());
        assertThat(reservation.get().getReservationTime()).isEqualTo(reservationTime);
        assertThat(reservation.get().getHairShop().getId()).isEqualTo(hairShop.getId());
        assertThat(reservation.get().getCustomer().getId()).isEqualTo(customer.getId());
    }

}
