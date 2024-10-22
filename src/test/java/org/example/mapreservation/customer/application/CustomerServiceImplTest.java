package org.example.mapreservation.customer.application;

import org.example.mapreservation.customer.application.repository.CustomerRepository;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

class CustomerServiceImplTest {

    private final PasswordEncoder passwordEncoder =
            Mockito.mock(PasswordEncoder.class);
    private final CustomerRepository customerRepository =
            Mockito.mock(CustomerRepository.class);
    private final CustomerServiceImpl customerService =
            new CustomerServiceImpl(passwordEncoder, customerRepository);

    @Test
    void 고객_생성_시_비밀번호를_인코딩한다() {
        // given
        String email = "abc@gmail.com";
        String password = "Password1!";
        String encodedPassword = "encodedPassword";

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // given - collaborators
        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        Mockito.when(customerRepository.saveAndFlush(Mockito.any())).thenReturn(Customer.builder()
                .id(1L)
                .email(email)
                .password(password)
                .build()
        );

        // when
        Long id = customerService.createCustomer(email, password);

        // then
        assertThat(id).isEqualTo(1L);

        Mockito.verify(customerRepository, Mockito.times(1))
                .saveAndFlush(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getEmail()).isEqualTo("abc@gmail.com");
        assertThat(capturedCustomer.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void 고객이_중복된_이메일로_가입_시도_시_예외를_던진다() {
        // given
        String email = "abc@gmail.com";
        String password = "Password1!";

        // given - collaborators
        Mockito.when(customerRepository.saveAndFlush(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException(""));

        // when, then
        assertThatThrownBy(() -> customerService.createCustomer(email, password))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 가입된 이메일 주소입니다.");
    }
}
