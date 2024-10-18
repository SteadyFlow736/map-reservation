package org.example.mapreservation.reservation.application.service;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime getCurrentDateTime();
}
