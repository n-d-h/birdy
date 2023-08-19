package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByPaymentMethodAndStatus(PaymentMethod paymentMethod, Boolean status);

    List<Order> findByCodeAndStatus(String code, Boolean status);

    Page<Order> findByShipmentOrderInAndStatus(List<Shipment> shipmentList, Boolean status, Pageable pageable);

    Page<Order> findByShipmentOrderInAndStatusAndState(List<Shipment> shipmentList, Boolean status, OrderState state, Pageable pageable);

    Optional<Order> findByIdAndStatus(Integer orderId, Boolean b);

//

    List<Order> findByShipmentOrderInAndPaymentMethodInAndStatus(List<Shipment> shipmentList, List<PaymentMethod> paymentMethodList, Boolean status);

    Page<Order> findByShipmentOrderInAndPaymentMethodInAndStatus(List<Shipment> shipmentList, List<PaymentMethod> paymentMethodList, Boolean status, Pageable pageable);

    Page<Order> findByShipmentOrderInAndPaymentMethodInAndStateInAndStatus(List<Shipment> shipmentList, List<PaymentMethod> paymentMethodList, List<OrderState> state, Boolean status, Pageable pageable);

    Page<Order> findByShipmentOrderInAndPaymentMethodInAndPaymentStatusInAndStatus(List<Shipment> shipmentList, List<PaymentMethod> paymentMethodList, List<PaymentStatus> paymentStatus, Boolean status, Pageable pageable);

    Page<Order> findByShipmentOrderInAndPaymentMethodInAndPaymentStatusInAndStateInAndStatus(List<Shipment> shipmentList, List<PaymentMethod> paymentMethodList, List<PaymentStatus> paymentStatus, List<OrderState> state, Boolean status, Pageable pageable);

    @Query("SELECT DISTINCT YEAR(o.createDate) FROM Order o " +
            "WHERE o.status = TRUE " +
            "AND o.shipmentOrder.shopShipment.id = :shopId " +
            "AND o.state IN :states " +
            "ORDER BY YEAR(o.createDate) DESC")
    List<Integer> getAllYearsForOrdersChart(Integer shopId, List<OrderState> states);

    @Query("SELECT MIN(DATE(o.createDate)) as firstDate, MAX(DATE(o.createDate)) as lastDate " +
            "FROM Order o " +
            "WHERE o.status = TRUE " +
            "AND o.shipmentOrder.shopShipment.id = :shopId ")
    Map<String, Date> getMinMaxDatesForOrdersChart(Integer shopId);

    @Query("SELECT MIN(DATE(o.createDate)) as firstDate, MAX(DATE(o.createDate)) as lastDate " +
            "FROM Order o " +
            "WHERE o.status = TRUE ")
    Map<String, Date> getMinMaxDatesForAdminChart();

    Long countByShipmentOrderInAndStateAndCreateDateBetweenAndStatus(List<Shipment> shipmentList, OrderState state, Date start, Date end, Boolean status);

    @Query("SELECT DISTINCT YEAR(o.createDate) FROM Order o " +
            "WHERE o.status = TRUE " +
            "ORDER BY YEAR(o.createDate) DESC")
    List<Integer> getAllYearsForAdminChart();

    Long countByCreateDateBetweenAndStatus(Date start, Date end, Boolean status);

    Long countByCreateDateBetweenAndStateAndStatus(Date start, Date end, OrderState state, Boolean status);

}
