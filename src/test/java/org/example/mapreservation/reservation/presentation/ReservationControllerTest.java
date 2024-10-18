package org.example.mapreservation.reservation.presentation;

import org.example.mapreservation.hairshop.domain.HairShopResponse;
import org.example.mapreservation.reservation.application.service.HairShopReservationService;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationResponse;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ReservationController가 올바른 인자를 전달받았을 때,
 * 기대한 대로 서비스를 호출하는지 테스트
 * 기대한 대로 컨트롤러가 응답하는지 테스트
 */
public class ReservationControllerTest {

    ReservationController controller;
    HairShopReservationService hairShopReservationService;

    @BeforeEach
    void setUp() {
        hairShopReservationService = Mockito.mock(HairShopReservationService.class);
        controller = new ReservationController(hairShopReservationService);
    }

    /**
     * <pre style="font-size: 11px;">
     * 컨트롤러는 예약 신청 서비스를 호출할 수 있다.
     * Given
     * - 헤어샵 id
     * - 회원 정보
     * - 예약 시간: 2024.10.16 10시 00분
     * When:
     * - 예약 생성 서비스 메소드 호출
     * Then: 호출 시 서비스에 올바른 인수 전달
     * </pre>
     */
    @Test
    void 컨트롤러는_예약_신청_서비스를_호출한다() {
        // given
        Long shopId = 1L;
        UserDetails userDetails = new User("abc@gmail.com", "Password1!",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(
                LocalDateTime.of(2024, 10, 18, 10, 0)
        );

        Long reservationId = 3L;
        when(hairShopReservationService.createReservation(any(), any(), any())).thenReturn(
                new HairShopReservationCreateResponse(reservationId)
        );

        // when
        ResponseEntity<HairShopReservationCreateResponse> response =
                controller.createHairShopReservation(shopId, userDetails, request);

        // then - 서비스에 올바른 인수 전달 여부 확인
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HairShopReservationCreateRequest> requestArgumentCaptor =
                ArgumentCaptor.forClass(HairShopReservationCreateRequest.class);

        Mockito.verify(hairShopReservationService, Mockito.times(1)).createReservation(
                longArgumentCaptor.capture(),
                stringArgumentCaptor.capture(),
                requestArgumentCaptor.capture()
        );

        assertThat(longArgumentCaptor.getValue()).isEqualTo(1L);
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("abc@gmail.com");
        assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);

        // then - 응답 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        HairShopReservationCreateResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.reservationId()).isEqualTo(3L);
    }

    /**
     * <pre style="font-size: 11px;">
     * 컨트롤러는 예약 현황 조회 서비스를 호출할 수 있다.
     * Given
     * - 헤어샵 id
     * - 조회 날짜
     * When: 예약 현황 조회 서비스 메소드 호출
     * Then: 호출 시 서비스에 올바른 인수 전달
     * </pre>
     */
    @Test
    void 컨트롤러는_헤어샵_예약_현황_조회_서비스를_호출할_수_있다() {
        // given
        Long shopId = 1L;
        LocalDate targetDate = LocalDate.of(2024, 10, 18);

        List<LocalTime> reservedTimes = List.of(LocalTime.of(14, 30), LocalTime.of(15, 0));
        when(hairShopReservationService.getReservationStatus(any(), any())).thenReturn(
                ReservationStatus.builder()
                        .date(targetDate)
                        .reservedTimes(reservedTimes)
                        .openingTime(LocalTime.of(10, 0))
                        .closingTime(LocalTime.of(20, 0))
                        .build()
        );

        // when
        ResponseEntity<ReservationStatus> response = controller.getHairShopReservationStatus(shopId, targetDate);

        // then - 서비스에 올바른 인수 전달 여부 확인
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LocalDate> localDateArgumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
        Mockito.verify(hairShopReservationService, Mockito.times(1))
                .getReservationStatus(longArgumentCaptor.capture(), localDateArgumentCaptor.capture());

        assertThat(longArgumentCaptor.getValue()).isEqualTo(1L);
        assertThat(localDateArgumentCaptor.getValue()).isEqualTo(LocalDate.of(2024, 10, 18));

        // then - 응답 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ReservationStatus body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.date()).isEqualTo(LocalDate.of(2024, 10, 18));
        assertThat(body.openingTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(body.closingTime()).isEqualTo(LocalTime.of(20, 0));
        assertThat(body.reservedTimes()).isEqualTo(
                List.of(LocalTime.of(14, 30), LocalTime.of(15, 0)));
    }

    /**
     * <pre style="font-size: 11px;">
     * 컨트롤러는 예약 상세 조회 서비스를 호출할 수 있다.
     * Given
     * - 예약 id
     * - 예약한 유저 정보
     * When: 예약 상세 조회 서비스 메소드 호출
     * Then: 호출 시 서비스에 올바른 인수 전달
     * </pre>
     */
    @Test
    void 컨트롤러는_예약_상세_현황_조회_서비스를_호출할_수_있다() {
        // given
        Long reservationId = 1L;

        HairShopResponse hairShopResponse = HairShopResponse.builder()
                .shopId(2L)
                .shopName("헤어샵2")
                .longitude("10.0")
                .latitude("20.0")
                .images(List.of("url1", "url2", "url3"))
                .build();
        LocalDateTime reservationTime = LocalDateTime.of(2024, 10, 18, 10, 0);

        when(hairShopReservationService.getReservation(any(), any())).thenReturn(
                HairShopReservationResponse.builder()
                        .reservationId(reservationId)
                        .reservationTime(reservationTime)
                        .hairShopResponse(hairShopResponse)
                        .username("abc@gmail.com")
                        .status(HairShopReservation.Status.RESERVED)
                        .build()
        );

        // when
        UserDetails userDetails = new User("abc@gmail.com", "Password1!",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        ResponseEntity<HairShopReservationResponse> response = controller.getHairShopReservation(reservationId, userDetails);

        // then - 서비스에 올바른 인수 전달 여부 확인
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(hairShopReservationService, Mockito.times(1))
                .getReservation(longArgumentCaptor.capture(), stringArgumentCaptor.capture());

        assertThat(longArgumentCaptor.getValue()).isEqualTo(1L);
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("abc@gmail.com");

        // then - 응답 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        HairShopReservationResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.reservationId()).isEqualTo(1L);
        assertThat(body.reservationTime()).isEqualTo(
                LocalDateTime.of(2024, 10, 18, 10, 0));
        assertThat(body.hairShopResponse()).isEqualTo(hairShopResponse);
        assertThat(body.username()).isEqualTo("abc@gmail.com");
        assertThat(body.status()).isEqualTo(HairShopReservation.Status.RESERVED);
    }

    // 계속 할 것인가 말 것인가...?
}
