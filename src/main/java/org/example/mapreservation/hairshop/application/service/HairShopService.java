package org.example.mapreservation.hairshop.application.service;

import org.example.mapreservation.hairshop.domain.HairShopCreate;
import org.example.mapreservation.hairshop.domain.HairShopDetailResponse;
import org.example.mapreservation.hairshop.domain.HairShopResponse;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HairShopService {
    Long createHairShop(HairShopCreate request);

    Page<HairShopResponse> searchHairShop(HairShopSearchCondition searchCondition, Pageable pageable);

    HairShopDetailResponse getHairShopDetail(Long hairShopId);
}
