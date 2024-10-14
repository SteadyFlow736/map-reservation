package org.example.mapreservation.hairshop.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record HairShopDetailResponse(
        Long shopId,
        String shopName,
        String latitude,
        String longitude,
        List<String> imageUrls,
        String roadAddress,
        String detailAddress
) {
    public static HairShopDetailResponse from(HairShop hairShop) {
        return new HairShopDetailResponse(
                hairShop.getId(),
                hairShop.getName(),
                hairShop.getLatitude(),
                hairShop.getLongitude(),
                hairShop.getImageUrls(),
                hairShop.getAddress().getRoadAddress(),
                hairShop.getAddress().getDetailAddress());
    }
}
