package org.example.mapreservation.auth;

import org.example.mapreservation.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthenticationTest {

    @Autowired
    MockMvc mockMvc;

    @DisplayName("미인증 고객이 인증이 필요한 자원 요청시 로그인이 필요하다는 응답을 보낸다.")
    @Test
    void givenNotAuthenticatedRequest_thenAuthenticationRequired() throws Exception {
        mockMvc.perform(post("/api/some-secured-resource")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTH_NEED_AUTHENTICATION.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.AUTH_NEED_AUTHENTICATION.getMessage()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }
}
