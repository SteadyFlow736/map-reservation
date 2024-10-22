package org.example.mapreservation.hairshop.infrastructure;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.hairshop.application.repository.HairShopRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class HairShopRepositoryImpl implements HairShopRepository {

    private final HairShopJpaRepository hairShopJpaRepository;

    @Override
    public HairShop save(HairShop hairShop) {
        return hairShopJpaRepository.save(hairShop);
    }

    @Override
    public Optional<HairShop> findById(Long hairShopId) {
        return hairShopJpaRepository.findById(hairShopId);
    }
}
