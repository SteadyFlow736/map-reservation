package org.example.mapreservation.hairshop.service;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
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

    public Long createHairShop(CreateHairShopRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new CustomException(ErrorCode.OWNER_NOT_FOUND));
        HairShop hairShop = new HairShop(request.name(), request.address(), owner);
        return hairShopRepository.save(hairShop).getId();
    }
}
