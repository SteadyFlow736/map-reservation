package org.example.mapreservation.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.repository.CustomerRepository;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.dto.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ReservationService {

    private final HairShopReservationRepository hairShopReservationRepository;
    private final HairShopRepository hairShopRepository;
    private final CustomerRepository customerRepository;

    public Long createHairShopReservation(Long shopId, String username, LocalDateTime currentTime,
                                          HairShopReservationCreateRequest request) {

        isValidReservationTime(currentTime, request.reservationTime());

        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCode.CUST_NOT_FOUND));

        HairShop hairShop = hairShopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));

        HairShopReservation hairShopReservation =
                new HairShopReservation(customer, hairShop, request.reservationTime());

        hairShopReservationRepository.findByHairShopAndReservationTime(hairShop, request.reservationTime())
                .ifPresent(r -> {
                    throw new CustomException(ErrorCode.HSR_ALREADY_TAKEN_RESERVATION_TIME);
                });

        return hairShopReservationRepository.save(hairShopReservation).getId();
    }

    private static void isValidReservationTime(LocalDateTime currentTime, LocalDateTime reservationTime) {
        if (currentTime.isAfter(reservationTime)) {
            throw new CustomException(ErrorCode.HSR_OLD_RESERVATION_TIME);
        }
        List<Integer> allowedMinutes = List.of(0, 30);
        if (!allowedMinutes.contains(reservationTime.getMinute())) {
            throw new CustomException(ErrorCode.HSR_INVALID_RESERVATION_TIME);
        }
        if (reservationTime.getSecond() != 0 || reservationTime.getNano() != 0) {
            throw new CustomException(ErrorCode.HSR_INVALID_RESERVATION_TIME);
        }
    }
}
