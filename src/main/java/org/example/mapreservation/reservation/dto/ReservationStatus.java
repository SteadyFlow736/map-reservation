package org.example.mapreservation.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.mapreservation.reservation.domain.HairShopReservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 가게 예약 현황
 *
 * @param date          기준 날짜
 * @param openingTime   영업 시작 시간
 * @param closingTime   영업 종료 시간
 * @param reservedTimes 예약된 시간 리스트
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

        List<LocalTime> times = reservations.stream()
                .map(r -> r.getReservationTime().toLocalTime()).toList();

        return new ReservationStatus(date, openingTime, closingTime, times);
    }
}
