package org.example.mapreservation.hairshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.hairshop.dto.HairShopDetail;
import org.example.mapreservation.hairshop.dto.HairShopDto;
import org.example.mapreservation.hairshop.dto.HairShopSearchCondition;
import org.example.mapreservation.hairshop.service.HairShopService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HairShopController {

    private final HairShopService hairShopService;

    @GetMapping("/api/hairshop")
    public Page<HairShopDto> searchHairShop(
            HairShopSearchCondition searchCondition,
            Pageable pageable) {
        return hairShopService.searchHairShop(searchCondition, pageable);
    }

    @GetMapping("/api/hairshops/{hairShopId}")
    public HairShopDetail getHairShopDetail(@PathVariable Long hairShopId) {
        return hairShopService.getHairShopDetail(hairShopId);
    }

}
