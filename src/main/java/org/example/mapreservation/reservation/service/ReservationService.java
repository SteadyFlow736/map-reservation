package org.example.mapreservation.reservation.service;

import jakarta.transaction.Transactional;
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

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ReservationService {

    private final HairShopReservationRepository hairShopReservationRepository;
    private final HairShopRepository hairShopRepository;
    private final CustomerRepository customerRepository;

    public Long createHairShopReservation(Long shopId, String username, LocalDateTime currentTime,
                                          HairShopReservationCreateRequest request) {
        isValidReservationTime(currentTime, request.reservationTime());

        HairShop hairShop = hairShopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));
        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCode.CUST_NOT_FOUND));

        HairShopReservation hairShopReservation =
                new HairShopReservation(customer, hairShop, request.reservationTime());

        // 이 부분에서 두 가지가 마음에 걸린다.
        // 1. 이것만으로 중복 체크가 될까? 동시성도 문제는 없을까?
        //    harishopId + reservationTime 복합 인덱스? mysql repeatable read, select for update gap locking
        // 2. 예약을 찾는 메서드가 findByHairShopAndReservationTime 인데, 인덱스 없인 느리지 않을까?
        //    LocalDateTime에도 인덱스 가능한가?
        hairShopReservationRepository.findByHairShopAndReservationTime(hairShop, request.reservationTime())
                .ifPresent(r -> {
                    throw new CustomException(ErrorCode.HSR_ALREADY_TAKEN_RESERVATION_TIME);
                });

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
