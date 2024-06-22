package org.example.mapreservation.customer.repository;

import org.example.mapreservation.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
