package org.example.mapreservation.csrf.dto;

import org.springframework.security.web.csrf.CsrfToken;

public record CsrfTokenResponse(CsrfToken csrfToken) {
}
