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
@Table(name = "tbl_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "product_name",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", nullable = false)
    private String productName;

    @Column(name = "image_main")
    private String imageMain;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "sale_pct")
    private int salePtc;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "create_date", nullable = false)
    private Date createDate;

    @Column(name = "species",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", length = 50)
    private String species;

    @Column(name = "age", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String age;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "color",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", length = 20)
    private String color;

    @Column(name = "exp_date")
    private Date expDate;

    @Column(name = "made_in",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", length = 50)
    private String madeIn;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "size",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", length = 20)
    private String size;

    @Column(name = "material",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", length = 20)
    private String material;

    @Column(name = "description", columnDefinition = "LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String description;

    @Column(name = "brand_name",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", length = 20)
    private String brandName;

    @Column(name = "state", nullable = false)
    private Integer state;

    @Column(name = "is_warned", nullable = false)
    private Boolean isWarned;

    @Column(name = "is_disabled", nullable = false)
    private Boolean isDisabled;

    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shopProduct;

    @OneToMany(mappedBy = "productImg", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<ProductImage> productImageList;

    @OneToMany(mappedBy = "productOrderDetail", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<OrderDetail> orderDetailList;

    @OneToMany(mappedBy = "productWishlist", fetch = FetchType.LAZY)
    private List<Wishlist> wishlistList;

    @OneToMany(mappedBy = "productReport", fetch = FetchType.LAZY)
    private List<Report> reportList;

}
