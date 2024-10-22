package org.example.mapreservation.reservation.domain;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.owner.domain.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class HairShopReservationTest {

    Customer customer;
    HairShop hairShop;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .email("abc@gmail.com")
                .password("Password1!")
                .build();
        Owner owner = new Owner(1L, "주인1");
        hairShop = HairShop.builder()
                .id(1L)
                .name("헤어샵1")
                .address(new Address("도로주소", "상세주소"))
                .owner(owner)
                .longitude("10.0")
                .latitude("20.0")
                .imageUrls(List.of("url1", "url2", "url3"))
                .build();
    }

    /**
     * <pre style="font-size: 11px;">
     * 헤어샵 예약 객체는 생성 시 상태가 "예약"이 된다.
     * Given
     * When: 헤어샵 예약 객체 생성
     * Then: 헤어샵 예약 객체의 상태 프로퍼티가 "예약"
     * </pre>
     */
    @Test
    void 헤어샵_예약_객체는_생성_시_예약_상태가_된다() {
        // when
        HairShopReservation hairShopReservation = HairShopReservation.builder()
                .customer(customer)
                .hairShop(hairShop)
                .reservationTime(LocalDateTime.now())
                .build();

        // then
        assertThat(hairShopReservation.getReservationStatus()).isEqualTo(HairShopReservation.Status.RESERVED);
    }

    /**
     * <pre style="font-size: 11px;">
     * 헤어샵 예약 객체는 예약 시간 30분전까지만 예약 취소가 가능하다
     * Given
     * - 예약 객체의 예약 시간이 2024.10.16 17:30
     * When: 예약 취소 메소드 호출
     * - 예약 신청 시간이 2024.10.16 17:00 (예약 시간 30분 전)
     * Then: 예약 취소 성공
     * - 예약 객체의 상태 프로퍼티가 "취소됨"으로 변경
     * </pre>
     */
    @Test
    void 헤어샵_예약_객체는_예약_시간_30분전까지만_예약_취소가_가능하다() {
        // given
        LocalDateTime reservationDateTime = LocalDateTime.of(2024, 10, 16, 17, 30);

        HairShopReservation hairShopReservation = HairShopReservation.builder()
                .customer(customer)
                .hairShop(hairShop)
                .reservationTime(reservationDateTime)
                .build();

        // when
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 10, 16, 17, 0);
        hairShopReservation.cancel(currentDateTime);

        // then
        assertThat(hairShopReservation.getReservationStatus()).isEqualTo(HairShopReservation.Status.CANCELLED);
    }

    /**
     * <pre style="font-size: 11px;">
     * 헤어샵 예약 객체는 예약 시간 29분부터는 예약 취소가 불가능하다
     * Given
     * - 예약 객체의 예약 시간이 2024.10.16 17:30
     * When: 예약 취소 메소드 호출
     * - 예약 신청 시간이 2024.10.16 17:01 (예약 시간 29분 전)
     * Then: 예약 취소 성공
     * - 예약 객체의 상태 프로퍼티가 여전히 "예약됨"으로 유지
     * </pre>
     */
    @Test
    void 헤어샵_예약_객체는_예약_시간_29분부터는_예약_취소가_불가능하다() {
        // given
        LocalDateTime reservationDateTime = LocalDateTime.of(2024, 10, 16, 17, 30);

        HairShopReservation hairShopReservation = HairShopReservation.builder()
                .customer(customer)
                .hairShop(hairShop)
                .reservationTime(reservationDateTime)
                .build();

        // when
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 10, 16, 17, 1);
        assertThatThrownBy(() -> hairShopReservation.cancel(reservationDateTime))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 지나간 예약이므로 취소할 수 없습니다.: 최소 30분 전에 취소할 수 있습니다.");

        // then
        assertThat(hairShopReservation.getReservationStatus()).isEqualTo(HairShopReservation.Status.RESERVED);
    }

}
