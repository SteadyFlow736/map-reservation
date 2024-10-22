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
            // UPDATE: Customer의 키 생성 전략을 IDENTITY로 변경하면 MySQL 대상으로는 바로 sql을 보내 저장하고 id를 얻어온다.
            // 따라서 Customer의 키 생성 전략을 IDNETITY로 설정했다면 그냥 save를 시행해도 된다.
            // 하지만 언제나 MySQL일 것이라는 보장은 없으므로 saveAndFlush를 유지하는 것이 더 나을 것이다.
            return customerRepository.saveAndFlush(customer).getId();
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException(ErrorCode.CUST_ALREADY_TAKEN_EMAIL, ex);
        }
    }
}
