package org.example.mapreservation.hairshop.dto;

import org.example.mapreservation.common.Address;

public record CreateHairShopRequest(String name, Address address, Long ownerId) {
    public CreateHairShopRequest(String name, String roadAddress, String detailAddress, Long ownerId) {
        this(name, new Address(roadAddress, detailAddress), ownerId);
    }
}
