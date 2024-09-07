package org.example.mapreservation.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.repository.CustomerRepository;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.dto.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.dto.HairShopReservationDto;
import org.example.mapreservation.reservation.dto.HairShopReservationStatusGetRequest;
import org.example.mapreservation.reservation.dto.ReservationStatus;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ReservationService {

    private final HairShopReservationRepository hairShopReservationRepository;
    private final HairShopRepository hairShopRepository;
    private final CustomerRepository customerRepository;

    public Long createHairShopReservation(Long shopId, String username, LocalDateTime currentDateTime,
                                          HairShopReservationCreateRequest request) {

        isValidReservationTime(currentDateTime, request.reservationTime());

        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCode.CUST_NOT_FOUND));

        HairShop hairShop = hairShopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));

        HairShopReservation hairShopReservation =
                new HairShopReservation(customer, hairShop, request.reservationTime());

        hairShopReservationRepository.findByHairShopAndReservationTime(hairShop, request.reservationTime())
                .ifPresent(r -> {
                    throw new CustomException(ErrorCode.HSR_ALREADY_TAKEN_RESERVATION_TIME);
                });

        return hairShopReservationRepository.save(hairShopReservation).getId();
    }

    private static void isValidReservationTime(LocalDateTime currentDateTime, LocalDateTime reservationTime) {
        if (currentDateTime.isAfter(reservationTime)) {
            throw new CustomException(ErrorCode.HSR_OLD_RESERVATION_TIME);
        }
        List<Integer> allowedMinutes = List.of(0, 30);
        if (!allowedMinutes.contains(reservationTime.getMinute())) {
            throw new CustomException(ErrorCode.HSR_INVALID_RESERVATION_TIME);
        }
        if (reservationTime.getSecond() != 0 || reservationTime.getNano() != 0) {
            throw new CustomException(ErrorCode.HSR_INVALID_RESERVATION_TIME);
        }
    }

    public ReservationStatus getHairShopReservationStatus(Long shopId, HairShopReservationStatusGetRequest request) {
        LocalDateTime searchStartDateTime = request.getStartDateTime();
        LocalDateTime searchEndDateTime = request.getEndDateTime();

        HairShop hairShop = hairShopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));

        List<HairShopReservation> reservations = hairShopReservationRepository
                .findByHairShopAndReservationTimeBetween(hairShop, searchStartDateTime, searchEndDateTime);

        // TODO: 영업 시작, 종료 시간은 헤어샵 마다, 기준 날짜마다 다르게 가져갈 수 있도록 기능 넣기. 일단 고정된 값으로 전달.
        LocalTime openingTime = LocalTime.of(10, 0);
        LocalTime closingTime = LocalTime.of(20, 0);
        return ReservationStatus.from(request.targetDate(), openingTime, closingTime, reservations);
    }

    /**
     * 헤어샵 예약 정보 조회
     *
     * @param reservationId 헤어샵 예약 id
     * @param username      조회 요청 유저
     * @return 헤어샵 예약 정보
     */
    public HairShopReservationDto getHairShopReservation(Long reservationId, String username) {
        HairShopReservation hairShopReservation = hairShopReservationRepository.findByIdAndCustomerEmail(reservationId, username)
                .orElseThrow(() -> new CustomException(ErrorCode.HSR_NOT_FOUND));
        return HairShopReservationDto.from(hairShopReservation);
    }

    /**
     * 고객별 예약 정보 조회 (slicing)
     *
     * @param username 고객 이메일
     * @param pageable 페이지 정보
     * @return 헤어샵 예약 정보 (slice)
     */
    public Slice<HairShopReservationDto> getHairShopReservations(String username, Pageable pageable) {
        Slice<HairShopReservation> slice = hairShopReservationRepository.findByCustomerEmail(username, pageable);
        // TODO: HairShopReservation -> HairShopReservationDto 변경 시 Customer 엔티티 조회 추가로 일어나는 N + 1 문제 해결
        // ReesrvationServiceTest::getHairShopReservations 테스트에서 N + 1 문제 확인 가능
        return slice.map(HairShopReservationDto::from);
    }

    /**
     * 예약 취소
     *
     * @param reservationId   예약 id
     * @param username        예약자(고객 이메일)
     * @param currentDateTime 현재 시간
     */
    public void cancelReservationById(Long reservationId, String username, LocalDateTime currentDateTime) {
        HairShopReservation hairShopReservation = hairShopReservationRepository.findByIdAndCustomerEmail(reservationId, username)
                .orElseThrow(() -> new CustomException(ErrorCode.HSR_NOT_FOUND));

        hairShopReservation.cancel(currentDateTime);
    }

}
