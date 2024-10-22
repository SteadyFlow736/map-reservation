package org.example.mapreservation.auth;

import org.assertj.core.api.Assertions;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.infrastructure.CustomerJpaRepository;
import org.example.mapreservation.customer.application.CustomerServiceImpl;
import org.example.mapreservation.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class LoginTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    CustomerServiceImpl customerService;
    @Autowired
    CustomerJpaRepository customerRepository;

    @AfterEach
    void clean() {
        customerRepository.deleteAll();
    }

    @DisplayName("올바른 이메일과 비밀번호 제공 시 로그인 성공")
    @Test
    void givenValidAccountAndPassword_thenLoginSuccess() throws Exception {
        // given - 가입된 계정
        String email = "abc@gmail.com";
        String password = "12345678";
        customerService.createCustomer(email, password);

        // when - 가입된 계정을 올바르게 전달
        // then - 로그인 성공
        mockMvc.perform(post("/api/login")
                        .param("username", email)
                        .param("password", password)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(email))
                .andExpect(jsonPath("$.authorities[0]").value("ROLE_CUSTOMER"));
    }

    @DisplayName("일치하지 않는 비밀번호 제출시 로그인 실패")
    @Test
    void givenNotMatchingPassword_thenLoginFails() throws Exception {
        // given - 가입된 계정
        String email = "abc@gmail.com";
        String password = "12345678";
        customerService.createCustomer(email, password);

        // when - 일치하지 않는 비밀번호로 로그인 시도
        // then - 로그인 실패
        String wrongPassword = password + "123zxc";
        mockMvc.perform(post("/api/login")
                        .param("username", email)
                        .param("password", wrongPassword)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTH_INVALID_USERNAME_OR_PASSWORD.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.AUTH_INVALID_USERNAME_OR_PASSWORD.getMessage()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @DisplayName("존재하지 않는 고객 아이디 제출 시 로그인 실패")
    @Test
    void givenNotExistingCustomer_thenLoginFails() throws Exception {
        // given - 가입되지 않은 고객
        String email = "abc@gmail.com";
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);
        Assertions.assertThat(optionalCustomer).isEmpty();

        // when - 가입되지 않은 고객으로 로그인 시도
        // then - 로그인 실패
        String password = "12345678";
        mockMvc.perform(post("/api/login")
                        .param("username", email)
                        .param("password", password)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTH_INVALID_USERNAME_OR_PASSWORD.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.AUTH_INVALID_USERNAME_OR_PASSWORD.getMessage()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }
}
