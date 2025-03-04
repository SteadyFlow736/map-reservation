package org.example.mapreservation.reservation.presentation;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.mapreservation.reservation.application.service.HairShopReservationService;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationResponse;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final HairShopReservationService reservationService;

    /**
     * 헤어샵 예약
     *
     * @param shopId  헤어샵 id
     * @param user    유저(예약자)
     * @param request 예약 내용
     * @return 예약 결과
     */
    @PostMapping("/api/hairshops/{shopId}/reservations")
    public ResponseEntity<HairShopReservationCreateResponse> createHairShopReservation(
            @PathVariable("shopId") Long shopId,
            @AuthenticationPrincipal UserDetails user,
            @RequestBody HairShopReservationCreateRequest request
    ) {
        HairShopReservationCreateResponse response = reservationService.createReservation(
                shopId, user.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 헤어샵 예약 현황 조회 by 헤어샵 id, 날짜
     *
     * @param shopId     헤어샵 id
     * @param targetDate 조회 날짜
     * @return 예약 현황
     */
    @GetMapping("/api/hairshops/{shopId}/reservations/status")
    public ResponseEntity<ReservationStatus> getHairShopReservationStatus(
            @PathVariable("shopId") Long shopId,
            @Param("targetDate") LocalDate targetDate
    ) {
        ReservationStatus status = reservationService.getReservationStatus(shopId, targetDate);
        return ResponseEntity.ok(status);
    }

    /**
     * 예약 상세 조회 by 예약 id
     *
     * @param reservationId 예약 id
     * @param user          유저 정보
     * @return 예약 정보
     */
    @GetMapping("/api/reservations/{reservationId}")
    public ResponseEntity<HairShopReservationResponse> getHairShopReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal UserDetails user
    ) {
        HairShopReservationResponse response = reservationService.getReservation(reservationId, user.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * 유저의 예약 리스트 조회
     * 무한 스크롤을 지원하기 위해 Slice로 리턴한다.
     *
     * @param user     유저 정보
     * @param pageable 페이징 정보
     * @return 에약 리스트
     */
    @GetMapping("/api/reservations")
    public ResponseEntity<Slice<HairShopReservationResponse>> getHairShopReservations(
            @AuthenticationPrincipal UserDetails user,
            Pageable pageable
    ) {
        Slice<HairShopReservationResponse> page = reservationService.getReservations(user.getUsername(), pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 예약 취소
     *
     * @param reservationId 예약 id
     * @param user          유저(예약자)
     */
    @PostMapping("/api/reservations/{reservationId}")
    public ResponseEntity<Void> cancelHairShopReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetails user
    ) {
        reservationService.cancelReservation(reservationId, user.getUsername(), LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
}
