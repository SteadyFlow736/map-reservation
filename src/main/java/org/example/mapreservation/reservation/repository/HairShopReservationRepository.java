package org.example.mapreservation.reservation.repository;

import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HairShopReservationRepository extends JpaRepository<HairShopReservation, Long> {
    Optional<HairShopReservation> findByHairShopAndReservationTime(
            HairShop hairShop, LocalDateTime localDateTime);

    List<HairShopReservation> findByHairShopAndReservationTimeBetween(
            HairShop hairShop, LocalDateTime start, LocalDateTime end);

    @Query("select hsr from HairShopReservation hsr where hsr.id = :hairShopId and hsr.customer.email = :customerEmail")
    Optional<HairShopReservation> findByHairShopAndCustomer(
            @Param("hairShopId") Long hairShopId, @Param("customerEmail") String customerEmail);

}
