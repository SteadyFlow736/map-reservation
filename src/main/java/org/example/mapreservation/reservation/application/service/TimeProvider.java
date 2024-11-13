package org.example.mapreservation.reservation.application.service;

import java.time.LocalDateTime;

/**
 * 현재 시간 제공 인터페이스
 */
public interface TimeProvider {
    LocalDateTime getCurrentDateTime();
}
