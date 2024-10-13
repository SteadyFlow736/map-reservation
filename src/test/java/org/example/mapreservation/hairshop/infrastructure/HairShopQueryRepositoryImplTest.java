package org.example.mapreservation.hairshop.infrastructure;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.infrastructure.CustomerJpaRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.domain.HairShopResponse;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class HairShopQueryRepositoryImplTest {

    @Autowired
    HairShopQueryRepositoryImpl hairShopQueryRepositoryImpl;
    @Autowired
    HairShopReservationRepository hairShopReservationRepository;
    @Autowired
    CustomerJpaRepository customerRepository;
    @Autowired
    HairShopJpaRepository hairShopRepository;
    @Autowired
    OwnerRepository ownerRepository;

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
    void hairShopSearch() {
        // given - 데이터 준비
        Owner owner = new Owner("사장1");
        ownerRepository.save(owner);
        Address address = new Address("도로명 주소", "101호");
        hairShopRepository.saveAll(List.of(
                new HairShop("헤어샵1", address, owner),
                new HairShop("헤어샵2", address, owner),
                new HairShop("헤어샵3", address, owner),
                new HairShop("헤어샵4", address, owner),
                new HairShop("헤어샵5", address, owner),
                new HairShop("블루클럽", address, owner)
        ));

        // given - 검색 조건 설정
        HairShopSearchCondition condition = new HairShopSearchCondition("헤어");
        Pageable pageable = PageRequest.of(1, 3,
                Sort.by(Sort.Direction.DESC, "name")
        );

        // when
        Page<HairShopResponse> result = hairShopQueryRepositoryImpl.search(condition, pageable).map(HairShopResponse::from);

        // then
        List<HairShopResponse> content = result.getContent();
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(0).shopName()).isEqualTo("헤어샵2");
        assertThat(content.get(1).shopName()).isEqualTo("헤어샵1");
    }
}
