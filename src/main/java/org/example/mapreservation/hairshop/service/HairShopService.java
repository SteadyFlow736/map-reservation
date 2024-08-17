package org.example.mapreservation.hairshop.service;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.geocoding.dto.GeocodeRequest;
import org.example.mapreservation.geocoding.dto.GeocodeResponse;
import org.example.mapreservation.geocoding.service.GeocodeService;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.dto.CreateHairShopRequest;
import org.example.mapreservation.hairshop.dto.HairShopDetail;
import org.example.mapreservation.hairshop.dto.HairShopDto;
import org.example.mapreservation.hairshop.dto.HairShopSearchCondition;
import org.example.mapreservation.hairshop.repository.HairShopQueryRepository;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class HairShopService {

    private final HairShopRepository hairShopRepository;
    private final OwnerRepository ownerRepository;
    private final GeocodeService geocodeService;
    private final HairShopQueryRepository hairShopQueryRepository;

    public Long createHairShop(CreateHairShopRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new CustomException(ErrorCode.OWNER_NOT_FOUND));
        HairShop hairShop;

        GeocodeRequest geocodeRequest = new GeocodeRequest(request.address().getRoadAddress(), null);
        GeocodeResponse geocodeResponse = geocodeService.geocode(geocodeRequest);

        if (!geocodeResponse.addresses().isEmpty()) {
            String longitude = geocodeResponse.addresses().get(0).getX(); // 경도
            String latitude = geocodeResponse.addresses().get(0).getY(); // 위도
            hairShop = new HairShop(request.name(), request.address(), owner, longitude, latitude);
        } else {
            hairShop = new HairShop(request.name(), request.address(), owner);
        }

        return hairShopRepository.save(hairShop).getId();
    }

    public Page<HairShopDto> searchHairShop(HairShopSearchCondition searchCondition, Pageable pageable) {
        return hairShopQueryRepository.search(searchCondition, pageable);
    }

    public HairShopDetail getHairShopDetail(Long hairShopId) {
        HairShop hairShop = hairShopRepository.findById(hairShopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));
        return HairShopDetail.from(hairShop);
    }

}
