package org.example.mapreservation.customer.application.repository;

import org.example.mapreservation.customer.domain.Customer;

import java.util.Optional;

public interface CustomerRepository {
    Customer saveAndFlush(Customer customer);

    Optional<Customer> findByEmail(String username);

    Customer save(Customer anotherCustomer);
}
