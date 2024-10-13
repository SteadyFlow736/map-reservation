package org.example.mapreservation.hairshop.application;

import org.example.mapreservation.customer.infrastructure.CustomerJpaRepository;
import org.example.mapreservation.hairshop.domain.HairShopCreate;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.infrastructure.HairShopJpaRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@Disabled("GeocodeService는 외부 API를 사용 중. mocking 으로 분리시키기 전까진 비활성화 처리(임시)")
@SpringBootTest
class HairShopServiceImplTest {

    @Autowired
    HairShopServiceImpl hairShopServiceImpl;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    HairShopJpaRepository hairShopRepository;
    @Autowired
    HairShopReservationRepository hairShopReservationRepository;
    @Autowired
    CustomerJpaRepository customerRepository;

    @BeforeEach
    void beforeEach() {
        cleanUp();
    }

    private void cleanUp() {
        hairShopReservationRepository.deleteAll();
        customerRepository.deleteAll();
        hairShopRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    @Test
    void saveHairShop() {
        // given
        Owner owner = new Owner("주인1");
        ownerRepository.save(owner);
        HairShopCreate request = new HairShopCreate("헤어샵1", "도로 주소1", "101호", owner.getId());
        Long hairShopId = hairShopServiceImpl.createHairShop(request);

        // when
        HairShop foundHairShop = hairShopRepository.findById(hairShopId)
                .orElseThrow();

        // then
        assertThat(foundHairShop.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(foundHairShop.getName()).isEqualTo(request.name());
        assertThat(foundHairShop.getAddress()).isEqualTo(request.address());
    }
}
