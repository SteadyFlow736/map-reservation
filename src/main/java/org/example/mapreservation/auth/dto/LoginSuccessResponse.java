package org.example.mapreservation.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * 로그인 성공 응답
 * @param username 유저 email
 * @param authorities 유저 권한 리스트
 */
public record LoginSuccessResponse(String username, Collection<String> authorities) {

    public static LoginSuccessResponse from(User user) {
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return new LoginSuccessResponse(user.getUsername(), authorities);
    }
}
