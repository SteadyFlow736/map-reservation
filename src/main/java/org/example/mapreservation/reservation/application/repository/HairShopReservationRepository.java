package org.example.mapreservation.reservation.application.repository;

import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HairShopReservationRepository {

    /**
     * 헤어샵 예약 저장
     *
     * @param hairShopReservation 저장될 헤어샵 예약
     * @return 저장된 헤어샵 예약
     */
    HairShopReservation save(HairShopReservation hairShopReservation);

    /**
     * 다수의 헤어샵 예약 저장
     *
     * @param reservations 저장될 헤어샵 예약 리스트
     * @return 저장된 헤어샵 예약 리스트
     */
    List<HairShopReservation> saveAll(List<HairShopReservation> reservations);

    /**
     * 특정 헤어샵의 헤어샵 예약 리스트 조회
     *
     * @param hairShopId 헤어샵 아이디
     * @return 헤어샵 예약 리스트
     */
    Optional<HairShopReservation> findById(Long hairShopId);

    /**
     * 예약 아이디와 예약자를 이용해 예약 조회
     *
     * @param hairShopReservationId 예약 아이디
     * @param email                 예약자
     * @return 헤어샵 예약
     */
    Optional<HairShopReservation> findByIdAndCustomerEmail(Long hairShopReservationId, String email);

    /**
     * 헤어샵의 특정 날짜의 헤어샵 예약 리스트 조회
     *
     * @param hairShop   헤어샵
     * @param targetDate 조회 날짜
     * @return 헤어샵 예약 리스트
     */
    List<HairShopReservation> findByHairShopAndTargetDate(
            HairShop hairShop, LocalDate targetDate);

    /**
     * 유저의 헤어샵 예약 리스트
     *
     * @param email    유저
     * @param pageable 페이지 정보
     * @return 헤어샵 예약 리스트
     */
    Slice<HairShopReservation> findByCustomerEmail(String email, Pageable pageable);

    /**
     * 헤어샵, 헤어샵 예약 시간, 헤어샵 예약 상태로 헤어샵 예약 조회
     *
     * @param hairShop            헤어샵
     * @param reservationDateTime 헤어샵 예약 시간
     * @param status              헤어샵 예약 상태
     * @return 헤어샵 예약
     */
    Optional<HairShopReservation> findByHairShopAndReservationTimeAndReservationStatus(
            HairShop hairShop, LocalDateTime reservationDateTime, HairShopReservation.Status status);

}
