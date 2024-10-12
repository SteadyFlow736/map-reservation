package org.example.mapreservation.customer.application;

import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.infrastructure.CustomerJpaRepository;
import org.example.mapreservation.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SqlGroup({
        @Sql(value = "/sql/delete-all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
@SpringBootTest
class CustomerServiceImplIntegrationTest {

    @Autowired
    private CustomerServiceImpl customerService;
    @Autowired
    private CustomerJpaRepository customerRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void 고객을_생성_할_수_있다() {
        // given
        String email = "abc@gmail.com";
        String password = "Password1!";
        String encodedPassword = "encodedPassword";

        // given - collaborators
        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // when
        Long id = customerService.createCustomer(email, password);

        // then
        Customer foundCustomer = customerRepository.findById(id).orElseThrow();
        assertThat(foundCustomer.getEmail()).isEqualTo("abc@gmail.com");
        assertThat(foundCustomer.getPassword()).isEqualTo("encodedPassword");
    }

    @SqlGroup({
            @Sql(value = "/sql/customer-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "/sql/delete-all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    @Test
    void 고객이_중복된_이메일로_가입_시도_시_예외를_던진다() {
        // given
        String email = "abc@gmail.com";
        String password = "Password1!";
        String encodedPassword = "encodedPassword";

        // given - collaborators
        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // when, then
        assertThatThrownBy(() -> customerService.createCustomer(email, password))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 가입된 이메일 주소입니다.");
    }
}
