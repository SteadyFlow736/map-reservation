package org.example.mapreservation.hairshop.dto;

import org.example.mapreservation.hairshop.domain.HairShop;

public record HairShopDto(String shopName) {
    public static HairShopDto from(HairShop hairShop) {
        return new HairShopDto(hairShop.getName());
    }
}
