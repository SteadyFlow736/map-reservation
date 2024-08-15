package org.example.mapreservation.hairshop.dto;

import org.example.mapreservation.hairshop.domain.HairShop;

public record HairShopDto(Long shopId, String shopName, String latitude, String longitude) {
    public static HairShopDto from(HairShop hairShop) {
        return new HairShopDto(hairShop.getId(), hairShop.getName(), hairShop.getLatitude(), hairShop.getLongitude());
    }
}
