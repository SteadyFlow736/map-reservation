package org.example.mapreservation.hairshop.repository;

import jakarta.persistence.LockModeType;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface HairShopRepository extends JpaRepository<HairShop, Long> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select hs from HairShop hs where hs.id = :hairShopId")
    Optional<HairShop> findByIdOptimisticForceIncrement(@Param("hairShopId") Long hairShopId);
}
