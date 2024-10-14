package org.example.mapreservation.hairshop.infrastructure;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SqlGroup({
        @Sql(value = "/sql/delete-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/customer-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@SpringBootTest
class HairShopQueryRepositoryImplIntegrationTest {

    @Autowired
    HairShopQueryRepositoryImpl hairShopQueryRepositoryImpl;
    @Autowired
    HairShopJpaRepository hairShopJpaRepository;
    @Autowired
    OwnerRepository ownerRepository;

    @Test
    void 검색어로_헤어샵을_검색할_수_있다() {
        // given - 데이터 준비
        Owner owner = new Owner("사장1");
        ownerRepository.save(owner);
        Address address = new Address("도로명 주소", "101호");
        hairShopJpaRepository.saveAll(List.of(
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
        Page<HairShop> result = hairShopQueryRepositoryImpl.search(condition, pageable);

        // then
        List<HairShop> content = result.getContent();
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(0).getName()).isEqualTo("헤어샵2");
        assertThat(content.get(1).getName()).isEqualTo("헤어샵1");
    }
}
