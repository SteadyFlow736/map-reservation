package org.example.mapreservation.reservation.repository;

import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HairShopReservationRepository extends JpaRepository<HairShopReservation, Long> {

    Optional<HairShopReservation> findByHairShopAndReservationTimeAndReservationStatus(
            HairShop hairShop, LocalDateTime reservationDateTime, HairShopReservation.Status status);

    List<HairShopReservation> findByHairShopAndReservationTimeBetween(
            HairShop hairShop, LocalDateTime start, LocalDateTime end);

    Optional<HairShopReservation> findByIdAndCustomerEmail(Long hairShopId, String email);

    Slice<HairShopReservation> findByCustomerEmail(String email, Pageable pageable);
}
