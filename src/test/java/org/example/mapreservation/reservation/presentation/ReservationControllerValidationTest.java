package org.example.mapreservation.reservation.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mapreservation.reservation.application.service.HairShopReservationService;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
     * 컨트롤러는 예약 신청 api를 통해 전달받은 파라미터를 잘 추출하여 서비스로 예약 신청한다.
     * Given
     * - shopId
     * - 예약 시간: 2024.10.16 10시 00분
     * - 회원 정보
     * When: 예약 신청 api 호출
     * Then: 서비스로 예약 신청
     * </pre>
     */
    @WithMockUser
    @Test
    void test() throws Exception {
        // given
        Long shopId = 1L;
        LocalDateTime reservationDateTime = LocalDateTime.of(2024, 10, 18, 10, 0);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationDateTime);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hairshops/{shopId}/reservations", shopId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                        .with(csrf())
                )
                .andDo(print());

        // then
    }
}
