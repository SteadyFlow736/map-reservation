package org.example.mapreservation.hairshop.application;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.geocoding.application.service.GeocodeService;
import org.example.mapreservation.geocoding.domain.GeocodeRequest;
import org.example.mapreservation.geocoding.domain.GeocodeResponse;
import org.example.mapreservation.hairshop.application.service.HairShopService;
import org.example.mapreservation.hairshop.domain.HairShopCreate;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.domain.HairShopDetailResponse;
import org.example.mapreservation.hairshop.domain.HairShopResponse;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.example.mapreservation.hairshop.application.repository.HairShopQueryRepository;
import org.example.mapreservation.hairshop.application.repository.HairShopRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class HairShopServiceImpl implements HairShopService {

    private final HairShopRepository hairShopRepository;
    private final HairShopQueryRepository hairShopQueryRepository;
    private final OwnerRepository ownerRepository;
    private final GeocodeService geocodeService;

    public Long createHairShop(HairShopCreate request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new CustomException(ErrorCode.OWNER_NOT_FOUND));
        HairShop hairShop = HairShop.builder()
                .name(request.name())
                .address(request.address())
                .owner(owner)
                .imageUrls(request.imageUrls())
                .build();

        GeocodeRequest geocodeRequest = new GeocodeRequest(request.address().getRoadAddress(), null);
        GeocodeResponse geocodeResponse = geocodeService.geocode(geocodeRequest);

        if (!geocodeResponse.addresses().isEmpty()) {
            hairShop.updateLongitudeLatitude(
                    geocodeResponse.addresses().get(0).getX(),
                    geocodeResponse.addresses().get(0).getY());
        }

        return hairShopRepository.save(hairShop).getId();
    }

    public Page<HairShopResponse> searchHairShop(HairShopSearchCondition searchCondition, Pageable pageable) {
        return hairShopQueryRepository.search(searchCondition, pageable).map(HairShopResponse::from);
    }

    public HairShopDetailResponse getHairShopDetail(Long hairShopId) {
        HairShop hairShop = hairShopRepository.findById(hairShopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));
        return HairShopDetailResponse.from(hairShop);
    }

}
