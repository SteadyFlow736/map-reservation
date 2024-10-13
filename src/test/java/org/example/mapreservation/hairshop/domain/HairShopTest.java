package org.example.mapreservation.hairshop.domain;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.hairshop.infrastructure.HairShopJpaRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class HairShopTest {

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    HairShopJpaRepository hairShopRepository;

    @Test
    void saveAndLoadTest() {
        // given
        Owner owner = new Owner("소유자");
        ownerRepository.save(owner);
        Address address = new Address("도로 주소", "상세 주소");
        List<String> imageUrls = List.of("url1", "url2");
        HairShop hairShop = new HairShop("헤어샵1", address, owner, "33.1234", "127.12345", imageUrls);
        hairShopRepository.save(hairShop);

        // when
        HairShop foundHairShop = hairShopRepository.findById(hairShop.getId())
                .orElseThrow();

        // then
        assertThat(foundHairShop.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(foundHairShop.getAddress().getRoadAddress()).isEqualTo(address.getRoadAddress());
        assertThat(foundHairShop.getAddress().getDetailAddress()).isEqualTo(address.getDetailAddress());
        assertThat(foundHairShop.getName()).isEqualTo(hairShop.getName());
        assertThat(foundHairShop.getLongitude()).isEqualTo(hairShop.getLongitude());
        assertThat(foundHairShop.getLatitude()).isEqualTo(hairShop.getLatitude());
        assertThat(foundHairShop.getImageUrls()).isEqualTo(imageUrls);
    }
}
