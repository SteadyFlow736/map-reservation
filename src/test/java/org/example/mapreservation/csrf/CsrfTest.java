package org.example.mapreservation.csrf;

import org.example.mapreservation.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CsrfTest {

    @Autowired
    MockMvc mockMvc;

    @DisplayName("CSRF 토큰 발급 API는 CSRF 토큰이나 인증,인가를 요구하지 않는다.")
    @Test
    void csrfTokenAPI() throws Exception {
        mockMvc.perform(get("/api/csrf-token")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.csrfToken.token").isString())
                .andExpect(jsonPath("$.csrfToken.parameterName").isString())
                .andExpect(jsonPath("$.csrfToken.headerName").isString());
    }

    @DisplayName("유효하지 않은 CSRF 토큰으로 POST 등 CSRF 토큰이 필요한 API 호출 시 거부 메시지가 발급된다.")
    @Test
    void whenInvalidCsrfToken_thenForbidden() throws Exception {
        mockMvc.perform(post("/api/some-protected-endpoint")
                        .with(csrf().useInvalidToken())
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.CF_ERROR.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.CF_ERROR.getMessage()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @DisplayName("빈 CSRF 토큰으로 POST 등 CSRF 토큰이 필요한 API 호출 시 거부 메시지가 발급된다.")
    @Test
    void whenNoCsrfToken_thenForbidden() throws Exception {
        mockMvc.perform(post("/api/some-protected-endpoint"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.CF_ERROR.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.CF_ERROR.getMessage()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

}
