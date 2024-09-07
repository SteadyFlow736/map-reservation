package org.example.mapreservation.reservation.domain;

import org.assertj.core.api.Assertions;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.owner.domain.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HairShopReservationTest {

    @DisplayName("예약 시간 마진 이전에 예약 취소 시도 시 예약 취소 가능")
    @Test
    void givenBeforeMargin_thenCancellable() {
        // given
        Owner owner = new Owner("이준");
        HairShop hairShop = new HairShop("이준 헤어", new Address("성남대로123", "301호"), owner);
        Customer customer = new Customer("abc@gmail.com", "12345678");

        LocalDateTime reservationDateTime = LocalDateTime.now().withSecond(0).withNano(0);
        HairShopReservation hairShopReservation = new HairShopReservation(customer, hairShop, reservationDateTime);

        // when - 예약 시간에서 정확히 마진을 남겨둔 시간에 예약 취소 시도
        LocalDateTime currentDateTime = reservationDateTime.minusMinutes(HairShopReservation.CANCEL_MARGIN_MINUTES);

        // then - 예약 취소 가능
        assertDoesNotThrow(() -> hairShopReservation.cancel(currentDateTime));
    }

    @DisplayName("예약 시간 마진 이내에 예약 취소 시도 시 예약 취소 불가")
    @Test
    void givenWithinMargin_thenNotCancellable() {
        // given
        Owner owner = new Owner("이준");
        HairShop hairShop = new HairShop("이준 헤어", new Address("성남대로123", "301호"), owner);
        Customer customer = new Customer("abc@gmail.com", "12345678");

        LocalDateTime reservationDateTime = LocalDateTime.now().withSecond(0).withNano(0);
        HairShopReservation hairShopReservation = new HairShopReservation(customer, hairShop, reservationDateTime);

        // when - 예약 취소 가능 시간을 1분 넘겼을 때 취소 시도
        LocalDateTime currentDateTime = reservationDateTime.minusMinutes(HairShopReservation.CANCEL_MARGIN_MINUTES - 1);

        // then - 예약 취소 불가 예외 발생
        CustomException expectedException = new CustomException(ErrorCode.HSR_OLD_RESERVATION_TIME_CANNOT_CANCEL,
                String.format("최소 %s분 전에 취소할 수 있습니다.", HairShopReservation.CANCEL_MARGIN_MINUTES));
        Assertions.assertThatThrownBy(() -> hairShopReservation.cancel(currentDateTime))
                .isInstanceOf(CustomException.class)
                .hasMessage(String.format(expectedException.getCombinedMessage()));
    }
}
