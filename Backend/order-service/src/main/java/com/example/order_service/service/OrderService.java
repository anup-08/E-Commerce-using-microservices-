package com.example.order_service.service;

import com.example.order_service.dto.*;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.exception.EmptyCartException;
import com.example.order_service.exception.InsufficientStockException;
import com.example.order_service.exception.OrderNotFound;
import com.example.order_service.feignClient.CartFeignClient;
import com.example.order_service.feignClient.InventoryFeignClient;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderItem;
import com.example.order_service.repo.OrderRepo;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final CartFeignClient cartFeignClient;
    private final InventoryFeignClient inventoryFeignClient;

    @Transactional
    public OrderResponse placeOrder(String userId){

        CartResponse cartResponse = cartFeignClient.getDetailsOfCart();

        if (cartResponse == null || cartResponse.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot place an order with an empty cart.");
        }

        List<InventoryRequest> inventoryRequests = cartResponse.getItems().stream()
                .map( item -> new InventoryRequest(item.getProductId() , item.getSku() , item.getQuantity()))
                .toList();

        try {
            inventoryFeignClient.reserveStock(inventoryRequests);
        } catch (FeignException e) {
            throw new InsufficientStockException("Failed to reserve stock. Items may be sold out.");

        }

        List<OrderItem> orderItems = cartResponse.getItems().stream()
                .map(cartItem ->
                        OrderItem.builder()
                            .productId(cartItem.getProductId())
                            .quantity(cartItem.getQuantity())
                            .priceAtPurchase(cartItem.getPrice())
                            .build()

                ).toList();

        Order order = Order.builder()
                .userId(userId)
                .totalAmount(cartResponse.getTotalPrice())
                .orderStatus(OrderStatus.CONFIRMED)
                .orderItems(orderItems)
                .build();
        order.getOrderItems().forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepo.save(order);

        KafkaDto event = new KafkaDto(userId);
        kafkaTemplate.send("clear-cart-topic", event);
        kafkaTemplate.send("inventory-topic" , inventoryRequests);
        return convertToResponse(savedOrder);

    }

    public OrderResponse getOrderById(Long orderId){
        Order order = orderRepo.findById(orderId).orElseThrow(() ->  new OrderNotFound("Order not found with id "+orderId));
        return convertToResponse(order);
    }

    public OrderResponse updateOrderStatus(Long orderId , String newStatus){
        OrderStatus status;
        try{
            status = OrderStatus.valueOf(newStatus);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Error: '" + newStatus + "' is not a valid order status.");
        }
        Order order = orderRepo.findById(orderId).orElseThrow(() ->  new OrderNotFound("Order not found with id "+orderId));
        order.setOrderStatus(status);
        return convertToResponse(orderRepo.save(order));
    }




    private OrderResponse convertToResponse(Order order){

        List<OrderItemResponse> orderItemResponseList = order.getOrderItems().stream()
                .map(this::convertToItemResponse).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus().getDisplayName())
                .orderAt(order.getOrderAt())
                .orderItems(orderItemResponseList)
                .build();

    }

    private OrderItemResponse convertToItemResponse(OrderItem orderItem){
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .build();
    }
}
