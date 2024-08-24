package org.example.mapreservation.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.reservation.dto.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.dto.HairShopReservationStatusGetRequest;
import org.example.mapreservation.reservation.dto.ReservationStatus;
import org.example.mapreservation.reservation.service.ReservationService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * 헤어샵 예약 API
     *
     * @param shopId  헤어샵 id
     * @param user    예약자
     * @param request 예약 내용
     * @return 예약 결과
     */
    @PostMapping("/api/hairshops/{shopId}/reservations")
    public ResponseEntity<Void> createHairShopReservation(
            @PathVariable("shopId") Long shopId,
            @AuthenticationPrincipal User user,
            @RequestBody HairShopReservationCreateRequest request
    ) {
        Long reservationId = reservationService.createHairShopReservation(shopId, user.getUsername(), LocalDateTime.now(), request);
        String uri = String.format("/api/hairshops/%s/reservations/%s", shopId, reservationId);
        return ResponseEntity.created(URI.create(uri)).build();
    }

    @GetMapping("/api/hairshops/{shopId}/reservations/status")
    public ResponseEntity<ReservationStatus> getHairShopReservationStatus(
            @PathVariable("shopId") Long shopId,
            @Param("targetDate") HairShopReservationStatusGetRequest request
    ) {
        ReservationStatus status = reservationService.getHairShopReservationStatus(shopId, request);
        return ResponseEntity.ok(status);
    }
}
