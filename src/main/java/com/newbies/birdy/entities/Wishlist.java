package com.newbies.birdy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_wishlist")
public class Wishlist {

    @EmbeddedId
    private WishlistKey id;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "user_id")
    private User userWishlist;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "product_id")
    private Product productWishlist;
}
