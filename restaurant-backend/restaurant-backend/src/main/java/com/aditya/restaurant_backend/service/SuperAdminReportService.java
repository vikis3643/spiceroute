package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aditya.restaurant_backend.dto.SuperAdminCustomerReportResponse;
import com.aditya.restaurant_backend.dto.SuperAdminOrderReportResponse;
import com.aditya.restaurant_backend.dto.SuperAdminReportSummaryResponse;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantPerformanceResponse;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;
import com.aditya.restaurant_backend.repository.OrderRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class SuperAdminReportService {

    private final CustomerAccountRepository
            customerAccountRepository;

    private final OrderRepository
            orderRepository;

    private final RestaurantRepository
            restaurantRepository;

    public SuperAdminReportService(
            CustomerAccountRepository customerAccountRepository,
            OrderRepository orderRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.customerAccountRepository =
                customerAccountRepository;

        this.orderRepository =
                orderRepository;

        this.restaurantRepository =
                restaurantRepository;
    }

    // ==========================================
    // REPORT SUMMARY
    // ==========================================

    @Transactional(readOnly = true)
    public SuperAdminReportSummaryResponse
            getReport(
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

        SuperAdminCustomerReportResponse
                customerReport =
                buildCustomerReport(
                        start,
                        end
                );

        SuperAdminOrderReportResponse
                orderReport =
                buildOrderReport(
                        start,
                        end
                );

        List<SuperAdminRestaurantPerformanceResponse>
                restaurantReports =
                buildRestaurantReports(
                        start,
                        end
                );

        return new SuperAdminReportSummaryResponse(
                startDate,
                endDate,
                customerReport,
                orderReport,
                restaurantReports
        );
    }

    // ==========================================
    // CUSTOMER REPORT
    // ==========================================

    private SuperAdminCustomerReportResponse
            buildCustomerReport(
                    LocalDateTime start,
                    LocalDateTime end
            ) {

        long totalCustomers =
                customerAccountRepository.count();

        long activeCustomers =
                customerAccountRepository
                        .countByActiveTrue();

        long inactiveCustomers =
                customerAccountRepository
                        .countByActiveFalse();

        long verifiedCustomers =
                customerAccountRepository
                        .countByEmailVerifiedTrue();

        long unverifiedCustomers =
                customerAccountRepository
                        .countByEmailVerifiedFalse();

        long newCustomers =
                customerAccountRepository
                        .countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                start,
                                end
                        );

        long activeNewCustomers =
                customerAccountRepository
                        .countByActiveTrueAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                start,
                                end
                        );

        return new SuperAdminCustomerReportResponse(
                totalCustomers,
                activeCustomers,
                inactiveCustomers,
                verifiedCustomers,
                unverifiedCustomers,
                newCustomers,
                activeNewCustomers
        );
    }

    // ==========================================
    // ORDER REPORT
    // ==========================================

    private SuperAdminOrderReportResponse
            buildOrderReport(
                    LocalDateTime start,
                    LocalDateTime end
            ) {

        long totalOrders =
                orderRepository
                        .countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                start,
                                end
                        );

        long placedOrders =
                countStatus(
                        OrderStatus.PLACED,
                        start,
                        end
                );

        long confirmedOrders =
                countStatus(
                        OrderStatus.CONFIRMED,
                        start,
                        end
                );

        long preparingOrders =
                countStatus(
                        OrderStatus.PREPARING,
                        start,
                        end
                );

        long readyOrders =
                countStatus(
                        OrderStatus.READY,
                        start,
                        end
                );

        long outForDeliveryOrders =
                countStatus(
                        OrderStatus.OUT_FOR_DELIVERY,
                        start,
                        end
                );

        long deliveredOrders =
                countStatus(
                        OrderStatus.DELIVERED,
                        start,
                        end
                );

        long cancelledOrders =
                countStatus(
                        OrderStatus.CANCELLED,
                        start,
                        end
                );

        BigDecimal deliveredRevenue =
                orderRepository
                        .sumTotalAmountByStatusAndDateRange(
                                OrderStatus.DELIVERED,
                                start,
                                end
                        );

        return new SuperAdminOrderReportResponse(
                totalOrders,
                placedOrders,
                confirmedOrders,
                preparingOrders,
                readyOrders,
                outForDeliveryOrders,
                deliveredOrders,
                cancelledOrders,
                money(
                        deliveredRevenue
                )
        );
    }

    // ==========================================
    // RESTAURANT PERFORMANCE
    // ==========================================

    private List<SuperAdminRestaurantPerformanceResponse>
            buildRestaurantReports(
                    LocalDateTime start,
                    LocalDateTime end
            ) {

        List<Restaurant> restaurants =
                restaurantRepository.findAll();

        List<SuperAdminRestaurantPerformanceResponse>
                reports =
                new ArrayList<>();

        for (Restaurant restaurant : restaurants) {

            Long restaurantId =
                    restaurant.getId();

            long totalOrders =
                    orderRepository
                            .countByRestaurant_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                    restaurantId,
                                    start,
                                    end
                            );

            long deliveredOrders =
                    orderRepository
                            .countByRestaurant_IdAndStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                    restaurantId,
                                    OrderStatus.DELIVERED,
                                    start,
                                    end
                            );

            long cancelledOrders =
                    orderRepository
                            .countByRestaurant_IdAndStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
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

            BigDecimal commissionPercentage =
                    restaurant.getCommissionPercentage()
                            == null
                            ? BigDecimal.ZERO
                            : restaurant.getCommissionPercentage();

            BigDecimal platformCommission =
                    money(
                            deliveredSubtotal
                                    .multiply(
                                            commissionPercentage
                                    )
                                    .divide(
                                            new BigDecimal("100"),
                                            2,
                                            RoundingMode.HALF_UP
                                    )
                    );

            BigDecimal restaurantNetEarnings =
                    money(
                            deliveredSubtotal
                                    .subtract(
                                            platformCommission
                                    )
                    );

            reports.add(
                    new SuperAdminRestaurantPerformanceResponse(

                            restaurantId,
                            restaurant.getName(),

                            restaurant.isActive(),

                            commissionPercentage,

                            totalOrders,
                            deliveredOrders,
                            cancelledOrders,

                            money(
                                    deliveredRevenue
                            ),

                            money(
                                    deliveredSubtotal
                            ),

                            platformCommission,

                            restaurantNetEarnings
                    )
            );
        }

        return reports;
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private long countStatus(
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    ) {

        return orderRepository
                .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        status,
                        start,
                        end
                );
    }

    private BigDecimal money(
            BigDecimal amount
    ) {

        if (amount == null) {
            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        return amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}