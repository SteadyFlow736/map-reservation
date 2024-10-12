package org.example.mapreservation.customer.application;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.application.repository.CustomerRepository;
import org.example.mapreservation.customer.application.service.CustomerService;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    /**
     * 계정을 생성한다.
     *
     * @return 생성된 계정의 고유 아이디 리턴
     * @throws CustomException 이미 등록된 이메일로 계정 생성 요청 시
     */
    @Transactional
    public Long createCustomer(String email, String notEncodedPassword) {
        Customer customer = Customer.builder()
                .email(email)
                .password(passwordEncoder.encode(notEncodedPassword))
                .build();
        try {
            // save 대신 saveAndFlush 하여 바로 쓰기 쿼리를 날리도록 하였다.
            // 이 시점에 DataIntegrityViolationException 을 잡을 수 있도록 하기 위해서이다.
            return customerRepository.saveAndFlush(customer).getId();
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException(ErrorCode.CUST_ALREADY_TAKEN_EMAIL, ex);
        }
    }
}
