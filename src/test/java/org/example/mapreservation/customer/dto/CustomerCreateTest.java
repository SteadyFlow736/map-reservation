package org.example.mapreservation.customer.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.mapreservation.customer.controller.request.CustomerCreate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class CustomerCreateTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidEmailAndPassword_thenNoConstraintViolations() {
        String email = "abc@gmail.com";
        String password = "Password1!";
        CustomerCreate request = new CustomerCreate(email, password);

        Set<ConstraintViolation<CustomerCreate>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenInvalidEmail_thenConstraintViolation() {
        String email = "invalid-email";
        String password = "Password1!";
        CustomerCreate request = new CustomerCreate(email, password);

        Set<ConstraintViolation<CustomerCreate>> violations =
                validator.validateProperty(request, "email");

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("이메일 형식이 잘못되었습니다.");
    }

    @MethodSource("invalidPasswordProvider")
    @ParameterizedTest
    void whenInvalidPassword_thenConstraintViolation(String password, String violationMessage) {
        String email = "abc@gmail.com";
        CustomerCreate request = new CustomerCreate(email, password);

        Set<ConstraintViolation<CustomerCreate>> violations =
                validator.validateProperty(request, "password");

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(violationMessage);
    }

    static Stream<Arguments> invalidPasswordProvider() {
        String lengthViolationMessage = "비밀번호 길이는 8자리 이상 16자리 이하여야 합니다.";
        String upperCaseViolationMessage = "비밀번호는 적어도 하나 이상의 대문자를 포함해야 합니다.";
        String lowerCaseViolationMessage = "비밀번호는 적어도 하나 이상의 소문자를 포함해야 합니다.";
        String digitViolationMessage = "비밀번호는 적어도 하나 이상의 숫자를 포함해야 합니다.";
        String specialCharacterViolationMessage = "비밀번호는 적어도 다음 특수 문자(@$!%*?&) 중 하나 이상을 포함해야 합니다.";

        return Stream.of(
                Arguments.of("Passd1!", lengthViolationMessage), // 7자리
                Arguments.of("Password1!zzzzzzz", lengthViolationMessage), // 17자리
                Arguments.of("password1!", upperCaseViolationMessage), // 대문자 없음
                Arguments.of("PASSWORD1!", lowerCaseViolationMessage), // 소문자 없음
                Arguments.of("Password!", digitViolationMessage), // 숫자 없음
                Arguments.of("Password1", specialCharacterViolationMessage) // 특수문자 없음
        );
    }

    @Test
    void whenBlankPassword_thenAllConstraintViolations() {
        String email = "abc@gmail.com";
        String password = "";
        CustomerCreate request = new CustomerCreate(email, password);

        Set<ConstraintViolation<CustomerCreate>> violations =
                validator.validateProperty(request, "password");

        assertThat(violations.size()).isEqualTo(6);
    }

}
