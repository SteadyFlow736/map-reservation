package org.example.mapreservation.hairshop.presentation;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.infrastructure.HairShopJpaRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SqlGroup({
        @Sql(value = "/sql/delete-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@AutoConfigureMockMvc
@SpringBootTest
class HairShopControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    HairShopJpaRepository hairShopJpaRepository;
    @Autowired
    OwnerRepository ownerRepository;

    @Test
    void 검색어와_페이지_정보를_전달하여_검색_결과를_받아볼_수_있다() throws Exception {
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

        // when, then
        mockMvc.perform(get("/api/hairshop")
                        .queryParam("searchTerm", "헤어")
                        .queryParam("page", "1")
                        .queryParam("size", "3")
                        .queryParam("sort", "name,desc")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].shopName").value("헤어샵2"))
                .andExpect(jsonPath("$.content[1].shopName").value("헤어샵1"));
    }

    @Test
    void 특정_헤어샵의_상세_정보를_얻을_수_있다() throws Exception {
        // given - 데이터 준비
        Owner owner = new Owner("사장1");
        ownerRepository.save(owner);
        Address address = new Address("도로주소", "상세주소");
        HairShop hairShop = hairShopJpaRepository.save(
                new HairShop("헤어샵", address, owner, "10.0", "20.0", List.of("url1", "url2", "url3")));

        // when, then
        mockMvc.perform(get("/api/hairshops/{hairShopId}", hairShop.getId())
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopId").isNotEmpty())
                .andExpect(jsonPath("$.shopName").value("헤어샵"))
                .andExpect(jsonPath("$.longitude").value("10.0"))
                .andExpect(jsonPath("$.latitude").value("20.0"))
                .andExpect(jsonPath("$.imageUrls").value(
                        Matchers.contains("url1", "url2", "url3")
                ))
                .andExpect(jsonPath("$.roadAddress").value("도로주소"))
                .andExpect(jsonPath("$.detailAddress").value("상세주소"));
    }
}
