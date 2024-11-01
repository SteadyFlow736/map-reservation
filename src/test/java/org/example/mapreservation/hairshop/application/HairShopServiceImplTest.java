package org.example.mapreservation.hairshop.application;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.geocoding.application.GeocodeServiceImpl;
import org.example.mapreservation.geocoding.domain.GeocodeRequest;
import org.example.mapreservation.geocoding.domain.GeocodeResponse;
import org.example.mapreservation.hairshop.application.repository.HairShopQueryRepository;
import org.example.mapreservation.hairshop.application.repository.HairShopRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.domain.HairShopCreate;
import org.example.mapreservation.hairshop.domain.HairShopDetailResponse;
import org.example.mapreservation.hairshop.domain.HairShopResponse;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HairShopServiceImplTest {

    HairShopRepository hairShopRepository;
    HairShopQueryRepository hairShopQueryRepository;
    OwnerRepository ownerRepository;
    GeocodeServiceImpl geocodeService;
    HairShopServiceImpl hairShopService;

    @BeforeEach
    void setUp() {
        hairShopRepository = mock(HairShopRepository.class);
        hairShopQueryRepository = mock(HairShopQueryRepository.class);
        ownerRepository = mock(OwnerRepository.class);
        geocodeService = mock(GeocodeServiceImpl.class);
        hairShopService =
                new HairShopServiceImpl(hairShopRepository, hairShopQueryRepository, ownerRepository, geocodeService);
    }

    @Test
    void 헤어샵_생성할_수_있다() {
        // given - HairShopCreqte 생성
        String hairShopName = "헤어샵";
        String roadAddress = "도로주소";
        String detailAddress = "상세주소";
        Long ownerId = 1L;
        List<String> imageUrls = List.of("url1", "url2", "url3");
        HairShopCreate request = new HairShopCreate(hairShopName, roadAddress, detailAddress, ownerId, imageUrls);

        // given - ownerRepository.findById mocking
        Owner owner = new Owner(1L, "주인");
        when(ownerRepository.findById(1L)).thenReturn(
                Optional.of(owner)
        );

        // given - geocodeService.geocode mocking
        String longitude = "10.0";
        String latitude = "20.0";
        GeocodeResponse.Address address = new GeocodeResponse.Address();
        address.updateLongitude(longitude);
        address.updateLatitude(latitude);
        when(geocodeService.geocode(any())).thenReturn(
                new GeocodeResponse(null, null, List.of(address), null)
        );

        // given - hairShopRepository.save mocking
        when(hairShopRepository.save(any())).thenReturn(
                HairShop.builder()
                        .id(1L)
                        .name(hairShopName)
                        .owner(owner)
                        .address(new Address(roadAddress, detailAddress))
                        .longitude(longitude)
                        .latitude(latitude)
                        .imageUrls(imageUrls)
                        .build()
        );

        // given - ArgumentCaptors
        ArgumentCaptor<GeocodeRequest> geocodeRequestArgumentCaptor = ArgumentCaptor.forClass(GeocodeRequest.class);
        ArgumentCaptor<HairShop> hairShopArgumentCaptor = ArgumentCaptor.forClass(HairShop.class);

        // when
        Long hairShop = hairShopService.createHairShop(request);

        // then
        assertThat(hairShop).isEqualTo(1L);

        verify(hairShopRepository, times(1)).save(hairShopArgumentCaptor.capture());
        verify(geocodeService, times(1)).geocode(geocodeRequestArgumentCaptor.capture());
        GeocodeRequest geocodeRequestArgumentCaptorValue = geocodeRequestArgumentCaptor.getValue();
        HairShop hairShopArgumentCaptorValue = hairShopArgumentCaptor.getValue();

        assertThat(geocodeRequestArgumentCaptorValue.query()).isEqualTo("도로주소");
        assertThat(geocodeRequestArgumentCaptorValue.coordinate()).isNull();
        assertThat(hairShopArgumentCaptorValue.getName()).isEqualTo("헤어샵");
        assertThat(hairShopArgumentCaptorValue.getAddress().getRoadAddress()).isEqualTo("도로주소");
        assertThat(hairShopArgumentCaptorValue.getAddress().getDetailAddress()).isEqualTo("상세주소");
        assertThat(hairShopArgumentCaptorValue.getOwner().getId()).isEqualTo(1L);
        assertThat(hairShopArgumentCaptorValue.getOwner().getName()).isEqualTo("주인");
        assertThat(hairShopArgumentCaptorValue.getImageUrls()).isEqualTo(List.of("url1", "url2", "url3"));
        assertThat(hairShopArgumentCaptorValue.getLongitude()).isEqualTo("10.0");
        assertThat(hairShopArgumentCaptorValue.getLatitude()).isEqualTo("20.0");
    }

    @Test
    void 헤어샵을_페이지_검색할_수_있다() {
        // given
        String searchTerm = "헤어샵";
        HairShopSearchCondition searchCondition = HairShopSearchCondition.builder()
                .searchTerm(searchTerm)
                .build();

        int pageNumber = 0;
        int pageSize = 2;
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));

        Owner owner = new Owner(1L, "주인");
        List<HairShop> contents = List.of(
                HairShop.builder()
                        .id(1L)
                        .name("헤어샵1")
                        .address(new Address("도로주소", "상세주소"))
                        .owner(owner)
                        .longitude("10.0")
                        .latitude("20.0")
                        .build(),
                HairShop.builder()
                        .id(2L)
                        .name("헤어샵2")
                        .address(new Address("도로주소", "상세주소"))
                        .owner(owner)
                        .longitude("10.0")
                        .latitude("20.0")
                        .build()
        );
        Page<HairShop> hairShopPage = new PageImpl<>(contents);

        when(hairShopQueryRepository.search(searchCondition, pageRequest)).thenReturn(hairShopPage);

        // when
        Page<HairShopResponse> hairShopResponses = hairShopService.searchHairShop(searchCondition, pageRequest);

        // then
        assertThat(hairShopResponses.getTotalElements()).isEqualTo(contents.size());
        assertThat(hairShopResponses.getContent().get(0).shopId()).isEqualTo(1L);
        assertThat(hairShopResponses.getContent().get(0).shopName()).isEqualTo("헤어샵1");
        assertThat(hairShopResponses.getContent().get(0).longitude()).isEqualTo("10.0");
        assertThat(hairShopResponses.getContent().get(0).latitude()).isEqualTo("20.0");
        assertThat(hairShopResponses.getContent().get(0).images()).isEmpty();
    }

    @Test
    void 헤어샵_상세_정보를_조회할_수_있다() {
        // given
        when(hairShopRepository.findById(1L)).thenReturn(Optional.of(
                HairShop.builder()
                        .id(1L)
                        .name("헤어샵")
                        .owner(new Owner(34L, "주인"))
                        .address(new Address("도로주소", "상세주소"))
                        .longitude("10.0")
                        .latitude("20.0")
                        .imageUrls(List.of("url1", "url2", "url3"))
                        .build()
        ));

        // when
        HairShopDetailResponse hairShopDetail = hairShopService.getHairShopDetail(1L);

        // then
        assertThat(hairShopDetail.shopId()).isEqualTo(1L);
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
        when(hairShopRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> hairShopService.getHairShopDetail(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 헤어샵입니다.");
    }
}
