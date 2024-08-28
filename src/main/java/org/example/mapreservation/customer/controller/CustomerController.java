package org.example.mapreservation.customer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.dto.CustomerCreateRequest;
import org.example.mapreservation.customer.dto.CustomerInfo;
import org.example.mapreservation.customer.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/api/customers")
    public ResponseEntity<Void> createCustomer(@RequestBody @Valid CustomerCreateRequest customerCreateRequest) {
        Long id = customerService.createCustomer(customerCreateRequest);
        return ResponseEntity.created(URI.create("/api/customers/" + id)).build();
    }

    @GetMapping("/api/customers/me")
    public CustomerInfo getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return CustomerInfo.from(user);
    }

}
