package org.example.mapreservation.hairshop.presentation;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.hairshop.application.service.HairShopService;
import org.example.mapreservation.hairshop.domain.HairShopDetailResponse;
import org.example.mapreservation.hairshop.domain.HairShopResponse;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HairShopController {

    private final HairShopService hairShopService;

    @GetMapping("/api/hairshop")
    public ResponseEntity<Page<HairShopResponse>> searchHairShop(
            HairShopSearchCondition searchCondition,
            Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(hairShopService.searchHairShop(searchCondition, pageable));
    }

    @GetMapping("/api/hairshops/{hairShopId}")
    public ResponseEntity<HairShopDetailResponse> getHairShopDetail(@PathVariable Long hairShopId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(hairShopService.getHairShopDetail(hairShopId));
    }

}
