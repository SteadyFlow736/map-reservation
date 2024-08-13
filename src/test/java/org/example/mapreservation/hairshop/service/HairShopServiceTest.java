package org.example.mapreservation.hairshop.service;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.dto.CreateHairShopRequest;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class HairShopServiceTest {

    @Autowired
    HairShopService hairShopService;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    HairShopRepository hairShopRepository;

    @Test
    void saveHairShop() {
        // given
        Owner owner = new Owner("주인1");
        ownerRepository.save(owner);
        CreateHairShopRequest request = new CreateHairShopRequest("헤어샵1", new Address("도로 주소1", "101호"), owner.getId());
        Long hairShopId = hairShopService.createHairShop(request);

        // when
        HairShop foundHairShop = hairShopRepository.findById(hairShopId)
                .orElseThrow();

        // then
        assertThat(foundHairShop.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(foundHairShop.getName()).isEqualTo(request.name());
        assertThat(foundHairShop.getAddress()).isEqualTo(request.address());
    }
}
