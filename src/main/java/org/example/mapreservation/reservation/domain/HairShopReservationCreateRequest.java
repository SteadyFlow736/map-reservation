package org.example.mapreservation.reservation.domain;

import java.time.LocalDateTime;

public record HairShopReservationCreateRequest(LocalDateTime reservationTime) {
}
