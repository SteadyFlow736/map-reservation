package org.example.mapreservation.geocoding.service;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.geocoding.dto.GeocodeRequest;
import org.example.mapreservation.geocoding.dto.GeocodeResponse;
import org.example.mapreservation.geocoding.property.NaverMapsProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GeocodeService {

    private final NaverMapsProperty naverMapsProperty;
    private final RestTemplate restTemplate;

    /**
     * 주소에 대응하는 좌표를 리턴해준다.
     * <a href="https://api.ncloud-docs.com/docs/ai-naver-mapsgeocoding-geocode">naver maps geocode api</a>를 사용한다.
     */
    public GeocodeResponse geocode(GeocodeRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", naverMapsProperty.getApiKeyId());
        headers.set("X-NCP-APIGW-API-KEY", naverMapsProperty.getApiKey());

        String geocodeUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";
        URI uri = UriComponentsBuilder.fromHttpUrl(geocodeUrl)
                .queryParam("query", request.query())
                .queryParamIfPresent("coordinate", Optional.ofNullable(request.coordinate()))
                .encode()
                .build().toUri();

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity, GeocodeResponse.class).getBody();
    }
}
