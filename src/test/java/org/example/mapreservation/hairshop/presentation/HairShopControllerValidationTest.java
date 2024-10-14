package org.example.mapreservation.hairshop.presentation;

import org.example.mapreservation.exception.GlobalExceptionHandler;
import org.example.mapreservation.hairshop.application.service.HairShopService;
import org.example.mapreservation.hairshop.domain.HairShopDetailResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class HairShopControllerValidationTest {

    HairShopService hairShopService;
    HairShopController hairShopController;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        hairShopService = Mockito.mock(HairShopService.class);
        hairShopController = new HairShopController(hairShopService);
        mockMvc = MockMvcBuilders.standaloneSetup(hairShopController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @Test
    void 헤어샵_상세_정보_검사_시_헤어샵_아이디를_제공한다() throws Exception {
        // given
        HairShopDetailResponse response = HairShopDetailResponse.builder()
                .shopId(1L)
                .shopName("헤어샵")
                .latitude("10.0")
                .longitude("20.0")
                .imageUrls(List.of("url1", "url2", "url3"))
                .roadAddress("도로주소")
                .detailAddress("상세주소")
                .build();
        when(hairShopService.getHairShopDetail(1L)).thenReturn(response);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hairshops/{hairShopId}", 1)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopId").value(1L))
                .andExpect(jsonPath("$.shopName").value("헤어샵"))
                .andExpect(jsonPath("$.latitude").value("10.0"))
                .andExpect(jsonPath("$.longitude").value("20.0"))
                .andExpect(jsonPath("$.imageUrls").value(
                        Matchers.contains("url1", "url2", "url3")
                ))
                .andExpect(jsonPath("$.roadAddress").value("도로주소"))
                .andExpect(jsonPath("$.detailAddress").value("상세주소"));
    }

    @Test
    void 헤어샵_상세_정보_검사_시_적절치_않은_포맷의_헤어샵_아이디를_제공_시_에러_응답을_제공한다() throws Exception {
        // given
        String hairShopId = "strangevalue";

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hairshops/{hairShopId}", hairShopId)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CMM_ARGUMENT_TYPE_MISMATCH"))
                .andExpect(jsonPath("$.message").value("전달된 값의 타입이 맞지 않습니다."))
                .andExpect(jsonPath("$.errors.value").value("strangevalue"))
                .andExpect(jsonPath("$.errors.requiredType").value("java.lang.Long"));
    }

    @Test
    void 존재하지_않는_엔드포인트에_접근할_때_에러_응답을_제공한다() throws Exception {
        // given

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hairshops/")
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CMM_NO_HANDLER_FOUND"))
                .andExpect(jsonPath("$.message").value("핸들러가 존재하지 않습니다."))
                .andExpect(jsonPath("$.errors").value("No endpoint GET /api/hairshops/."));
    }
}
