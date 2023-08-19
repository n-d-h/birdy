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
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "full_name",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", length = 50, nullable = false)
    private String fullName;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "dob", nullable = false)
    private Date dob;

    @Column(name = "gender", nullable = false)
    private Integer gender;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @OneToOne
    @JoinColumn(name = "account_id")
    @JsonManagedReference
    private Account accountUser;

    @OneToMany(mappedBy = "userAddress",fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Address> addressList;

    @OneToMany(mappedBy = "userPaymentMethod", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<PaymentMethod> paymentMethodList;

    @OneToMany(mappedBy = "userWishlist", fetch = FetchType.LAZY)
    private List<Wishlist> wishlistList;

    @OneToMany(mappedBy = "userReport", fetch = FetchType.LAZY)
    private List<Report> reportList;

}
