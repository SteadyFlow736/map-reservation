package org.example.mapreservation.hairshop.domain;

import org.example.mapreservation.common.Address;

import java.util.List;

public record HairShopCreate(
        String name,
        Address address,
        Long ownerId,
        List<String> imageUrls
) {
    public HairShopCreate(String name, String roadAddress, String detailAddress, Long ownerId) {
        this(name, new Address(roadAddress, detailAddress), ownerId, List.of());
    }

    public HairShopCreate(String name, String roadAddress, String detailAddress, Long ownerId, List<String> imageUrls) {
        this(name, new Address(roadAddress, detailAddress), ownerId, imageUrls);
    }

}
