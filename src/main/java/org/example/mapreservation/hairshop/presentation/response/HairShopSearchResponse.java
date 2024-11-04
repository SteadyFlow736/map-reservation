package org.example.mapreservation.hairshop.presentation.response;

import lombok.Builder;
import lombok.Getter;
import org.example.mapreservation.hairshop.domain.HairShopResponse;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.springframework.data.domain.Page;

@Getter
public class HairShopSearchResponse {
    private final Page<HairShopResponse> page;
    private final HairShopSearchCondition searchCondition;

    @Builder
    public HairShopSearchResponse(Page<HairShopResponse> page, HairShopSearchCondition searchCondition) {
        this.page = page;
        this.searchCondition = searchCondition;
    }
}
