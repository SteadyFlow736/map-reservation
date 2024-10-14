package org.example.mapreservation.reservation.application;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.infrastructure.CustomerJpaRepository;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.infrastructure.HairShopJpaRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationStatusGetRequest;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.example.mapreservation.reservation.infrastructure.HairShopReservationRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final HairShopReservationRepository hairShopReservationRepository;
    private final HairShopJpaRepository hairShopRepository;
    private final CustomerJpaRepository customerRepository;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    /**
     * 헤어샵 예약
     *
     * @param shopId          대상 헤어샵 id
     * @param username        고객명(이메일)
     * @param currentDateTime 현재 시간
     * @param request         요청 내용(예약 시간)
     * @return 예약 id
     */
    public HairShopReservationCreateResponse createHairShopReservation(Long shopId, String username, LocalDateTime currentDateTime,
                                                                       HairShopReservationCreateRequest request) {

        // redisson을 이용한 분산락 적용으로 "헤어샵 id + 예약 시간" 조합이 유일하게 예약될 수 있도록 함
        // TODO: 분산락을 재사용 가능하게, 깔끔하게 적용할 수 있는 법 찾기. (wrapping method or aop or facade ...?)
        String lockKey = String.format("%s:%s", shopId, request.reservationTime());
        RLock lock = redissonClient.getLock(lockKey);

        // 커밋 전에 잠금이 풀리면 race condition이 발생해 정합성에 문제가 발생한다.
        // 커밋 후에 잠금을 풀도록 하기 위해, 트랜잭션 실행을 분산락 실행으로 감쌌다.
        // https://helloworld.kurly.com/blog/distributed-redisson-lock/
        try {
            boolean available = lock.tryLock(5L, 3L, TimeUnit.SECONDS);
            if (!available) {
                throw new CustomException(ErrorCode.LCK_CANNOT_ACQUIRE_LOCK, "잠시 후 다시 시도해 주세요.");
            }
            Long reservationId = transactionTemplate.execute(status -> createHairShopReservationInternal(shopId, username, currentDateTime, request));
            return new HairShopReservationCreateResponse(reservationId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private Long createHairShopReservationInternal(Long shopId, String username, LocalDateTime currentDateTime,
                                                   HairShopReservationCreateRequest request) {

        isValidReservationTime(currentDateTime, request.reservationTime());

        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCode.CUST_NOT_FOUND));

        HairShop hairShop = hairShopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));

        HairShopReservation hairShopReservation =
                new HairShopReservation(customer, hairShop, request.reservationTime());

        hairShopReservationRepository.findByHairShopAndReservationTimeAndReservationStatus(
                        hairShop, request.reservationTime(), HairShopReservation.Status.RESERVED
                )
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

    @Transactional
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
    @Transactional
    public HairShopReservationResponse getHairShopReservation(Long reservationId, String username) {
        HairShopReservation hairShopReservation = hairShopReservationRepository.findByIdAndCustomerEmail(reservationId, username)
                .orElseThrow(() -> new CustomException(ErrorCode.HSR_NOT_FOUND));
        return HairShopReservationResponse.from(hairShopReservation);
    }

    /**
     * 고객별 예약 정보 조회 (slicing)
     *
     * @param username 고객 이메일
     * @param pageable 페이지 정보
     * @return 헤어샵 예약 정보 (slice)
     */
    @Transactional
    public Slice<HairShopReservationResponse> getHairShopReservations(String username, Pageable pageable) {
        Slice<HairShopReservation> slice = hairShopReservationRepository.findByCustomerEmail(username, pageable);
        // TODO: HairShopReservation -> HairShopReservationDto 변경 시 Customer 엔티티 조회 추가로 일어나는 N + 1 문제 해결
        // ReesrvationServiceTest::getHairShopReservations 테스트에서 N + 1 문제 확인 가능
        return slice.map(HairShopReservationResponse::from);
    }

    /**
     * 예약 취소
     *
     * @param reservationId   예약 id
     * @param username        예약자(고객 이메일)
     * @param currentDateTime 현재 시간
     */
    @Transactional
    public void cancelReservationById(Long reservationId, String username, LocalDateTime currentDateTime) {
        HairShopReservation hairShopReservation = hairShopReservationRepository.findByIdAndCustomerEmail(reservationId, username)
                .orElseThrow(() -> new CustomException(ErrorCode.HSR_NOT_FOUND));

        hairShopReservation.cancel(currentDateTime);
    }

}
