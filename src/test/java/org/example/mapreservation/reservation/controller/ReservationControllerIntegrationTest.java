package org.example.mapreservation.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.customer.repository.CustomerRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.repository.HairShopRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.example.mapreservation.reservation.dto.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.repository.HairShopReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class ReservationControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    HairShopRepository hairShopRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    HairShopReservationRepository hairShopReservationRepository;

    private Long hairShopId;

    @BeforeEach
    void setup() {
        // 소유자
        Owner owner = new Owner("이준");
        ownerRepository.save(owner);
        // 헤어샵
        HairShop hairShop = new HairShop("이준 헤어", new Address("성남대로123", "301호"), owner);
        hairShopId = hairShopRepository.save(hairShop).getId();
        // 고객
        Customer customer = new Customer("abc@gmail.com", "12345678");
        customerRepository.save(customer);
    }

    @AfterEach
    void clean() {
        hairShopReservationRepository.deleteAll();
        hairShopRepository.deleteAll();
        ownerRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @DisplayName("고객은 헤어샵에 원하는 시간으로 예약할 수 있다.")
    @WithMockUser(username = "abc@gmail.com")
    @Test
    void givenReservationTime_thenReservationSuccess() throws Exception {
        // given - 유효한 예약 시간
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1)
                .withMinute(30).withSecond(0).withNano(0);
        HairShopReservationCreateRequest request = new HairShopReservationCreateRequest(reservationTime);

        // when, then - 예약 성공
        mockMvc.perform(post("/api/hairshops/{shopId}/reservations", hairShopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
}
