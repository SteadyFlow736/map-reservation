package org.example.mapreservation.hairshop.dto;

import org.example.mapreservation.common.Address;

public record CreateHairShopRequest(String name, Address address, Long ownerId) {
}
