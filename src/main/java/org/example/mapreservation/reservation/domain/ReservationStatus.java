package org.example.mapreservation.reservation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 가게 예약 현황
 *
 * @param date                기준 날짜
 * @param openingTime         영업 시작 시간
 * @param closingTime         영업 종료 시간
 * @param timeAndStatuses 예약 시간과 예약 상태
 */
public record ReservationStatus(
        LocalDate date,

        @JsonFormat(pattern = "HH:mm")
        LocalTime openingTime,

        @JsonFormat(pattern = "HH:mm")
        LocalTime closingTime,

        List<TimeAndStatus> timeAndStatuses) {

    public static ReservationStatus from(
            LocalDate date, LocalTime openingTime, LocalTime closingTime,
            List<HairShopReservation> reservations) {

        List<TimeAndStatus> TimeAndStatuses = reservations.stream()
                .map(r -> new TimeAndStatus(r.getReservationTime().toLocalTime(), r.getReservationStatus())).toList();

        return new ReservationStatus(date, openingTime, closingTime, TimeAndStatuses);
    }

    public record TimeAndStatus(
            @JsonFormat(pattern = "HH:mm")
            LocalTime time,

            HairShopReservation.Status status
    ) {
    }
}
