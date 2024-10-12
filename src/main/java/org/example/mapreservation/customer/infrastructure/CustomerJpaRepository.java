package org.example.mapreservation.customer.infrastructure;

import org.example.mapreservation.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
