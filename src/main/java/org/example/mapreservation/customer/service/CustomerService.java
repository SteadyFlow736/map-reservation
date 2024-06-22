package org.example.mapreservation.customer.service;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.dto.CustomerCreateRequest;
import org.example.mapreservation.customer.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    public Long createCustomer(CustomerCreateRequest customerCreateRequest) {
        Customer customer = customerCreateRequest.toEntity(passwordEncoder);
        return customerRepository.save(customer).getId();
    }
}
