package org.example.mapreservation.reservation.domain;

import org.example.mapreservation.hairshop.domain.HairShopResponse;

import java.time.LocalDateTime;

/**
 * 헤어샵 예약 정보
 *
 * @param reservationId   예약 id
 * @param username        예약 유저명
 * @param hairShopResponse     헤어샵 정보
 * @param reservationTime 예약 시간
 * @param status          예약 상태
 */
public record HairShopReservationResponse(
        Long reservationId,
        String username,
        HairShopResponse hairShopResponse,
        LocalDateTime reservationTime,
        HairShopReservation.Status status
) {
    public static HairShopReservationResponse from(
            HairShopReservation hairShopReservation) {

        return new HairShopReservationResponse(
                hairShopReservation.getId(),
                hairShopReservation.getCustomer().getEmail(),
                HairShopResponse.from(hairShopReservation.getHairShop()),
                hairShopReservation.getReservationTime(),
                hairShopReservation.getReservationStatus()
        );
    }
}
