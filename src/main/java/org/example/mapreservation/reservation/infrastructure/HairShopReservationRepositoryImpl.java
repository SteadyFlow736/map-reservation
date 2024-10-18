package org.example.mapreservation.reservation.infrastructure;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.application.repository.HairShopReservationRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public List<HairShopReservation> saveAll(List<HairShopReservation> reservations) {
        return hairShopReservationJpaRepository.saveAll(reservations);
    }

    @Override
    public Optional<HairShopReservation> findById(Long hairShopId) {
        return hairShopReservationJpaRepository.findById(hairShopId);
    }

    @Override
    public Optional<HairShopReservation> findByIdAndCustomerEmail(Long hairShopId, String email) {
        return hairShopReservationJpaRepository.findByIdAndCustomerEmail(hairShopId, email);
    }

    @Override
    public List<HairShopReservation> findByHairShopAndTargetDate(HairShop hairShop, LocalDate targetDate) {
        return hairShopReservationJpaRepository.findByHairShopAndReservationTimeBetween(
                hairShop, targetDate.atStartOfDay(), targetDate.atTime(LocalTime.MAX));
    }

    @Override
    public Slice<HairShopReservation> findByCustomerEmail(String email, Pageable pageable) {
        return hairShopReservationJpaRepository.findByCustomerEmail(email, pageable);
    }

    @Override
    public Optional<HairShopReservation> findByHairShopAndReservationTimeAndReservationStatus(
            HairShop hairShop, LocalDateTime reservationDateTime, HairShopReservation.Status status) {
        return hairShopReservationJpaRepository
                .findByHairShopAndReservationTimeAndReservationStatus(hairShop, reservationDateTime, status);
    }
}
