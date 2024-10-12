package org.example.mapreservation.customer.application.service;

public interface CustomerService {
    Long createCustomer(String email, String notEncodedPassword);
}
