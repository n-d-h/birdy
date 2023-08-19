package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Order;
import com.newbies.birdy.entities.OrderDetail;
import com.newbies.birdy.entities.OrderState;
import com.newbies.birdy.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    List<OrderDetail> findByOrderAndStatus(Order order, Boolean status);

    //    @Query("SELECT od FROM OrderDetail od WHERE od.productOrderDetail = ?1 AND od.status = ?2 AND od.rating > 0")
    Page<OrderDetail> findByProductOrderDetailAndStatusAndRatingGreaterThan(Product p, Boolean status, Integer rating, Pageable pageable);

    @Query("SELECT SUM(o.price * o.quantity) " +
            "FROM OrderDetail o " +
            "WHERE o.order.state = :state " +
            "AND YEAR(o.order.createDate) = :year " +
            "AND MONTH(o.order.createDate) = :month " +
            "AND o.status = TRUE " +
            "AND o.order.status = TRUE")
    BigDecimal calculateTotalSumByMonthAndYear(@Param("state") OrderState state, @Param("month") Integer month, @Param("year") Integer year);

    default Double calculateFormattedTotalSumByMonthAndYear(Integer month, Integer year) {
        BigDecimal totalSum = calculateTotalSumByMonthAndYear(OrderState.DONE, month, year);
        if (totalSum == null) {
            return 0.0;
        }
        return totalSum.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Query("SELECT SUM(o.price * o.quantity) " +
            "FROM OrderDetail o " +
            "WHERE o.order.state = :state " +
            "AND o.order.createDate BETWEEN :startDate AND :endDate " +
            "AND o.order.shipmentOrder.shopShipment.id = :shopId " +
            "AND o.status = TRUE " +
            "AND o.order.status = TRUE")
    BigDecimal calculateTotalSumByDateRange(@Param("shopId") Integer shopId, @Param("state") OrderState state, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    default Double calculateTotalIncomeBetweenDates(Integer shopId, Date start, Date end) {
        BigDecimal totalSum = calculateTotalSumByDateRange(shopId, OrderState.DONE, start, end);
        if (totalSum == null) {
            return 0.0;
        }
        return totalSum.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Query("SELECT SUM(o.price * o.quantity) " +
            "FROM OrderDetail o " +
            "WHERE o.order.state = :state " +
            "AND o.order.createDate BETWEEN :startDate AND :endDate " +
            "AND o.status = TRUE " +
            "AND o.order.status = TRUE")
    BigDecimal calculateTotalIncomeByDateRange(@Param("state") OrderState state, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    default Double calculateTotalAdminIncomeBetweenDates(Date start, Date end) {
        BigDecimal totalSum = calculateTotalIncomeByDateRange(OrderState.DONE, start, end);
        if (totalSum == null) {
            return 0.0;
        }
        return totalSum.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    Long countByProductOrderDetailAndStatusAndRatingGreaterThan(Product p, Boolean status, Integer rating);

    @Query("select count (od) from OrderDetail od " +
            "where od.productOrderDetail.id = :id " +
            "and od.status = true " +
            "and od.order.status = true " +
            "and od.order.state = :state ")
    Long countByProductOrderDetailAndStatusAndOrderState(@Param("id") Integer id, @Param("state") OrderState state);

    @Query("select count (od) from OrderDetail od " +
            "where od.productOrderDetail.id = :id " +
            "and od.status = true " +
            "and od.order.status = true ")
    Long countByProductOrderDetailAndStatus(@Param("id") Integer id);

    @Query("select distinct o.order.id from OrderDetail o " +
            "where o.productOrderDetail.id = :id " +
            "and o.status = true " +
            "and o.order.status = true " +
            "and o.order.state = :state ")
    List<Integer> getListOrderIdProductChecked(@Param("id") Integer id, @Param("state") OrderState state);
}
