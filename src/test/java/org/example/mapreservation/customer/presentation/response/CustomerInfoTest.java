package org.example.mapreservation.customer.presentation.response;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CustomerInfoTest {

    @Test
    void CustomerInfo는_UserDetail로_부터_생성될수있다() {
        // given
        String username = "abc@gmail.com";
        String password = "Password1!";
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        UserDetails userDetails = new User(username, password, List.of(authority));

        // when
        CustomerInfo info = CustomerInfo.from(userDetails);

        // then
        assertThat(info.username()).isEqualTo("abc@gmail.com");
        assertThat(info.authorities()).isEqualTo(List.of("ROLE_USER"));
    }
}
