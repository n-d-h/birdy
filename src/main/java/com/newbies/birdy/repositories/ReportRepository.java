package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Report;
import com.newbies.birdy.entities.ReportKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, ReportKey> {

    @Query("SELECT w FROM Report w WHERE w.id.userId = ?1 AND w.status = ?2")
    List<Report> findByUserId(Integer userId, Boolean status);

    @Query("SELECT w FROM Report w WHERE w.id.productId = ?1 AND w.status = ?2")
    List<Report> findByProductId(Integer productId, Boolean status);

    @Query("SELECT r.productReport.id, r.reason, COUNT(r.reason) " +
            "FROM Report r " +
            "WHERE r.productReport.id = :productId " +
            "AND r.productReport.status = true " +
            "AND r.status = true " +
            "GROUP BY r.productReport.id, r.reason " +
            "ORDER BY COUNT(r.reason) DESC")
    List<Object[]> countReportsByProductIdWithDistinctReasonsSorted(@Param("productId") Integer productId);

    @Query("SELECT r.productReport.id, COUNT(r.productReport.id) AS counted FROM Report r " +
            "WHERE r.productReport.status = true " +
            "AND r.productReport.isBanned = false " +
            "AND r.status = true " +
            "GROUP BY r.productReport.id " +
            "HAVING COUNT(r.productReport.id) > 2 " +
            "ORDER BY counted DESC")
    List<Object[]> countReportsByProductIdWithCounted();
}
