package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Shipment;
import com.newbies.birdy.entities.ShipmentType;
import com.newbies.birdy.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository  extends JpaRepository<Shipment, Integer> {

    Optional<Shipment> findByIdAndStatus(Integer id, Boolean status);

    List<Shipment> findByShopShipmentAndStatus(Shop shops, Boolean status);

    Shipment findByShopShipmentAndShipmentTypeAndStatus(Shop shop, ShipmentType shipmentType, Boolean status);
}
