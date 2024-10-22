package org.example.mapreservation.auth;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.application.repository.CustomerRepository;
import org.example.mapreservation.customer.domain.Customer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Customer 로그인 시 사용될 UserDetailsService 정의
 */
@RequiredArgsConstructor
@Component
public class CustomerDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(username)
                // TODO: UsernameNotFoundException 대신 CustomException 사용 가능한지 알아보기
                .orElseThrow(() -> new UsernameNotFoundException("username: " + username));
        return new User(customer.getEmail(), customer.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
    }
}
