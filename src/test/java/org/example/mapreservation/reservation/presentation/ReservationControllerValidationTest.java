package org.example.mapreservation.reservation.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mapreservation.reservation.application.service.HairShopReservationService;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ReservationController validation 테스트
 * 파라미터 validation, collaborator(서비스) 메시징, api 리턴 검증
 */
@WebMvcTest(controllers = ReservationController.class)
class ReservationControllerValidationTest {

    @MockBean
    HairShopReservationService hairShopReservationService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    /**
     * <pre style="font-size: 11px;">
     * 예약 신청 api 정상 호출 동작
     * Given
     * - shopId
     * - 예약 시간: 2024.10.18 10시 00분
     * - 회원 정보
     * When: 예약 신청 api 호출
     * Then:
     * - 서비스 호출
     * - 리턴
     * </pre>
     */
    @WithMockUser(username = "abc@gmail.com")
    @Test
    void 예약_신청_api_정상_호출_동작() throws Exception {
        // given
        Long shopId = 1L;
        LocalDateTime reservationDateTime = LocalDateTime.of(2024, 10, 18, 10, 0);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationDateTime);

        // given - collaborator
        when(hairShopReservationService.createReservation(shopId, "abc@gmail.com", request)).thenReturn(
                new HairShopReservationCreateResponse(3L)
        );

        // when, then
        mockMvc.perform(post("/api/hairshops/{shopId}/reservations", shopId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationId").value(3L));
    }

    /**
     * <pre style="font-size: 11px;">
     * 예약 신청 api 비정상 호출 - 헤어샵 id 포맷 이상
     * Given
     * - shopId (Long으로 변경 불가능한 값)
     * - 예약 시간: 2024.10.18 10시 00분
     * - 회원 정보
     * When: 예약 신청 api 호출
     * Then:
     * - 에러 메시지 응답
     * </pre>
     */
    @WithMockUser(username = "abc@gmail.com")
    @Test
    void 예약_신청_api_비정상_호출_헤어샵_id_포맷_이상() throws Exception {
        // given
        String shopId = "strange";
        LocalDateTime reservationDateTime = LocalDateTime.of(2024, 10, 18, 10, 0);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationDateTime);

        // when, then
        mockMvc.perform(post("/api/hairshops/{shopId}/reservations", shopId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CMM_ARGUMENT_TYPE_MISMATCH"))
                .andExpect(jsonPath("$.message").value("전달된 값의 타입이 맞지 않습니다."))
                .andExpect(jsonPath("$.errors.value").value("strange"))
                .andExpect(jsonPath("$.errors.requiredType").value("java.lang.Long"));
    }

    /**
     * <pre style="font-size: 11px;">
     * 헤어샵 예약 현황 조회 api 정상 호출
     * Given
     * - shopId
     * - 조회 날짜: 2024.10.18
     * When: 예약 현황 조회 api 호출
     * Then:
     * - 예약 현황 리턴
     * </pre>
     */
    @WithMockUser
    @Test
    void 헤어샵_예약_현황_조회_api_정상_호출() throws Exception {
        // given
        Long shopId = 1L;
        LocalDate targetDate = LocalDate.of(2024, 10, 18);

        // given - collaborator
        ReservationStatus reservationStatus = ReservationStatus.builder()
                .date(targetDate)
                .openingTime(LocalTime.of(10, 0))
                .closingTime(LocalTime.of(20, 0))
                .reservedTimes(List.of(LocalTime.of(13, 0), LocalTime.of(14, 0)))
                .build();
        when(hairShopReservationService.getReservationStatus(shopId, targetDate)).thenReturn(reservationStatus);

        // when, then
        mockMvc.perform(get("/api/hairshops/{shopId}/reservations/status", shopId)
                        .queryParam("targetDate", "2024-10-18")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2024-10-18"))
                .andExpect(jsonPath("$.openingTime").value("10:00"))
                .andExpect(jsonPath("$.closingTime").value("20:00"))
                .andExpect(jsonPath("$.reservedTimes").value(
                        Matchers.contains("13:00", "14:00")
                ));
    }

}
