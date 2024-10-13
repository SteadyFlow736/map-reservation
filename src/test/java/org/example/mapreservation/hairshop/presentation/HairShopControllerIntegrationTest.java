package org.example.mapreservation.hairshop.presentation;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.infrastructure.CustomerJpaRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.infrastructure.HairShopJpaRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest
class HairShopControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
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
    void searchHairShop() throws Exception {
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
    void getHairShopDetail() throws Exception {
        // given - 데이터 준비
        Owner owner = new Owner("사장1");
        ownerRepository.save(owner);
        Address address = new Address("도로명 주소", "101호");
        HairShop hairShop = hairShopRepository.save(new HairShop("헤어샵1", address, owner));

        // when, then
        mockMvc.perform(get("/api/hairshops/{hairShopId}", hairShop.getId())
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(jsonPath("$.shopId").value(hairShop.getId()))
                .andExpect(jsonPath("$.shopName").value(hairShop.getName()));
    }
}
