package org.example.mapreservation.owner.repository;

import org.example.mapreservation.owner.domain.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
