package org.example.mapreservation.customer.controller.port;

public interface CustomerService {
    Long createCustomer(String email, String notEncodedPassword);
}
