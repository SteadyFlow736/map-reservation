package org.example.mapreservation.customer.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mapreservation.customer.presentation.request.CustomerCreate;
import org.example.mapreservation.customer.infrastructure.CustomerJpaRepository;
import org.example.mapreservation.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CustomerControllerIntegrationTest {

    final String registerURL = "/api/customers";
    final String meURL = "/api/customers/me";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    CustomerJpaRepository customerRepository;

    @AfterEach
    void clean() {
        customerRepository.deleteAll();
    }

    @DisplayName("고객은 계정을 만들 수 있다.")
    @Test
    void createCustomer() throws Exception {
        // given
        String email = "abc@gmail.com";
        String password = "Password1!";
        CustomerCreate customerCreate = new CustomerCreate(email, password);

        // when, then
        mockMvc.perform(post(registerURL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(customerCreate))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @DisplayName("고객은 이미 가입된 이메일 주소로 계정을 만들 수 없다.")
    @Test
    void givenDuplicateEmail_thenNotAllowed() throws Exception {
        // given - 최초 가입
        String email = "abc@gmail.com";
        String password = "Password1!";
        CustomerCreate customerCreate = new CustomerCreate(email, password);
        mockMvc.perform(post(registerURL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(customerCreate))
                .with(csrf())
        );

        // when - 이미 가입된 이메일로 계정 생성 요청
        // then - 에러 메시지 출력
        mockMvc.perform(post(registerURL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(customerCreate))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.CUST_ALREADY_TAKEN_EMAIL.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.CUST_ALREADY_TAKEN_EMAIL.getMessage()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @DisplayName("고객은 자신의 정보를 읽을 수 있다.")
    @WithMockUser(username = "abc@gmail.com", roles = {"USER"})
    @Test
    void givenLoggedIn_thenGetCustomerInfo() throws Exception {
        mockMvc.perform(get(meURL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(jsonPath("$.username").value("abc@gmail.com"))
                .andExpect(jsonPath("$.authorities.size()").value(1))
                .andExpect(jsonPath("$.authorities[0]").value("ROLE_USER"));
    }
}
