package org.example.mapreservation.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.hairshop.domain.HairShop;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(
        name = "idx_hair_shop_id_reservation_time",
        columnList = "hair_shop_id, reservation_time"
))
@Entity
public class HairShopReservation {

    /**
     * 예약 취소 마진.
     * 예약 시간 최소 {CANCEL_MARGIN_MINUTES} 전에 취소 신청해야 취소가 가능.
     */
    public static final int CANCEL_MARGIN_MINUTES = 30;

    @Id
    @GeneratedValue
    private Long id;

    // 예약 손님
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;

    // 예약 받은 헤어샵
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private HairShop hairShop;

    // 예약 시간
    @Column(nullable = false)
    private LocalDateTime reservationTime;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    private Status reservationStatus;

    public HairShopReservation(Customer customer, HairShop hairShop, LocalDateTime reservationTime) {
        this.customer = customer;
        this.hairShop = hairShop;
        this.reservationTime = reservationTime;
        this.reservationStatus = Status.RESERVED;
    }

    /**
     * 주문 취소
     * 예약 시간까지 취소 마진 이상 남은 상황에서 취소가 가능하지만 취소 마진도 안 남은 상황에서는 취소가 불가능하다.
     *
     * @param currentDateTime 현재 시간
     */
    public void cancel(LocalDateTime currentDateTime) {
        if (currentDateTime.plusMinutes(CANCEL_MARGIN_MINUTES).isAfter(this.getReservationTime())) {
            String additionalMessage = String.format("최소 %s분 전에 취소할 수 있습니다.", CANCEL_MARGIN_MINUTES);
            throw new CustomException(ErrorCode.HSR_OLD_RESERVATION_TIME_CANNOT_CANCEL, additionalMessage);
        }
        this.reservationStatus = Status.CANCELLED;
    }

    public enum Status {
        RESERVED,  // 예약된 상태
        CANCELLED, // 예약 취소된 상태
    }
}
