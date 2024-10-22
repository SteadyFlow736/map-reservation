package org.example.mapreservation.reservation.application.repository;

import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HairShopReservationRepository {

    HairShopReservation save(HairShopReservation hairShopReservation);

    List<HairShopReservation> saveAll(List<HairShopReservation> reservations);

    Optional<HairShopReservation> findById(Long hairShopId);

    Optional<HairShopReservation> findByIdAndCustomerEmail(Long hairShopId, String email);

    List<HairShopReservation> findByHairShopAndTargetDate(
            HairShop hairShop, LocalDate targetDate);

    Slice<HairShopReservation> findByCustomerEmail(String email, Pageable pageable);

    Optional<HairShopReservation> findByHairShopAndReservationTimeAndReservationStatus(
            HairShop hairShop, LocalDateTime reservationDateTime, HairShopReservation.Status status);

}
