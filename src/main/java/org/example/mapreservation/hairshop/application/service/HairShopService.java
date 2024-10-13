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

    /**
     * 헤어샵 상세 정보 조회
     *
     * @param hairShopId 헤어샵 아이디
     * @return {@link HairShopDetailResponse} 헤어샵 상세 정보
     * @throws org.example.mapreservation.exception.CustomException 헤어샵을 찾을 수 없는 경우
     */
    HairShopDetailResponse getHairShopDetail(Long hairShopId);
}
