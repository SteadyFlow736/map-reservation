package org.example.mapreservation.hairshop.dto;

import org.example.mapreservation.hairshop.domain.HairShop;

import java.util.List;

public record HairShopDetail(
        Long shopId, String shopName, String latitude, String longitude, List<String> imageUrls) {
    public static HairShopDetail from(HairShop hairShop) {
        return new HairShopDetail(hairShop.getId(), hairShop.getName(), hairShop.getLatitude(), hairShop.getLongitude(), hairShop.getImageUrls());
    }
}
