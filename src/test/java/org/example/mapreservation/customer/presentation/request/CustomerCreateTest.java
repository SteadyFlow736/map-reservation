package org.example.mapreservation.customer.presentation.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.example.mapreservation.customer.providers.InvalidPasswordProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class CustomerCreateTest {

    private static final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void 올바른_이메일과_비밀번호는_아무것도_위반하지않는다() {
        // given
        String email = "abc@gmail.com";
        String password = "Password1!";
        CustomerCreate request = new CustomerCreate(email, password);

        // when
        Set<ConstraintViolation<CustomerCreate>> violations =
                validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void 이메일_형식에_만족하지_않는_값은_위반된다() {
        // given
        String email = "invalid-email";
        String password = "Password1!";
        CustomerCreate request = new CustomerCreate(email, password);

        // when
        Set<ConstraintViolation<CustomerCreate>> violations =
                validator.validateProperty(request, "email");

        // then
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("이메일 형식이 잘못되었습니다.");
    }

    @ArgumentsSource(InvalidPasswordProvider.class)
    @ParameterizedTest
    void 올바르지_않은_형식의_비밀번호는_위반된다(String password, String violationMessage) {
        // given
        String email = "abc@gmail.com";
        CustomerCreate request = new CustomerCreate(email, password);

        // when
        Set<ConstraintViolation<CustomerCreate>> violations =
                validator.validateProperty(request, "password");

        // then
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(violationMessage);
    }

    @Test
    void 빈_비밀번호는_위반된다() {
        // givenn
        String email = "abc@gmail.com";
        String password = "";
        CustomerCreate request = new CustomerCreate(email, password);

        // when
        Set<ConstraintViolation<CustomerCreate>> violations =
                validator.validateProperty(request, "password");

        // then
        assertThat(violations.size()).isEqualTo(6);
        assertThat(violations.stream().map(ConstraintViolation::getMessage)).containsExactlyInAnyOrder(
                "비밀번호는 필수 제출 항목입니다.",
                "비밀번호 길이는 8자리 이상 16자리 이하여야 합니다.",
                "비밀번호는 적어도 하나 이상의 대문자를 포함해야 합니다.",
                "비밀번호는 적어도 하나 이상의 소문자를 포함해야 합니다.",
                "비밀번호는 적어도 하나 이상의 숫자를 포함해야 합니다.",
                "비밀번호는 적어도 다음 특수 문자(@$!%*?&) 중 하나 이상을 포함해야 합니다."
        );
    }

}
