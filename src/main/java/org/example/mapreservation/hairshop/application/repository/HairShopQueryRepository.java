package org.example.mapreservation.hairshop.application.repository;

import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.domain.HairShopSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HairShopQueryRepository {
    Page<HairShop> search(HairShopSearchCondition searchCondition, Pageable pageable);
}
