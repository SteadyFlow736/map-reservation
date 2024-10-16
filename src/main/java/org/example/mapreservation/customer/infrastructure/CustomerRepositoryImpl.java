package org.example.mapreservation.customer.infrastructure;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.application.repository.CustomerRepository;
import org.example.mapreservation.customer.domain.Customer;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;

    @Override
    public Customer saveAndFlush(Customer customer) {
        return customerJpaRepository.saveAndFlush(customer);
    }

    @Override
    public Optional<Customer> findByEmail(String username) {
        return customerJpaRepository.findByEmail(username);
    }

    @Override
    public Customer save(Customer customer) {
        return customerJpaRepository.save(customer);
    }
}
