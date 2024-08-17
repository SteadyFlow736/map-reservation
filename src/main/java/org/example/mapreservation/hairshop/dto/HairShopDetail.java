package org.example.mapreservation.hairshop.dto;

import org.example.mapreservation.hairshop.domain.HairShop;

public record HairShopDetail(
        Long shopId, String shopName, String latitude, String longitude) {
    public static HairShopDetail from(HairShop hairShop) {
        return new HairShopDetail(hairShop.getId(), hairShop.getName(), hairShop.getLatitude(), hairShop.getLongitude());
    }
}
