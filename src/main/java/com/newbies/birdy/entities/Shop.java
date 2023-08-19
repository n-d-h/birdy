package com.newbies.birdy.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_shop")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "email",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String email;

    @Column(name = "shop_name",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", nullable = false)
    private String shopName;

    @Column(name = "address",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", nullable = false)
    private String address;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "create_Date", nullable = false)
    private Date createDate;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @OneToOne
    @JoinColumn(name = "account_id")
    @JsonManagedReference
    private Account accountShop;

    @OneToMany(mappedBy = "shopProduct", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Product> productList;

    @OneToMany(mappedBy = "shopShipment", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Shipment> shipmentList;


}
