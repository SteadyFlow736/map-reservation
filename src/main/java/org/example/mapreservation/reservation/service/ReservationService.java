package org.example.mapreservation.reservation.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.repository.CustomerRepository;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.dto.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ReservationService {

    private final HairShopReservationRepository hairShopReservationRepository;
    private final HairShopRepository hairShopRepository;
    private final CustomerRepository customerRepository;

    public Long createHairShopReservationOptimistic(Long shopId, String username, LocalDateTime currentTime,
                                                    HairShopReservationCreateRequest request) {
        isValidReservationTime(currentTime, request.reservationTime());
        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCode.CUST_NOT_FOUND));

        // OPTIMISTIC_FORCE_INCREMENT를 통해 HairShop의 논리적 versioning.
        // HairShop에 예약이 늘어나는 것은 HairShop의 논리적 변경
        // 같은 헤어샵, 같은 시간에 예약 유무 조회 결과(전제)가 이 트랜잭션이 끝나기 전까지 변경되게 하지 않도록 막아주는 역할을 한다.
        HairShop hairShop = hairShopRepository.findByIdOptimisticForceIncrement(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));

        HairShopReservation hairShopReservation =
                new HairShopReservation(customer, hairShop, request.reservationTime());

        hairShopReservationRepository.findByHairShopAndReservationTime(hairShop, request.reservationTime())
                .ifPresent(r -> {
                    throw new CustomException(ErrorCode.HSR_ALREADY_TAKEN_RESERVATION_TIME);
                });
        // 이 단계에서 다른 트랜잭션이 같은 헤어샵, 같은 시간으로 예약하는 것을 막아야 한다.
        // 이것을 위의 HairShop의 논리적 versioning을 통해 해결하려한다.

        return hairShopReservationRepository.save(hairShopReservation).getId();
    }

    private static void isValidReservationTime(LocalDateTime currentTime, LocalDateTime reservationTime) {
        if (currentTime.isAfter(reservationTime)) {
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
}
