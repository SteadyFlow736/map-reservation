package org.example.mapreservation.reservation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 가게 예약 현황
 *
 * @param date          기준 날짜
 * @param openingTime   영업 시작 시간
 * @param closingTime   영업 종료 시간
 * @param reservedTimes 예약 시간과 예약 상태
 */
public record ReservationStatus(
        LocalDate date,

        @JsonFormat(pattern = "HH:mm")
        LocalTime openingTime,

        @JsonFormat(pattern = "HH:mm")
        LocalTime closingTime,

        @JsonFormat(pattern = "HH:mm")
        List<LocalTime> reservedTimes) {

    public static ReservationStatus from(
            LocalDate date, LocalTime openingTime, LocalTime closingTime,
            List<HairShopReservation> reservations) {

        List<LocalTime> reservedTimes = reservations.stream()
                .filter(r -> r.getReservationStatus().equals(HairShopReservation.Status.RESERVED))
                .map(r -> r.getReservationTime().toLocalTime()).toList();

        return new ReservationStatus(date, openingTime, closingTime, reservedTimes);
    }
}
