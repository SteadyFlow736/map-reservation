package org.example.mapreservation.hairshop.domain;

import lombok.Builder;

@Builder
public record HairShopSearchCondition(
        String searchTerm,
        String minLongitude,
        String maxLongitude,
        String minLatitude,
        String maxLatitude
) {
}
