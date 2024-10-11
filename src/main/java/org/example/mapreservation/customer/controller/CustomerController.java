package org.example.mapreservation.customer.controller;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.controller.port.CustomerService;
import org.example.mapreservation.customer.controller.request.CustomerCreate;
import org.example.mapreservation.customer.controller.response.CustomerInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Builder
@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/api/customers")
    public ResponseEntity<Void> createCustomer(@RequestBody @Valid CustomerCreate request) {
        Long id = customerService.createCustomer(request.email(), request.password());
        return ResponseEntity.created(URI.create("/api/customers/" + id)).build();
    }

    @GetMapping("/api/customers/me")
    public ResponseEntity<CustomerInfo> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(CustomerInfo.from(user));
    }

}
