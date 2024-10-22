package org.example.mapreservation.hairshop.application;

import jakarta.persistence.EntityManager;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.geocoding.application.service.GeocodeService;
import org.example.mapreservation.geocoding.domain.GeocodeResponse;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.domain.HairShopCreate;
import org.example.mapreservation.hairshop.domain.HairShopDetailResponse;
import org.example.mapreservation.hairshop.domain.HairShopResponse;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.example.mapreservation.hairshop.infrastructure.HairShopJpaRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SqlGroup({
        @Sql(value = "/sql/delete-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@SpringBootTest
class HairShopServiceImplIntegrationTest {

    @Autowired
    HairShopServiceImpl hairShopService;
    @MockBean
    GeocodeService geocodeService;
    @Autowired
    HairShopJpaRepository hairShopRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    void 헤어샵_생성할_수_있다() {
        // given - Owner 준비
        Owner owner = ownerRepository.save(new Owner("주인"));

        // given - HairShopCreate 준비
        String name = "헤어샵";
        String roadAddress = "도로주소";
        String detailAddress = "상세주소";
        Long ownerId = owner.getId();
        List<String> urls = List.of("url1", "url2", "url3");
        HairShopCreate request = new HairShopCreate(name, roadAddress, detailAddress, ownerId, urls);

        List<HairShop> before = hairShopRepository.findAll();
        assertThat(before.size()).isEqualTo(0);

        // given - GeocodeService.geocode mocking
        String longitude = "10.0";
        String latitude = "20.0";
        GeocodeResponse.Address address = new GeocodeResponse.Address();
        address.updateLongitude(longitude);
        address.updateLatitude(latitude);
        when(geocodeService.geocode(any())).thenReturn(
                new GeocodeResponse(null, null, List.of(address), null)
        );

        // when
        Long hairShop = hairShopService.createHairShop(request);

        // then
        assertThat(hairShop).isNotNull();
        List<HairShop> after = hairShopRepository.findAll();
        assertThat(after.size()).isEqualTo(1);
        assertThat(after.get(0).getName()).isEqualTo("헤어샵");
        assertThat(after.get(0).getAddress().getRoadAddress()).isEqualTo("도로주소");
        assertThat(after.get(0).getAddress().getDetailAddress()).isEqualTo("상세주소");
        assertThat(after.get(0).getOwner().getId()).isEqualTo(ownerId);
        assertThat(after.get(0).getLongitude()).isEqualTo("10.0");
        assertThat(after.get(0).getLatitude()).isEqualTo("20.0");
        assertThat(after.get(0).getImageUrls()).isEqualTo(List.of("url1", "url2", "url3"));
    }

    @Test
    void 헤어샵을_페이지_검색할_수_있다() {
        // given
        Owner owner = new Owner("주인");
        ownerRepository.save(owner);

        List<HairShop> contents = List.of(
                new HairShop(1L, "헤어샵1", new Address("도로주소", "상세주소"), owner, "10.0", "20.0", null),
                new HairShop(2L, "헤어샵2", new Address("도로주소", "상세주소"), owner, "10.0", "20.0", null),
                new HairShop(3L, "헤어샵3", new Address("도로주소", "상세주소"), owner, "10.0", "20.0", null)
        );
        hairShopRepository.saveAll(contents);

        String searchTerm = "헤어샵";
        HairShopSearchCondition searchCondition = new HairShopSearchCondition(searchTerm);

        int pageNumber = 0;
        int pageSize = 2;
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));

        // when
        Page<HairShopResponse> hairShopResponses = hairShopService.searchHairShop(searchCondition, pageRequest);

        // then
        assertThat(hairShopResponses.getTotalElements()).isEqualTo(3);
        assertThat(hairShopResponses.getContent().size()).isEqualTo(2);
        assertThat(hairShopResponses.getContent().get(0).shopId()).isEqualTo(1L);
        assertThat(hairShopResponses.getContent().get(0).shopName()).isEqualTo("헤어샵1");
        assertThat(hairShopResponses.getContent().get(0).longitude()).isEqualTo("10.0");
        assertThat(hairShopResponses.getContent().get(0).latitude()).isEqualTo("20.0");
        assertThat(hairShopResponses.getContent().get(0).images()).isEmpty();
        assertThat(hairShopResponses.getContent().get(1).shopId()).isEqualTo(2L);
    }

    @Test
    void 헤어샵_상세_정보를_조회할_수_있다() {
        // given
        Owner owner = new Owner("주인");
        owner = ownerRepository.save(owner);

        HairShop hairShop = HairShop.builder()
                .name("헤어샵")
                .owner(owner)
                .address(new Address("도로주소", "상세주소"))
                .longitude("10.0")
                .latitude("20.0")
                .imageUrls(List.of("url1", "url2", "url3"))
                .build();
        hairShopRepository.save(hairShop);

        // when
        HairShopDetailResponse hairShopDetail = hairShopService.getHairShopDetail(hairShop.getId());

        // then
        assertThat(hairShopDetail.shopId()).isNotNull();
        assertThat(hairShopDetail.shopName()).isEqualTo("헤어샵");
        assertThat(hairShopDetail.longitude()).isEqualTo("10.0");
        assertThat(hairShopDetail.latitude()).isEqualTo("20.0");
        assertThat(hairShopDetail.imageUrls()).isEqualTo(List.of("url1", "url2", "url3"));
        assertThat(hairShopDetail.roadAddress()).isEqualTo("도로주소");
        assertThat(hairShopDetail.detailAddress()).isEqualTo("상세주소");
    }


    @Test
    void 헤어샵이_없다면_예외를_던진다() {
        // given
        List<HairShop> all = hairShopRepository.findAll();
        assertThat(all.size()).isEqualTo(0);

        // when, then
        assertThatThrownBy(() -> hairShopService.getHairShopDetail(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 헤어샵입니다.");
    }
}
