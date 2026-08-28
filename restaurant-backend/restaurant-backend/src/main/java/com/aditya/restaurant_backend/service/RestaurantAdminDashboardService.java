package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.repository.OrderRepository;

@Service
public class RestaurantAdminDashboardService {

    private final OrderRepository
            orderRepository;

    public RestaurantAdminDashboardService(
            OrderRepository orderRepository
    ) {
        this.orderRepository =
                orderRepository;
    }

    public Map<String, Object> getDashboardSummary(
            Long restaurantId
    ) {

        long totalOrders =
                orderRepository
                        .countByRestaurant_Id(
                                restaurantId
                        );

        long placedOrders =
                orderRepository
                        .countByRestaurant_IdAndStatus(
                                restaurantId,
                                OrderStatus.PLACED
                        );

        long preparingOrders =
                orderRepository
                        .countByRestaurant_IdAndStatus(
                                restaurantId,
                                OrderStatus.PREPARING
                        );

        long readyOrders =
                orderRepository
                        .countByRestaurant_IdAndStatus(
                                restaurantId,
                                OrderStatus.READY
                        );

        long deliveredOrders =
                orderRepository
                        .countByRestaurant_IdAndStatus(
                                restaurantId,
                                OrderStatus.DELIVERED
                        );

        long cancelledOrders =
                orderRepository
                        .countByRestaurant_IdAndStatus(
                                restaurantId,
                                OrderStatus.CANCELLED
                        );

        BigDecimal deliveredRevenue =
                orderRepository
                        .sumTotalAmountByRestaurantIdAndStatus(
                                restaurantId,
                                OrderStatus.DELIVERED
                        );

        BigDecimal deliveredSubtotal =
                orderRepository
                        .sumSubtotalByRestaurantIdAndStatus(
                                restaurantId,
                                OrderStatus.DELIVERED
                        );

        Map<String, Object> summary =
                new LinkedHashMap<>();

        summary.put(
                "totalOrders",
                totalOrders
        );

        summary.put(
                "placedOrders",
                placedOrders
        );

        summary.put(
                "preparingOrders",
                preparingOrders
        );

        summary.put(
                "readyOrders",
                readyOrders
        );

        summary.put(
                "deliveredOrders",
                deliveredOrders
        );

        summary.put(
                "cancelledOrders",
                cancelledOrders
        );

        summary.put(
                "deliveredRevenue",
                deliveredRevenue
        );

        summary.put(
                "deliveredSubtotal",
                deliveredSubtotal
        );

        return summary;
    }
}