package org.example.mapreservation.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mapreservation.customer.controller.port.CustomerService;
import org.example.mapreservation.customer.controller.request.CustomerCreate;
import org.example.mapreservation.customer.providers.InvalidPasswordProvider;
import org.example.mapreservation.exception.GlobalExceptionHandler;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerControllerValidationTest {

    CustomerService customerService;
    CustomerController customerController;
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        customerService = mock(CustomerService.class);
        customerController = CustomerController.builder()
                .customerService(customerService)
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @Test
    void 회원가입_신청_시_요구사항에_맞는_이메일과_비밀번호를_제공해야_한다() throws Exception {
        // given
        when(customerService.createCustomer(any(), any())).thenReturn(1L);
        String email = "abc@gmail.com";
        String password = "Password1!";

        CustomerCreate request = CustomerCreate.builder()
                .email(email)
                .password(password)
                .build();

        // when, then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void 회원가입_신청_시_이메일을_빈_칸으로_제공하면_에러_메시지를_리턴한다() throws Exception {
        // given
        when(customerService.createCustomer(any(), any())).thenReturn(1L);
        String email = "";
        String password = "Password1!";

        CustomerCreate request = CustomerCreate.builder()
                .email(email)
                .password(password)
                .build();

        // when, then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CMN_BINDING_ERROR"))
                .andExpect(jsonPath("$.message").value("필드 바인딩 실패"))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors.email").isArray())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[0]").value("이메일은 필수 제출 항목입니다."));
    }

    @Test
    void 회원가입_신청_시_올바르지_않은_형식의_이메일을_제공하면_에러_메시지를_리턴한다() throws Exception {
        // given
        when(customerService.createCustomer(any(), any())).thenReturn(1L);
        String email = "zzz.com";
        String password = "Password1!";

        CustomerCreate request = CustomerCreate.builder()
                .email(email)
                .password(password)
                .build();

        // when, then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CMN_BINDING_ERROR"))
                .andExpect(jsonPath("$.message").value("필드 바인딩 실패"))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors.email").isArray())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[0]").value("이메일 형식이 잘못되었습니다."));
    }

    @ArgumentsSource(InvalidPasswordProvider.class)
    @ParameterizedTest
    void 회원가입_신청_시_올바르지_않은_형식의_비밀번호를_제공하면_에러_메시지를_리턴한다(String password, String violationMessage) throws Exception {
        // given
        when(customerService.createCustomer(any(), any())).thenReturn(1L);
        String email = "abc@gmail.com";

        CustomerCreate request = CustomerCreate.builder()
                .email(email)
                .password(password)
                .build();

        // when, then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CMN_BINDING_ERROR"))
                .andExpect(jsonPath("$.message").value("필드 바인딩 실패"))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors.password").isArray())
                .andExpect(jsonPath("$.errors.password.length()").value(1))
                .andExpect(jsonPath("$.errors.password[0]").value(violationMessage));
    }

    @Test
    void 회원가입_신청_시_빈_비밀번호를_제공하면_에러_메시지를_리턴한다() throws Exception {
        // given
        when(customerService.createCustomer(any(), any())).thenReturn(1L);
        String email = "abc@gmail.com";
        String password = "";

        CustomerCreate request = CustomerCreate.builder()
                .email(email)
                .password(password)
                .build();

        // when, then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CMN_BINDING_ERROR"))
                .andExpect(jsonPath("$.message").value("필드 바인딩 실패"))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors.password").isArray())
                .andExpect(jsonPath("$.errors.password.length()").value(6))
                .andExpect(jsonPath("$.errors.password").value(
                        Matchers.containsInAnyOrder(
                                "비밀번호는 필수 제출 항목입니다.",
                                "비밀번호 길이는 8자리 이상 16자리 이하여야 합니다.",
                                "비밀번호는 적어도 하나 이상의 대문자를 포함해야 합니다.",
                                "비밀번호는 적어도 하나 이상의 소문자를 포함해야 합니다.",
                                "비밀번호는 적어도 하나 이상의 숫자를 포함해야 합니다.",
                                "비밀번호는 적어도 다음 특수 문자(@$!%*?&) 중 하나 이상을 포함해야 합니다."
                        )
                ));
    }

    @Test
    void 회원가입_신청_시_이메일과_비밀번호_에러_메시지를_동시에_받을_수_있다() throws Exception {
        // given
        when(customerService.createCustomer(any(), any())).thenReturn(1L);
        String email = "";
        String password = "";

        CustomerCreate request = CustomerCreate.builder()
                .email(email)
                .password(password)
                .build();

        // when, then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CMN_BINDING_ERROR"))
                .andExpect(jsonPath("$.message").value("필드 바인딩 실패"))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors.email").isArray())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[0]").value("이메일은 필수 제출 항목입니다."))
                .andExpect(jsonPath("$.errors.password").isArray())
                .andExpect(jsonPath("$.errors.password.length()").value(6))
                .andExpect(jsonPath("$.errors.password").value(
                        Matchers.containsInAnyOrder(
                                "비밀번호는 필수 제출 항목입니다.",
                                "비밀번호 길이는 8자리 이상 16자리 이하여야 합니다.",
                                "비밀번호는 적어도 하나 이상의 대문자를 포함해야 합니다.",
                                "비밀번호는 적어도 하나 이상의 소문자를 포함해야 합니다.",
                                "비밀번호는 적어도 하나 이상의 숫자를 포함해야 합니다.",
                                "비밀번호는 적어도 다음 특수 문자(@$!%*?&) 중 하나 이상을 포함해야 합니다."
                        )
                ));
    }

}
