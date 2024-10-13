package org.example.mapreservation.hairshop.infrastructure;

import org.example.mapreservation.hairshop.domain.HairShop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HairShopJpaRepository extends JpaRepository<HairShop, Long> {
}
