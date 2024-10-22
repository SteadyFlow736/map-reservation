package org.example.mapreservation.hairshop.application.repository;

import org.example.mapreservation.hairshop.domain.HairShop;

import java.util.Optional;

public interface HairShopRepository {
    HairShop save(HairShop hairShop);

    Optional<HairShop> findById(Long hairShopId);
}
