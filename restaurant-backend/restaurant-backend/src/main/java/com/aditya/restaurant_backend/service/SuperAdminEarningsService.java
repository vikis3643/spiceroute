package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aditya.restaurant_backend.dto.SuperAdminEarningsDateRangeResponse;
import com.aditya.restaurant_backend.dto.SuperAdminPlatformEarningsResponse;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantEarningsResponse;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.OrderRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class SuperAdminEarningsService {

    private final OrderRepository orderRepository;

    private final RestaurantRepository restaurantRepository;

    public SuperAdminEarningsService(
            OrderRepository orderRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.orderRepository =
                orderRepository;

        this.restaurantRepository =
                restaurantRepository;
    }

    // ==========================================
    // PLATFORM-WIDE EARNINGS
    // ==========================================

    @Transactional(readOnly = true)
    public SuperAdminPlatformEarningsResponse
            getPlatformEarnings() {

        List<Restaurant> restaurants =
                restaurantRepository.findAll();

        List<SuperAdminRestaurantEarningsResponse>
                restaurantEarnings =
                new ArrayList<>();

        long totalDeliveredOrders = 0L;

        BigDecimal totalDeliveredSubtotal =
                BigDecimal.ZERO;

        BigDecimal totalPlatformCommission =
                BigDecimal.ZERO;

        BigDecimal totalRestaurantNetEarnings =
                BigDecimal.ZERO;

        for (Restaurant restaurant : restaurants) {

            SuperAdminRestaurantEarningsResponse
                    earnings =
                    calculateRestaurantEarnings(
                            restaurant
                    );

            restaurantEarnings.add(
                    earnings
            );

            totalDeliveredOrders +=
                    earnings.deliveredOrders();

            totalDeliveredSubtotal =
                    totalDeliveredSubtotal.add(
                            earnings.deliveredSubtotal()
                    );

            totalPlatformCommission =
                    totalPlatformCommission.add(
                            earnings.platformCommission()
                    );

            totalRestaurantNetEarnings =
                    totalRestaurantNetEarnings.add(
                            earnings.restaurantNetEarnings()
                    );
        }

        return new SuperAdminPlatformEarningsResponse(

                totalDeliveredOrders,

                money(
                        totalDeliveredSubtotal
                ),

                money(
                        totalPlatformCommission
                ),

                money(
                        totalRestaurantNetEarnings
                ),

                restaurantEarnings
        );
    }

    // ==========================================
    // DATE-RANGE EARNINGS
    // ==========================================

    @Transactional(readOnly = true)
    public SuperAdminEarningsDateRangeResponse
            getEarningsByDateRange(
                    LocalDate startDate,
                    LocalDate endDate
            ) {

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

        List<Restaurant> restaurants =
                restaurantRepository.findAll();

        List<SuperAdminRestaurantEarningsResponse>
                restaurantEarnings =
                new ArrayList<>();

        long totalDeliveredOrders = 0L;

        BigDecimal totalDeliveredSubtotal =
                BigDecimal.ZERO;

        BigDecimal totalPlatformCommission =
                BigDecimal.ZERO;

        BigDecimal totalRestaurantNetEarnings =
                BigDecimal.ZERO;

        for (Restaurant restaurant : restaurants) {

            SuperAdminRestaurantEarningsResponse
                    earnings =
                    calculateRestaurantEarningsForDateRange(
                            restaurant,
                            start,
                            end
                    );

            restaurantEarnings.add(
                    earnings
            );

            totalDeliveredOrders +=
                    earnings.deliveredOrders();

            totalDeliveredSubtotal =
                    totalDeliveredSubtotal.add(
                            earnings.deliveredSubtotal()
                    );

            totalPlatformCommission =
                    totalPlatformCommission.add(
                            earnings.platformCommission()
                    );

            totalRestaurantNetEarnings =
                    totalRestaurantNetEarnings.add(
                            earnings.restaurantNetEarnings()
                    );
        }

        return new SuperAdminEarningsDateRangeResponse(

                startDate,
                endDate,

                totalDeliveredOrders,

                money(
                        totalDeliveredSubtotal
                ),

                money(
                        totalPlatformCommission
                ),

                money(
                        totalRestaurantNetEarnings
                ),

                restaurantEarnings
        );
    }

    // ==========================================
    // RESTAURANT EARNINGS
    // ==========================================

    private SuperAdminRestaurantEarningsResponse
            calculateRestaurantEarnings(
                    Restaurant restaurant
            ) {

        long deliveredOrders =
                orderRepository
                        .countByRestaurant_IdAndStatus(
                                restaurant.getId(),
                                OrderStatus.DELIVERED
                        );

        BigDecimal deliveredSubtotal =
                orderRepository
                        .sumSubtotalByRestaurantIdAndStatus(
                                restaurant.getId(),
                                OrderStatus.DELIVERED
                        );

        return createRestaurantEarnings(
                restaurant,
                deliveredOrders,
                deliveredSubtotal
        );
    }

    // ==========================================
    // RESTAURANT DATE-RANGE EARNINGS
    // ==========================================

    private SuperAdminRestaurantEarningsResponse
            calculateRestaurantEarningsForDateRange(
                    Restaurant restaurant,
                    LocalDateTime start,
                    LocalDateTime end
            ) {

        long deliveredOrders =
                orderRepository
                        .countByRestaurant_IdAndStatusAndCreatedAtBetween(
                                restaurant.getId(),
                                OrderStatus.DELIVERED,
                                start,
                                end
                        );

        BigDecimal deliveredSubtotal =
                orderRepository
                        .sumSubtotalByRestaurantStatusAndDateRange(
                                restaurant.getId(),
                                OrderStatus.DELIVERED,
                                start,
                                end
                        );

        return createRestaurantEarnings(
                restaurant,
                deliveredOrders,
                deliveredSubtotal
        );
    }

    // ==========================================
    // COMMISSION CALCULATION
    // ==========================================

    private SuperAdminRestaurantEarningsResponse
            createRestaurantEarnings(
                    Restaurant restaurant,
                    long deliveredOrders,
                    BigDecimal deliveredSubtotal
            ) {

        BigDecimal safeSubtotal =
                deliveredSubtotal == null
                        ? BigDecimal.ZERO
                        : deliveredSubtotal;

        BigDecimal commissionPercentage =
                restaurant.getCommissionPercentage()
                        == null
                        ? BigDecimal.ZERO
                        : restaurant.getCommissionPercentage();

        BigDecimal platformCommission =
                safeSubtotal
                        .multiply(
                                commissionPercentage
                        )
                        .divide(
                                new BigDecimal("100"),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal restaurantNetEarnings =
                safeSubtotal.subtract(
                        platformCommission
                );

        return new SuperAdminRestaurantEarningsResponse(

                restaurant.getId(),
                restaurant.getName(),

                commissionPercentage,

                deliveredOrders,

                money(
                        safeSubtotal
                ),

                money(
                        platformCommission
                ),

                money(
                        restaurantNetEarnings
                )
        );
    }

    // ==========================================
    // MONEY FORMAT
    // ==========================================

    private BigDecimal money(
            BigDecimal amount
    ) {

        return amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}