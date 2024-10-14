package org.example.mapreservation.reservation.infrastructure;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.application.repository.HairShopReservationRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class HairShopReservationRepositoryImpl implements HairShopReservationRepository {

    private final HairShopReservationJpaRepository hairShopReservationJpaRepository;

    @Override
    public HairShopReservation save(HairShopReservation hairShopReservation) {
        return hairShopReservationJpaRepository.save(hairShopReservation);
    }

    @Override
    public Optional<HairShopReservation> findByHairShopAndReservationTimeAndReservationStatus(HairShop hairShop, LocalDateTime reservationDateTime, HairShopReservation.Status status) {
        return hairShopReservationJpaRepository.findByHairShopAndReservationTimeAndReservationStatus(hairShop, reservationDateTime, status);
    }

    @Override
    public List<HairShopReservation> findByHairShopAndReservationTimeBetween(HairShop hairShop, LocalDateTime start, LocalDateTime end) {
        return hairShopReservationJpaRepository.findByHairShopAndReservationTimeBetween(hairShop, start, end);
    }

    @Override
    public Optional<HairShopReservation> findByIdAndCustomerEmail(Long hairShopId, String email) {
        return hairShopReservationJpaRepository.findByIdAndCustomerEmail(hairShopId, email);
    }

    @Override
    public Slice<HairShopReservation> findByCustomerEmail(String email, Pageable pageable) {
        return hairShopReservationJpaRepository.findByCustomerEmail(email, pageable);
    }
}
