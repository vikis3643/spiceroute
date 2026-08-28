package com.aditya.restaurant_backend.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.entity.PaymentMethod;
import com.aditya.restaurant_backend.entity.PaymentStatus;

public interface OrderRepository
        extends JpaRepository<CustomerOrder, Long> {

    // ==========================================
    // EXISTING METHODS
    // ==========================================

    List<CustomerOrder>
            findAllByOrderByCreatedAtDesc();

    List<CustomerOrder>
            findByCustomerAccountIdOrderByCreatedAtDesc(
                    Long customerAccountId
            );

    long countByCustomerAccountId(
            Long customerAccountId
    );

    // ==========================================
    // MULTI-RESTAURANT METHODS
    // ==========================================

    List<CustomerOrder>
            findByRestaurant_IdOrderByCreatedAtDesc(
                    Long restaurantId
            );

    Optional<CustomerOrder>
            findByIdAndRestaurant_Id(
                    Long orderId,
                    Long restaurantId
            );

    long countByRestaurant_Id(
            Long restaurantId
    );

    // ==========================================
    // RESTAURANT DASHBOARD METHODS
    // ==========================================

    long countByRestaurant_IdAndStatus(
            Long restaurantId,
            OrderStatus status
    );

    @Query("""
            SELECT COALESCE(
                SUM(o.totalAmount),
                0
            )
            FROM CustomerOrder o
            WHERE o.restaurant.id = :restaurantId
              AND o.status = :status
            """)
    BigDecimal sumTotalAmountByRestaurantIdAndStatus(
            @Param("restaurantId")
            Long restaurantId,

            @Param("status")
            OrderStatus status
    );

    @Query("""
            SELECT COALESCE(
                SUM(o.subtotal),
                0
            )
            FROM CustomerOrder o
            WHERE o.restaurant.id = :restaurantId
              AND o.status = :status
            """)
    BigDecimal sumSubtotalByRestaurantIdAndStatus(
            @Param("restaurantId")
            Long restaurantId,

            @Param("status")
            OrderStatus status
    );

    // ==========================================
    // SALES / DATE RANGE METHODS
    // ==========================================

    long countByRestaurant_IdAndCreatedAtBetween(
            Long restaurantId,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByRestaurant_IdAndStatusAndCreatedAtBetween(
            Long restaurantId,
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    List<CustomerOrder>
            findByRestaurant_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
                    Long restaurantId,
                    LocalDateTime start,
                    LocalDateTime end
            );

    @Query("""
            SELECT COALESCE(
                SUM(o.totalAmount),
                0
            )
            FROM CustomerOrder o
            WHERE o.restaurant.id = :restaurantId
              AND o.status = :status
              AND o.createdAt >= :start
              AND o.createdAt < :end
            """)
    BigDecimal sumTotalAmountByRestaurantStatusAndDateRange(
            @Param("restaurantId")
            Long restaurantId,

            @Param("status")
            OrderStatus status,

            @Param("start")
            LocalDateTime start,

            @Param("end")
            LocalDateTime end
    );

    @Query("""
            SELECT COALESCE(
                SUM(o.subtotal),
                0
            )
            FROM CustomerOrder o
            WHERE o.restaurant.id = :restaurantId
              AND o.status = :status
              AND o.createdAt >= :start
              AND o.createdAt < :end
            """)
    BigDecimal sumSubtotalByRestaurantStatusAndDateRange(
            @Param("restaurantId")
            Long restaurantId,

            @Param("status")
            OrderStatus status,

            @Param("start")
            LocalDateTime start,

            @Param("end")
            LocalDateTime end
    );

    // ==========================================
    // SUPER ADMIN PLATFORM DASHBOARD
    // ==========================================

    long countByStatus(
            OrderStatus status
    );

    @Query("""
            SELECT COALESCE(
                SUM(o.totalAmount),
                0
            )
            FROM CustomerOrder o
            WHERE o.status = :status
            """)
    BigDecimal sumTotalAmountByStatus(
            @Param("status")
            OrderStatus status
    );

    // ==========================================
    // SUPER ADMIN ORDER FILTERS
    // ==========================================

    List<CustomerOrder>
            findByStatusOrderByCreatedAtDesc(
                    OrderStatus status
            );

    List<CustomerOrder>
            findByPaymentMethodOrderByCreatedAtDesc(
                    PaymentMethod paymentMethod
            );

    List<CustomerOrder>
            findByPaymentStatusOrderByCreatedAtDesc(
                    PaymentStatus paymentStatus
            );

    List<CustomerOrder>
            findByRestaurant_IdAndStatusOrderByCreatedAtDesc(
                    Long restaurantId,
                    OrderStatus status
            );

    List<CustomerOrder>
            findByRestaurant_IdAndPaymentMethodOrderByCreatedAtDesc(
                    Long restaurantId,
                    PaymentMethod paymentMethod
            );

    List<CustomerOrder>
            findByRestaurant_IdAndPaymentStatusOrderByCreatedAtDesc(
                    Long restaurantId,
                    PaymentStatus paymentStatus
            );

    // ==========================================
    // SUPER ADMIN PAYMENT SUMMARY
    // ==========================================

    long countByPaymentMethod(
            PaymentMethod paymentMethod
    );

    long countByPaymentStatus(
            PaymentStatus paymentStatus
    );

    @Query("""
            SELECT COALESCE(
                SUM(o.totalAmount),
                0
            )
            FROM CustomerOrder o
            WHERE o.paymentStatus = :paymentStatus
            """)
    BigDecimal sumTotalAmountByPaymentStatus(
            @Param("paymentStatus")
            PaymentStatus paymentStatus
    );

    // ==========================================
    // SUPER ADMIN REPORTS - PLATFORM DATE RANGE
    // ==========================================

    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime start,
            LocalDateTime end
    );

    long countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
            SELECT COALESCE(
                SUM(o.totalAmount),
                0
            )
            FROM CustomerOrder o
            WHERE o.status = :status
              AND o.createdAt >= :start
              AND o.createdAt < :end
            """)
    BigDecimal sumTotalAmountByStatusAndDateRange(
            @Param("status")
            OrderStatus status,

            @Param("start")
            LocalDateTime start,

            @Param("end")
            LocalDateTime end
    );

    // ==========================================
    // SUPER ADMIN REPORTS - RESTAURANT DATE RANGE
    // ==========================================

    long countByRestaurant_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            Long restaurantId,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByRestaurant_IdAndStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            Long restaurantId,
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
}