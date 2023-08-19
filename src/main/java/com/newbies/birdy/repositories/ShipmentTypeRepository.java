package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.ShipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentTypeRepository extends JpaRepository<ShipmentType, Integer> {
}
