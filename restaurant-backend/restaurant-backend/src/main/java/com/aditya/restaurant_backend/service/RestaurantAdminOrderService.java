package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.repository.OrderRepository;

@Service
public class RestaurantAdminOrderService {

    private final OrderRepository
            orderRepository;

    public RestaurantAdminOrderService(
            OrderRepository orderRepository
    ) {
        this.orderRepository =
                orderRepository;
    }

    public List<CustomerOrder> getOrders(
            Long restaurantId
    ) {
        return orderRepository
                .findByRestaurant_IdOrderByCreatedAtDesc(
                        restaurantId
                );
    }

    public CustomerOrder getOrder(
            Long restaurantId,
            Long orderId
    ) {
        return getRestaurantOrder(
                restaurantId,
                orderId
        );
    }

    @Transactional
    public CustomerOrder updateOrderStatus(
            Long restaurantId,
            Long orderId,
            OrderStatus newStatus
    ) {

        if (newStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order status is required"
            );
        }

        CustomerOrder order =
                getRestaurantOrder(
                        restaurantId,
                        orderId
                );

        order.setStatus(
                newStatus
        );

        return orderRepository.save(
                order
        );
    }

    public long getOrderCount(
            Long restaurantId
    ) {
        return orderRepository
                .countByRestaurant_Id(
                        restaurantId
                );
    }

    private CustomerOrder getRestaurantOrder(
            Long restaurantId,
            Long orderId
    ) {

        return orderRepository
                .findByIdAndRestaurant_Id(
                        orderId,
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found for this restaurant"
                        )
                );
    }
}