package org.example.mapreservation.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 헤어샵 예약 현황 조회를 위한 DTO
 *
 * @param targetDate 조회 날짜
 */
public record HairShopReservationStatusGetRequest(LocalDate targetDate) {
    /**
     * @return 조회 날짜의 시작 시간
     */
    public LocalDateTime getStartDateTime() {
        return targetDate.atStartOfDay();
    }

    /**
     * @return 조회 날짜의 마지막 시간
     */
    public LocalDateTime getEndDateTime() {
        return targetDate.atTime(LocalTime.MAX);
    }
}
