package org.example.mapreservation.initialization;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.hairshop.dto.CreateHairShopRequest;
import org.example.mapreservation.hairshop.service.HairShopService;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataLoadService {

    private final HairShopService hairShopService;
    private final OwnerRepository ownerRepository;

    public void load() {
        Owner owner = new Owner("소유자1");
        ownerRepository.save(owner);

        Address address = new Address("서울 종로구 자하문로7길 12 2층", "101호");
        CreateHairShopRequest request = new CreateHairShopRequest("헤어샵1", address, owner.getId());
        hairShopService.createHairShop(request);
    }
}
