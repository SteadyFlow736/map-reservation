package org.example.mapreservation.hairshop.controller;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.repository.CustomerRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest
class HairShopControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    HairShopReservationRepository hairShopReservationRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    HairShopRepository hairShopRepository;
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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hairshop")
                        .queryParam("searchTerm", "헤어")
                        .queryParam("page", "1")
                        .queryParam("size", "2")
                        .queryParam("sort", "name,desc")
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.totalElements").value(5));
    }
}
