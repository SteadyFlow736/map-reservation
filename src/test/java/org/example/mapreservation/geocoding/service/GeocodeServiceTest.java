package org.example.mapreservation.geocoding.service;

import org.example.mapreservation.geocoding.dto.GeocodeRequest;
import org.example.mapreservation.geocoding.dto.GeocodeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GeocodeServiceTest {

    @Autowired
    GeocodeService geocodeService;

    @Test
    void test() {
        // given
        String query = "분당구 불정로 6";
        String coordinate = null;

        // when
        GeocodeResponse response = geocodeService.geocode(new GeocodeRequest(query, coordinate));
        //System.out.println(response);

        // then
        assertThat(response.status()).isEqualTo("OK");

        assertThat(response.meta().getTotalCount()).isEqualTo(1);
        assertThat(response.meta().getPage()).isEqualTo(1);
        assertThat(response.meta().getCount()).isEqualTo(1);

        GeocodeResponse.Address address = response.addresses().get(0);
        assertThat(address.getRoadAddress()).isEqualTo("경기도 성남시 분당구 불정로 6 NAVER그린팩토리");
        assertThat(address.getJibunAddress()).isEqualTo("경기도 성남시 분당구 정자동 178-1 NAVER그린팩토리");
        assertThat(address.getEnglishAddress()).isEqualTo("6, Buljeong-ro, Bundang-gu, Seongnam-si, Gyeonggi-do, Republic of Korea");

        GeocodeResponse.AddressElement addressElement = address.getAddressElements().get(0);
        assertThat(addressElement.getTypes().get(0)).isEqualTo("SIDO");
        assertThat(addressElement.getLongName()).isEqualTo("경기도");
        assertThat(addressElement.getShortName()).isEqualTo("경기도");
        // 나머지 AddressElement 생략

        assertThat(address.getX()).isEqualTo("127.1054328");
        assertThat(address.getY()).isEqualTo("37.3595963");
        assertThat(address.getDistance()).isEqualTo(0);
        assertThat(response.errorMessage()).isEqualTo("");
    }
}
