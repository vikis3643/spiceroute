package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.aditya.restaurant_backend.dto.SuperAdminDashboardSummary;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;
import com.aditya.restaurant_backend.repository.OrderRepository;
import com.aditya.restaurant_backend.repository.RestaurantAdminRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class SuperAdminDashboardService {

    private final RestaurantRepository
            restaurantRepository;

    private final RestaurantAdminRepository
            restaurantAdminRepository;

    private final CustomerAccountRepository
            customerAccountRepository;

    private final OrderRepository
            orderRepository;

    public SuperAdminDashboardService(
            RestaurantRepository restaurantRepository,
            RestaurantAdminRepository restaurantAdminRepository,
            CustomerAccountRepository customerAccountRepository,
            OrderRepository orderRepository
    ) {
        this.restaurantRepository =
                restaurantRepository;

        this.restaurantAdminRepository =
                restaurantAdminRepository;

        this.customerAccountRepository =
                customerAccountRepository;

        this.orderRepository =
                orderRepository;
    }

    public SuperAdminDashboardSummary
            getSummary() {

        long totalRestaurants =
                restaurantRepository.count();

        long pendingRestaurants =
                restaurantRepository
                        .countByApprovalStatus(
                                RestaurantApprovalStatus.PENDING
                        );

        long approvedRestaurants =
                restaurantRepository
                        .countByApprovalStatus(
                                RestaurantApprovalStatus.APPROVED
                        );

        long activeRestaurants =
                restaurantRepository
                        .countByActiveTrue();

        long totalRestaurantAdmins =
                restaurantAdminRepository.count();

        long totalCustomers =
                customerAccountRepository.count();

        long totalOrders =
                orderRepository.count();

        long deliveredOrders =
                orderRepository.countByStatus(
                        OrderStatus.DELIVERED
                );

        long cancelledOrders =
                orderRepository.countByStatus(
                        OrderStatus.CANCELLED
                );

        BigDecimal deliveredRevenue =
                orderRepository
                        .sumTotalAmountByStatus(
                                OrderStatus.DELIVERED
                        );

        return new SuperAdminDashboardSummary(
                totalRestaurants,
                pendingRestaurants,
                approvedRestaurants,
                activeRestaurants,
                totalRestaurantAdmins,
                totalCustomers,
                totalOrders,
                deliveredOrders,
                cancelledOrders,
                deliveredRevenue
        );
    }
}