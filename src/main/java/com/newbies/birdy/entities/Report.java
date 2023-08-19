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
@Table(name = "tbl_report")
public class Report {

    @EmbeddedId
    private ReportKey id;

    @Column(name = "reason",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String reason;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "user_id")
    private User userReport;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "product_id")
    private Product productReport;
}
