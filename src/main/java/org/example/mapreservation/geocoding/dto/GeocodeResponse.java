package org.example.mapreservation.geocoding.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * geocode 응답 DTO.
 * <a href="https://api.ncloud-docs.com/docs/ai-naver-mapsgeocoding-geocode">레퍼런스 문서</a>
 *
 * @param status       검색 결과 상태 코드
 * @param meta         검색 메타 데이터
 * @param addresses    주소 검색 결과 목록
 * @param errorMessage 에러 메시지
 */
public record GeocodeResponse(String status, Meta meta, List<Address> addresses, String errorMessage) {

    @Getter
    static public class Meta {
        private Integer totalCount;
        private Integer page;
        private Integer count;
    }

    @Getter
    static public class Address {
        private String roadAddress;
        private String jibunAddress;
        private String englishAddress;
        private List<AddressElement> addressElements;
        private String x; // 경도(longitude)
        private String y; // 위도(latitude)
        private Double distance;
    }

    @Getter
    static public class AddressElement {
        private List<String> types;
        private String longName;
        private String shortName;
        private String code;
    }
}

