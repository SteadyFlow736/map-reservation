package org.example.mapreservation.initialization;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataLoadService {

    private final HairShopRepository hairShopRepository;
    private final OwnerRepository ownerRepository;

    public void load() {
        Owner owner = new Owner("소유자1");
        Address address = new Address("서울특별시 영등포구 의사당대로 127", "B15호 (여의도동, 롯데캐슬엠파이어)");
        HairShop hairShop = new HairShop("헤어샵1", address, owner);
        ownerRepository.save(owner);
        hairShopRepository.save(hairShop);
    }
}
