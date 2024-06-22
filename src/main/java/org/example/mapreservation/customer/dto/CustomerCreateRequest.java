package org.example.mapreservation.customer.dto;

import org.example.mapreservation.customer.domain.Customer;
import org.springframework.security.crypto.password.PasswordEncoder;

public record CustomerCreateRequest(String email, String password) {
    public Customer toEntity(PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(password);
        return new Customer(email, encodedPassword);
    }
}
