package org.example.mapreservation.hairshop.service;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.geocoding.dto.GeocodeRequest;
import org.example.mapreservation.geocoding.dto.GeocodeResponse;
import org.example.mapreservation.geocoding.service.GeocodeService;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.dto.CreateHairShopRequest;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class HairShopService {

    private final HairShopRepository hairShopRepository;
    private final OwnerRepository ownerRepository;
    private final GeocodeService geocodeService;

    public Long createHairShop(CreateHairShopRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new CustomException(ErrorCode.OWNER_NOT_FOUND));
        HairShop hairShop;

        GeocodeRequest geocodeRequest = new GeocodeRequest(request.address().getRoadAddress(), null);
        GeocodeResponse geocodeResponse = geocodeService.geocode(geocodeRequest);

        if (!geocodeResponse.addresses().isEmpty()) {
            String x = geocodeResponse.addresses().get(0).getX();
            String y = geocodeResponse.addresses().get(0).getY();
            hairShop = new HairShop(request.name(), request.address(), owner, x, y);
        } else {
            hairShop = new HairShop(request.name(), request.address(), owner);
        }

        return hairShopRepository.save(hairShop).getId();
    }
}
