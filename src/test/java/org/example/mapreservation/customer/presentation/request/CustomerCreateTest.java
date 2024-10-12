package org.example.mapreservation.customer.presentation.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.mapreservation.customer.providers.InvalidPasswordProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Set;

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

    @ArgumentsSource(InvalidPasswordProvider.class)
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
