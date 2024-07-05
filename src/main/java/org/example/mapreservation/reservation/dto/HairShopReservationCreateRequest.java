package org.example.mapreservation.reservation.dto;

import java.time.LocalDateTime;

public record HairShopReservationCreateRequest(LocalDateTime reservationTime) {
}
