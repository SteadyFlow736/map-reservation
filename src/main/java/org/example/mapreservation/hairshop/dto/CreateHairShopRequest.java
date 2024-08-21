package org.example.mapreservation.hairshop.dto;

import org.example.mapreservation.common.Address;

import java.util.List;

public record CreateHairShopRequest(String name, Address address, Long ownerId, List<String> imageUrls) {

    public CreateHairShopRequest(String name, Address address, Long ownerId) {
        this(name, address, ownerId, List.of());
    }

    public CreateHairShopRequest(String name, String roadAddress, String detailAddress, Long ownerId) {
        this(name, new Address(roadAddress, detailAddress), ownerId, List.of());
    }

    public CreateHairShopRequest(String name, String roadAddress, String detailAddress, Long ownerId, List<String> imageUrls) {
        this(name, new Address(roadAddress, detailAddress), ownerId, imageUrls);
    }

}
