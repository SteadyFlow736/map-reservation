package org.example.mapreservation.reservation.application.service;

import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationStatusGetRequest;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface HairShopReservationService {

    /**
     * 헤어샵 예약 생성
     *
     * @param shopId          대상 헤어샵 id
     * @param username        고객명(이메일)
     * @param currentDateTime 현재 시간
     * @param request         요청 내용(예약 시간)
     * @return 예약 id
     */
    HairShopReservationCreateResponse createReservation(
            Long shopId, String username, LocalDateTime currentDateTime, HairShopReservationCreateRequest request);

    /**
     * 헤어샵 하루 예약 현황 조회
     *
     * @param shopId  헤어샵 id
     * @param request 날짜
     * @return 헤어샵 예약 현황
     */
    ReservationStatus getReservationStatus(Long shopId, HairShopReservationStatusGetRequest request);

    /**
     * 헤어샵 예약 상세 조회
     *
     * @param reservationId 헤어샵 예약 id
     * @param username      조회 요청 유저
     * @return 헤어샵 예약 정보
     */
    HairShopReservationResponse getReservation(Long reservationId, String username);

    /**
     * 유저 예약 히스토리 조회 (slicing)
     *
     * @param username 조회 요청 유저
     * @param pageable 페이지 정보
     * @return 헤어샵 예약 정보 (slice)
     */
    Slice<HairShopReservationResponse> getReservations(String username, Pageable pageable);

    /**
     * 예약 취소
     *
     * @param reservationId   예약 id
     * @param username        유저(예약자)
     * @param currentDateTime 현재 시간
     */
    void cancelReservation(Long reservationId, String username, LocalDateTime currentDateTime);
}
