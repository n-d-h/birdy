package com.newbies.birdy.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_shipment")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "price_per_km", nullable = false)
    private Double pricePerKm;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    @JsonManagedReference
    private Shop shopShipment;

    @ManyToOne
    @JoinColumn(name = "shipment_type_id")
    @JsonManagedReference
    private ShipmentType shipmentType;

    @OneToMany(mappedBy = "shipmentOrder", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Order> orderList;

}
