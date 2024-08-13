package org.example.mapreservation.geocoding.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * geocode 요청 DTO.
 * <a href="https://api.ncloud-docs.com/docs/ai-naver-mapsgeocoding-geocode">레퍼런스 문서</a>
 *
 * @param query      좌표로 변환될 주소
 * @param coordinate 검색 중심 좌표. 'lon,lat' 형식. 응답의 distance가 바로 이 중심 좌표로부터의 거리다.
 */
@Validated
public record GeocodeRequest(@NotBlank String query, String coordinate) {
}
