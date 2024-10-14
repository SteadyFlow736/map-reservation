package org.example.mapreservation.reservation.application.service;

import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationStatusGetRequest;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface HairShopReservationService {
    HairShopReservationCreateResponse createReservation(Long shopId,
                                                        String username,
                                                        LocalDateTime currentDateTime,
                                                        HairShopReservationCreateRequest request);

    ReservationStatus getReservationStatus(Long shopId, HairShopReservationStatusGetRequest request);

    HairShopReservationResponse getReservation(Long reservationId, String username);

    Slice<HairShopReservationResponse> getReservations(String username, Pageable pageable);

    void cancelReservation(Long reservationId, String username, LocalDateTime currentDateTime);
}
