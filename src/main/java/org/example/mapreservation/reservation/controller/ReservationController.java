package org.example.mapreservation.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.reservation.dto.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/api/hairshops/{shopId}/reservations")
    public ResponseEntity<Void> createHairShopReservation(
            @PathVariable("shopId") Long shopId,
            @AuthenticationPrincipal User user,
            @RequestBody HairShopReservationCreateRequest request
    ) {
        Long reservationId = reservationService.createHairShopReservationOptimistic(shopId, user.getUsername(), LocalDateTime.now(), request);
        String uri = String.format("/api/hairshops/%s/reservations/%s", shopId, reservationId);
        return ResponseEntity.created(URI.create(uri)).build();
    }
}
