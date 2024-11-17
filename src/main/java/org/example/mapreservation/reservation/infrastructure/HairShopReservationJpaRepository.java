package org.example.mapreservation.reservation.infrastructure;

import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HairShopReservationJpaRepository extends JpaRepository<HairShopReservation, Long> {

    Optional<HairShopReservation> findByHairShopAndReservationTimeAndReservationStatus(
            HairShop hairShop, LocalDateTime reservationDateTime, HairShopReservation.Status status);

    List<HairShopReservation> findByHairShopAndReservationTimeBetween(
            HairShop hairShop, LocalDateTime start, LocalDateTime end);

    // N + 1 해결을 위해 fetch join 사용
    @Query("select hsr from HairShopReservation hsr" +
            " join fetch hsr.customer" +
            " join fetch hsr.hairShop" +
            " where hsr.id = :hairShopId and hsr.customer.email = :email")
    Optional<HairShopReservation> findByIdAndCustomerEmail(Long hairShopId, String email);

    // N + 1 해결을 위해 fetch join 사용
    @Query("select hsr from HairShopReservation hsr" +
            " join fetch hsr.customer" +
            " join fetch hsr.hairShop" +
            " where hsr.customer.email = :email")
    Slice<HairShopReservation> findByCustomerEmail(String email, Pageable pageable);
}
