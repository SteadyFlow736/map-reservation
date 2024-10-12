package org.example.mapreservation.customer.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mapreservation.customer.presentation.request.CustomerCreate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SqlGroup({
        @Sql(value = "/sql/delete-all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
@AutoConfigureMockMvc
@SpringBootTest
class CustomerControllerIntegrationTest {

    final String registerURL = "/api/customers";
    final String meURL = "/api/customers/me";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 고객은_계정을_만들_수_있다() throws Exception {
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
                .andExpect(status().isCreated());
    }

    @Test
    void 고객은_적절치_못한_정보로_계정을_만들_수_없다() throws Exception {
        // given
        String email = "invalid-email";
        String password = "12345678!";
        CustomerCreate customerCreate = new CustomerCreate(email, password);

        // when, then
        mockMvc.perform(post(registerURL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(customerCreate))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CMM_FIELD_VALIDATION_FAILURE"))
                .andExpect(jsonPath("$.message").value("필드 값 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors.email").isArray())
                .andExpect(jsonPath("$.errors.email").value("이메일 형식이 잘못되었습니다."))
                .andExpect(jsonPath("$.errors.password").isArray())
                .andExpect(jsonPath("$.errors.password").value(
                        Matchers.containsInAnyOrder(
                                "비밀번호는 적어도 하나 이상의 대문자를 포함해야 합니다.",
                                "비밀번호는 적어도 하나 이상의 소문자를 포함해야 합니다."
                        )
                ));
    }

    @Test
    void 고객은_이미_가입된_이메일_주소로_계정을_만들_수_없다() throws Exception {
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
                .andExpect(jsonPath("$.code").value("CUST_ALREADY_TAKEN_EMAIL"))
                .andExpect(jsonPath("$.message").value("이미 가입된 이메일 주소입니다."))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @WithMockUser(username = "abc@gmail.com", roles = {"USER"})
    @Test
    void 고객은_자신의_정보를_읽을_수_있다() throws Exception {
        mockMvc.perform(get(meURL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("abc@gmail.com"))
                .andExpect(jsonPath("$.authorities.size()").value(1))
                .andExpect(jsonPath("$.authorities[0]").value("ROLE_USER"));
    }
}
