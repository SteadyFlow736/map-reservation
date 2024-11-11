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
 * Customer 로그인 시 유저 정보를 가져오는 UserDetailsService 정의
 * <p>
 * <strong>CustomerDetailsService::loadUserByUsername 호출 순서</strong>
 * <p>
 * UsernamePasswordAuthenticationFilter(AbstractAuthenticationProcessingFilter)::doFilter
 * <p>
 * UsernamePasswordAuthenticationFilter::attemptAuthentication
 * <p>
 * ProviderManager::authenticate
 * <p>
 * DaoAuthenticationProvider(AbstractUserDetailsAuthenticationProvider)::authenticate
 * <p>
 * DaoAuthenticationProvider::retrieveUser
 * <p>
 * CustomerDetailsService(UserDetailsService)::loadUserByUsername
 */
@RequiredArgsConstructor
@Component
public class CustomerDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(username)
                // UsernameNotFoundException 핸들링 과정
                // 1) DaoAuthenticationProvider(AbstractUserDetailsAuthenticationProvider)::authenticate 에서 catch,
                // BadCredentialsException throw.
                // 2) UsernamePasswordAuthenticationFilter(AbstractAuthenticationProcessingFilter)::doFilter 에서
                // BadCredentialsException catch, unsuccessfulAuthentication 호출
                // 3) AuthenticationFailureHandler::onAuthenticationFailure 호출
                // 이때 bean으로 등록한 AuthenticationFailureHandler인 CustomAuthenticationFailerHandler를 사용
                .orElseThrow(() -> new UsernameNotFoundException("username: " + username));
        return new User(customer.getEmail(), customer.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
    }
}
