package org.example.mapreservation.hairshop.dto;

import org.example.mapreservation.hairshop.domain.HairShop;

import java.util.List;

public record HairShopDto(Long shopId, String shopName, String latitude, String longitude, List<String> images) {
    public static HairShopDto from(HairShop hairShop) {
        return new HairShopDto(hairShop.getId(), hairShop.getName(), hairShop.getLatitude(), hairShop.getLongitude(), hairShop.getImageUrls());
    }
}
