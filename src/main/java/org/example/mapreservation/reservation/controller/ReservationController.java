package org.example.mapreservation.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.reservation.dto.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.dto.HairShopReservationDto;
import org.example.mapreservation.reservation.dto.HairShopReservationStatusGetRequest;
import org.example.mapreservation.reservation.dto.ReservationStatus;
import org.example.mapreservation.reservation.service.ReservationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

    /**
     * 헤어샵의 특정 날짜 예약 현황 조회 API
     *
     * @param shopId  헤어샵 id
     * @param request 조회 날짜
     * @return 예약 현황
     */
    @GetMapping("/api/hairshops/{shopId}/reservations/status")
    public ResponseEntity<ReservationStatus> getHairShopReservationStatus(
            @PathVariable("shopId") Long shopId,
            @Param("targetDate") HairShopReservationStatusGetRequest request
    ) {
        ReservationStatus status = reservationService.getHairShopReservationStatus(shopId, request);
        return ResponseEntity.ok(status);
    }

    /**
     * 예약 id를 통한 헤어샵 예약 정보 조회 API
     *
     * @param reservationId 예약 id
     * @param user          유저 정보
     * @return 예약 정보
     */
    @GetMapping("/api/reservations/{reservationId}")
    public ResponseEntity<HairShopReservationDto> getHairShopReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal UserDetails user
    ) {
        HairShopReservationDto dto = reservationService.getHairShopReservation(reservationId, user.getUsername());
        return ResponseEntity.ok(dto);
    }

    /**
     * 고객의 예약 리스트 조회 API
     * 무한 스크롤을 지원하기 위해 Slice로 리턴한다.
     *
     * @param user 고객 정보
     * @return 에약 리스트
     */
    @GetMapping("/api/reservations")
    public ResponseEntity<Slice<HairShopReservationDto>> getHairShopReservations(
            @AuthenticationPrincipal UserDetails user,
            Pageable pageable
    ) {
        Slice<HairShopReservationDto> page = reservationService.getHairShopReservations(user.getUsername(), pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 예약 취소 API
     *
     * @param reservationId 예약 id
     * @param user          예약자(고객 정보)
     */
    @PostMapping("/api/reservations/{reservationId}")
    public ResponseEntity<Void> cancelHairShopReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetails user
    ) {
        reservationService.cancelReservationById(reservationId, user.getUsername(), LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
}
