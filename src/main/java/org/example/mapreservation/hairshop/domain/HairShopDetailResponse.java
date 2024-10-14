package org.example.mapreservation.hairshop.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record HairShopDetailResponse(
        Long shopId,
        String shopName,
        String longitude,
        String latitude,
        List<String> imageUrls,
        String roadAddress,
        String detailAddress
) {
    public static HairShopDetailResponse from(HairShop hairShop) {
        return new HairShopDetailResponse(
                hairShop.getId(),
                hairShop.getName(),
                hairShop.getLongitude(),
                hairShop.getLatitude(),
                hairShop.getImageUrls(),
                hairShop.getAddress().getRoadAddress(),
                hairShop.getAddress().getDetailAddress());
    }
}
