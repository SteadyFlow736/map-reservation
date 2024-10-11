package org.example.mapreservation.customer.controller.response;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 고객의 정보를 담는 객체
 *
 * @param username    username (예: abc@gmail.com)
 * @param authorities 권한 (예: {"ROLE_USER"}
 */
public record CustomerInfo(
        String username,
        Collection<String> authorities) {

    public static CustomerInfo from(UserDetails userDetails) {
        List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return new CustomerInfo(userDetails.getUsername(), authorities);
    }

}
