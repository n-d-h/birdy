package com.newbies.birdy.services.impl;

import com.newbies.birdy.exceptions.entity.EntityNotFoundException;
import com.newbies.birdy.repositories.ShipmentRepository;
import com.newbies.birdy.services.ShipmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Override
    public Double getShipmentPriceById(Integer id, Boolean status) {
        return shipmentRepository.findByIdAndStatus(id, status)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can not find shipment have id: " + id +" have been deleted!"))
                .getPricePerKm();
    }
}
