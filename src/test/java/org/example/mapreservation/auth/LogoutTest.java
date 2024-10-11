package org.example.mapreservation.auth;

import org.example.mapreservation.customer.service.CustomerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class LogoutTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    CustomerServiceImpl customerService;

    @DisplayName("고객은 로그아웃할 수 있다.")
    @WithMockUser(username = "abc@gmail.com")
    @Test
    void givenLogout_thenSuccess() throws Exception {
        // when - 로그아웃 수행
        mockMvc.perform(post("/api/logout")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
        // then - 인증이 필요한 곳에 접근이 불가하다.
        mockMvc.perform(post("/api/secured")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
