package com.newbies.birdy.services;

import com.newbies.birdy.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ShopService {

    List<ShopDTO> listByShopName(String name, Boolean status);

    ShopDTO getShopById(Integer id);

    List<ShopDTO> listAllShop(Boolean status);

    Map<List<ShopDTO>, Integer> listByNameAndStatusWithPaging(String name, Boolean status, Pageable pageable);

    String getShopAddress(Integer shopId);

    List<ShipmentDTO> listShipmentByShopId(Integer shopId, Boolean status);

    ShopDTO getShopByPhoneNumber(String phoneNumber, Boolean status);

    List<Integer> getAllYearsForProductsChart(Integer shopId);

    ProductsChartDTO getDataForProductsChart(Integer shopId, Integer year);

    List<Integer> getAllYearsForOrdersChart(Integer shopId);

    OrdersChartDTO getDataForOrdersChart(Integer shopId, Integer year);

    ShopIncomeDTO getDefaultShopIncome(Integer shopId);

    ShopIncomeDTO getShopIncome(Integer shopId, String startDate, String endDate);

    Integer updateShopProfile(Integer id, String shopName, String address, String ava);

    Boolean updateShopShipment(Integer shopID, Integer shipmentTypeId, Double pricePerKm);
}
