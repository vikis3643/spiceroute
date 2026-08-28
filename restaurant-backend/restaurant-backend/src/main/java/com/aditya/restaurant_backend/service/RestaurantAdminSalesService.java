package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.repository.OrderRepository;

@Service
public class RestaurantAdminSalesService {

    private final OrderRepository
            orderRepository;

    public RestaurantAdminSalesService(
            OrderRepository orderRepository
    ) {
        this.orderRepository =
                orderRepository;
    }

    public Map<String, Object> getTodaySales(
            Long restaurantId
    ) {

        LocalDate today =
                LocalDate.now();

        LocalDateTime start =
                today.atStartOfDay();

        LocalDateTime end =
                today
                        .plusDays(1)
                        .atStartOfDay();

        return buildSalesSummary(
                restaurantId,
                start,
                end
        );
    }

    public Map<String, Object> getSalesByDateRange(
            Long restaurantId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (
                startDate == null ||
                endDate == null
        ) {
            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime end =
                endDate
                        .plusDays(1)
                        .atStartOfDay();

        return buildSalesSummary(
                restaurantId,
                start,
                end
        );
    }

    public List<CustomerOrder> getOrdersByDateRange(
            Long restaurantId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (
                startDate == null ||
                endDate == null
        ) {
            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime end =
                endDate
                        .plusDays(1)
                        .atStartOfDay();

        return orderRepository
                .findByRestaurant_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        restaurantId,
                        start,
                        end
                );
    }

    private Map<String, Object> buildSalesSummary(
            Long restaurantId,
            LocalDateTime start,
            LocalDateTime end
    ) {

        long totalOrders =
                orderRepository
                        .countByRestaurant_IdAndCreatedAtBetween(
                                restaurantId,
                                start,
                                end
                        );

        long deliveredOrders =
                orderRepository
                        .countByRestaurant_IdAndStatusAndCreatedAtBetween(
                                restaurantId,
                                OrderStatus.DELIVERED,
                                start,
                                end
                        );

        long cancelledOrders =
                orderRepository
                        .countByRestaurant_IdAndStatusAndCreatedAtBetween(
                                restaurantId,
                                OrderStatus.CANCELLED,
                                start,
                                end
                        );

        BigDecimal deliveredRevenue =
                orderRepository
                        .sumTotalAmountByRestaurantStatusAndDateRange(
                                restaurantId,
                                OrderStatus.DELIVERED,
                                start,
                                end
                        );

        BigDecimal deliveredSubtotal =
                orderRepository
                        .sumSubtotalByRestaurantStatusAndDateRange(
                                restaurantId,
                                OrderStatus.DELIVERED,
                                start,
                                end
                        );

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "start",
                start
        );

        result.put(
                "end",
                end
        );

        result.put(
                "totalOrders",
                totalOrders
        );

        result.put(
                "deliveredOrders",
                deliveredOrders
        );

        result.put(
                "cancelledOrders",
                cancelledOrders
        );

        result.put(
                "deliveredRevenue",
                deliveredRevenue
        );

        result.put(
                "deliveredSubtotal",
                deliveredSubtotal
        );

        return result;
    }
}