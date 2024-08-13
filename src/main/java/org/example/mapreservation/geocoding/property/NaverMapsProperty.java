package org.example.mapreservation.geocoding.property;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter
@Component
@ConfigurationProperties("naver-maps")
public class NaverMapsProperty {

    @NotBlank
    private String apiKeyId;

    @NotBlank
    private String apiKey;

}
