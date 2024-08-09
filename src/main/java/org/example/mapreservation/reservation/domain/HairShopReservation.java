package org.example.mapreservation.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.example.mapreservation.hairshop.domain.HairShop;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(
        name = "idx_hair_shop_id_reservation_time",
        columnList = "hair_shop_id, reservation_time",
        unique = true))
@Entity
public class HairShopReservation {

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

    public HairShopReservation(Customer customer, HairShop hairShop, LocalDateTime reservationTime) {
        this.customer = customer;
        this.hairShop = hairShop;
        this.reservationTime = reservationTime;
    }
}
