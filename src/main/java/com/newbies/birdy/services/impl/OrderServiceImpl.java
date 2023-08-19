package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.OrderDTO;
import com.newbies.birdy.dto.OrderDetailDTO;
import com.newbies.birdy.dto.OrderManageDTO;
import com.newbies.birdy.entities.*;
import com.newbies.birdy.exceptions.entity.EntityNotFoundException;
import com.newbies.birdy.exceptions.entity.QuantityException;
import com.newbies.birdy.mapper.OrderDetailMapper;
import com.newbies.birdy.mapper.OrderMapper;
import com.newbies.birdy.repositories.*;
import com.newbies.birdy.services.GoogleDistantMatrixService;
import com.newbies.birdy.services.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ProductRepository productRepository;

    private final ShipmentRepository shipmentRepository;
    private final ShopRepository shopRepository;
    private final AccountRepository accountRepository;

    private final GoogleDistantMatrixService googleDistantMatrixService;
    private final AddressRepository addressRepository;

    @Override
    public List<OrderDTO> getAllOrdersByUserIdAndStatus(Integer userId, Boolean status) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<PaymentMethod> paymentMethodList = paymentMethodRepository.findByUserPaymentMethodAndStatus(user, status);
        List<Order> orderList = new ArrayList<>();
        for (PaymentMethod paymentMethod : paymentMethodList) {
            orderList.addAll(orderRepository.findByPaymentMethodAndStatus(paymentMethod, status));
        }
        return orderList.stream().map(OrderMapper.INSTANCE::toDTO).toList();
    }

    @Override
    public Order createParentOrder(OrderDTO orderDTO) {
        Order order = OrderMapper.INSTANCE.toEntity(orderDTO);
        order.setCreateDate(new Date());
        order.setCode(UUID.randomUUID().toString());
        order.setUpdateDate(new Date());
        order.setStatus(true);
        order.setState(Enum.valueOf(OrderState.class, "PENDING"));
        order.setPaymentStatus(Enum.valueOf(PaymentStatus.class, "PENDING"));
        order.setOrder(null);
        Order savedOrder = orderRepository.save(order);
        if (savedOrder != null) {
            return savedOrder;
        }
        return null;
    }

    @Override
    public Order createOtherOrder(OrderDTO orderDTO, Order parentOrder) {
        Order order = OrderMapper.INSTANCE.toEntity(orderDTO);
        order.setCreateDate(new Date());
        order.setStatus(true);
        order.setState(Enum.valueOf(OrderState.class, "PENDING"));
        order.setPaymentStatus(Enum.valueOf(PaymentStatus.class, "PENDING"));
        order.setOrder(parentOrder);
        Order savedOrder = orderRepository.save(order);
        if (savedOrder != null) {
            return savedOrder;
        }
        return null;
    }

    @Override
    public Map<List<OrderManageDTO>, Long> getAllOrdersByShopId(Integer shopId, String search, List<String> payment, List<String> state, Pageable pageable) {
        Map<List<OrderManageDTO>, Long> pair = new HashMap<>();

        List<User> userList = userRepository.findByFullNameContainingIgnoreCaseAndStatus(search, true);
        List<PaymentMethod> paymentMethodList = paymentMethodRepository.findByUserPaymentMethodInAndStatus(userList, true);

        Shop shop = shopRepository.findByIdAndStatus(shopId, true);
        List<Shipment> shipmentList = shipmentRepository.findByShopShipmentAndStatus(shop, true);

        Page<Order> orderList;
        if (state != null) {
            List<OrderState> orderStateList = state.stream().map(s -> Enum.valueOf(OrderState.class, s.toUpperCase().trim())).toList();
            if (payment == null) {
                orderList = orderRepository.findByShipmentOrderInAndPaymentMethodInAndStateInAndStatus(shipmentList, paymentMethodList, orderStateList, true, pageable);
            } else {
                List<PaymentStatus> paymentStatusList = payment.stream().map(s -> Enum.valueOf(PaymentStatus.class, s.toUpperCase().trim())).toList();
                orderList = orderRepository.findByShipmentOrderInAndPaymentMethodInAndPaymentStatusInAndStateInAndStatus(shipmentList, paymentMethodList, paymentStatusList, orderStateList, true, pageable);
            }
        } else {
            if (payment != null) {
                List<PaymentStatus> paymentStatusList = payment.stream().map(s -> Enum.valueOf(PaymentStatus.class, s.toUpperCase().trim())).toList();
                orderList = orderRepository.findByShipmentOrderInAndPaymentMethodInAndPaymentStatusInAndStatus(shipmentList, paymentMethodList, paymentStatusList, true, pageable);
            } else {
                orderList = orderRepository.findByShipmentOrderInAndPaymentMethodInAndStatus(shipmentList, paymentMethodList, true, pageable);
            }
        }

        List<OrderManageDTO> orderManageDTOList = new ArrayList<>();
        for (Order order : orderList.getContent()) {
            OrderManageDTO orderManageDTO = new OrderManageDTO();
            orderManageDTO.setId(order.getId());
            orderManageDTO.setOrderDate(formatDate(order.getCreateDate()));
            orderManageDTO.setCustomer(order.getPaymentMethod().getUserPaymentMethod().getFullName());

            List<Double> listPrice = order.getOrderDetailList().stream().map(OrderDetail::getPrice).toList();
            Double total = listPrice.stream().reduce(0.0, Double::sum);

            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
            orderManageDTO.setTotal(decimalFormat.format(total));

            orderManageDTO.setShipType(order.getShipmentOrder().getShipmentType().getShipmentTypeName());
            orderManageDTO.setPaymentMethod(order.getPaymentMethod().getPaymentType().getPaymentTypeName().toUpperCase());
            orderManageDTO.setPaymentStatus(String.valueOf(order.getPaymentStatus()));
            orderManageDTO.setComment(order.getComment());
            orderManageDTO.setState(String.valueOf(order.getState()));

            orderManageDTOList.add(orderManageDTO);
        }
        pair.put(orderManageDTOList, orderList.getTotalElements());
        return pair;
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }

    @Override
    public List<OrderManageDTO> getAllOrderByShop(Integer shopId, String search) {

        List<User> userList = userRepository.findByFullNameContainingIgnoreCaseAndStatus(search, true);
        List<PaymentMethod> paymentMethodList = paymentMethodRepository.findByUserPaymentMethodInAndStatus(userList, true);

        Shop shop = shopRepository.findByIdAndStatus(shopId, true);
        List<Shipment> shipmentList = shipmentRepository.findByShopShipmentAndStatus(shop, true);

        List<Order> orderList = orderRepository.findByShipmentOrderInAndPaymentMethodInAndStatus(shipmentList, paymentMethodList, true);


        List<OrderManageDTO> orderManageDTOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderManageDTO orderManageDTO = new OrderManageDTO();
            orderManageDTO.setId(order.getId());
            orderManageDTO.setCustomer(order.getPaymentMethod().getUserPaymentMethod().getFullName());

            List<Double> listPrice = order.getOrderDetailList().stream()
                    .map(detail -> detail.getPrice() * detail.getQuantity())
                    .toList();
            Double total = listPrice.stream().reduce(0.0, Double::sum);

            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
            orderManageDTO.setTotal(decimalFormat.format(total));

            orderManageDTO.setShipType(order.getShipmentOrder().getShipmentType().getShipmentTypeName());
            orderManageDTO.setPaymentMethod(String.valueOf(order.getPaymentMethod().getPaymentType().getPaymentTypeName()));
            orderManageDTO.setPaymentStatus(String.valueOf(order.getPaymentStatus()));
            orderManageDTO.setState(String.valueOf(order.getState()));

            orderManageDTOList.add(orderManageDTO);
        }
        return orderManageDTOList;
    }

    @Override
    public Map<List<OrderManageDTO>, Integer> getAllOrdersByShopIdAndState(Integer shopId, String state, Pageable pageable) {
        Map<List<OrderManageDTO>, Integer> pair = new HashMap<>();
        OrderState orderState = Enum.valueOf(OrderState.class, state.toUpperCase());

        Shop shop = shopRepository.findByIdAndStatus(shopId, true);
        List<Shipment> shipmentList = shipmentRepository.findByShopShipmentAndStatus(shop, true);
        Page<Order> orderList = orderRepository.findByShipmentOrderInAndStatusAndState(shipmentList, true, orderState, pageable);

        List<OrderManageDTO> orderManageDTOList = new ArrayList<>();
        for (Order order : orderList.getContent()) {
            OrderManageDTO orderManageDTO = new OrderManageDTO();
            orderManageDTO.setId(order.getId());
            orderManageDTO.setCustomer(order.getPaymentMethod().getUserPaymentMethod().getFullName());

            List<Double> listPrice = order.getOrderDetailList().stream().map(OrderDetail::getPrice).toList();
            Double total = listPrice.stream().reduce(0.0, Double::sum);

            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
            orderManageDTO.setTotal(decimalFormat.format(total));

            orderManageDTO.setShipType(order.getShipmentOrder().getShipmentType().getShipmentTypeName());
            orderManageDTO.setPaymentMethod(String.valueOf(order.getPaymentMethod().getPaymentType().getPaymentTypeName()));
            orderManageDTO.setPaymentStatus(String.valueOf(order.getPaymentStatus()));
            orderManageDTO.setState(String.valueOf(order.getState()));

            orderManageDTOList.add(orderManageDTO);
        }
        pair.put(orderManageDTOList, orderList.getTotalPages());
        return pair;
    }

    @Override
    public Boolean editOrderState(Integer orderId, String state, String comment) {
        Order order = orderRepository.findByIdAndStatus(orderId, true).orElseThrow(() -> new EntityNotFoundException("Order ID not found"));
        OrderState orderState = Enum.valueOf(OrderState.class, state.toUpperCase().trim());
        order.setState(orderState);
        order.setUpdateDate(new Date());
        order.setComment(comment.isEmpty() ? null : comment);
        return orderRepository.save(order).getState().toString().equals(state.toUpperCase().trim());
    }

    @Override
    public Boolean payOrder(Integer orderId, Integer userId, Double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        amount = Double.parseDouble(decimalFormat.format(amount));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Account account = user.getAccountUser();
        if(account.getBalance() < amount) {
            return false;
        }
        account.setBalance(account.getBalance() - amount);
        Order order = orderRepository.findByIdAndStatus(orderId, true)
                .orElseThrow(() -> new EntityNotFoundException("Order ID not found"));
        if (accountRepository.save(account).getId() != null) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }
        return orderRepository.save(order).getPaymentStatus().equals(PaymentStatus.PAID);
    }

    @Override
    public Boolean payOrderByBalance(String code, Integer userId, Double amount) {
        amount = amount / 23000;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        amount = Double.parseDouble(decimalFormat.format(amount));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Account account = user.getAccountUser();
        account.setBalance(account.getBalance() - amount);
        List<Order> orderList = orderRepository.findByCodeAndStatus(code, true);
        if (accountRepository.save(account).getId() != null) {
            for (Order order : orderList) {
                order.setPaymentStatus(PaymentStatus.PAID);
            }
        }
        return orderRepository.saveAll(orderList).size() > 0;
    }

    @Override
    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findByIdAndStatus(orderId, true)
                .orElseThrow(() -> new EntityNotFoundException("Order ID not found"));
        if(order.getPaymentStatus().equals(PaymentStatus.PAID)){
            Account accountUser = order.getPaymentMethod().getUserPaymentMethod().getAccountUser();
            accountUser.setBalance(accountUser.getBalance() + order.getTotal());
            accountRepository.save(accountUser);
        }
    }

    @Override
    public void deliveryOrder(Integer orderId) {

    }

    @Override
    public void doneOrder(Integer orderId) {
        Order order = orderRepository.findByIdAndStatus(orderId, true)
                .orElseThrow(() -> new EntityNotFoundException("Order ID not found"));
        if(order.getPaymentStatus().equals(PaymentStatus.PAID)){
            Double actualTotal = order.getTotal() * 0.98;
            Account accountShop = order.getShipmentOrder().getShopShipment().getAccountShop();
            accountShop.setBalance(accountShop.getBalance() + actualTotal);
            accountRepository.save(accountShop);
        }
    }

    @Override
    public void updateOrder(String code) {
        List<Order> orderList = orderRepository.findByCodeAndStatus(code, true);
        orderList.forEach(o -> o.setPaymentStatus(Enum.valueOf(PaymentStatus.class, "PAID")));
        orderRepository.saveAll(orderList);
    }

    @Override
    public void updateOrderIndividual(String code) {

        Integer orderIdTmp = Integer.parseInt(code.substring(36));

        Order order = orderRepository.findByIdAndStatus(orderIdTmp, true).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setPaymentStatus(Enum.valueOf(PaymentStatus.class, "PAID"));
        order.setCode(code.substring(0, code.length() - 2));
        orderRepository.save(order);
    }

    @Override
    public String createOrder(List<OrderDTO> orderDTOList, List<OrderDetailDTO> orderDetailDTOList, Integer addressId) throws Exception {
        List<Order> orderList = new ArrayList<>(orderDTOList.stream().map(o -> {
            Order order = OrderMapper.INSTANCE.toEntity(o);
            order.setCreateDate(new Date());
            order.setCode(UUID.randomUUID().toString());
            order.setStatus(true);
            order.setState(Enum.valueOf(OrderState.class, "PENDING"));
            order.setPaymentStatus(Enum.valueOf(PaymentStatus.class, "PENDING"));
            order.setOrder(null);
            return order;
        }).toList());

        Order parentOrder = orderRepository.save(orderList.get(0));
        System.out.println(parentOrder);
        if (parentOrder.getId() != null) {
            saveDetailforOrder(parentOrder, orderDetailDTOList, addressId);
            orderList.remove(0);
            for (Order order : orderList) {
                order.setOrder(parentOrder);
                order.setCode(parentOrder.getCode());
                Order orderSaved = orderRepository.save(order);
                if (orderSaved != null) {
                    saveDetailforOrder(orderSaved, orderDetailDTOList, addressId);
                }
            }
            return parentOrder.getCode();
        }
        return null;
    }


    public void updateTotal(Order order, Double totalPriceDetail, Integer addressId) throws Exception {
        Double total = 0.0;
        Shipment shipment = shipmentRepository.findById(order.getShipmentOrder().getId())
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found"));
        String userAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"))
                .getAddress();
        Long distant = googleDistantMatrixService.getData(shipment.getShopShipment().getAddress(), userAddress);
        Double shipmentPrice =  shipment.getPricePerKm() * distant / 1000;
        total = shipmentPrice + totalPriceDetail;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        Double roundedNumber = Double.parseDouble(decimalFormat.format(total));
        order.setTotal(roundedNumber);
        orderRepository.save(order);
    }

    @Override
    public void saveDetailforOrder(Order order, List<OrderDetailDTO> orderDetailDTOList, Integer addressId) throws Exception {
        Integer shopId = shipmentRepository.findById(order.getShipmentOrder().getId()).orElseThrow(() -> new EntityNotFoundException("Shipment not found")).getShopShipment().getId();
        Double total = 0.0;
        for (OrderDetailDTO orderDetailDTO : orderDetailDTOList) {
            Product product = productRepository.findById(orderDetailDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found!"));
            if (product.getShopProduct().getId().equals(shopId)) {
                int quantityLeft = product.getQuantity() - orderDetailDTO.getQuantity();
                if (quantityLeft < 0) {
                    throw new QuantityException("Product quantity not enough!");
                }
                OrderDetail orderDetail = OrderDetailMapper.INSTANCE.toEntity(orderDetailDTO);
                orderDetail.setStatus(true);
                orderDetail.setRating(0);
                orderDetail.setOrder(order);
                orderDetailRepository.save(orderDetail);
                product.setQuantity(product.getQuantity() - orderDetail.getQuantity());
                productRepository.save(product);
                total += orderDetail.getPrice() * orderDetail.getQuantity();
            }
        }
        updateTotal(order, total, addressId);


    }

}
