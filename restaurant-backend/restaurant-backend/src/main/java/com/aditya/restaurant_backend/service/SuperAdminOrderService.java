package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.SuperAdminOrderItemResponse;
import com.aditya.restaurant_backend.dto.SuperAdminOrderResponse;
import com.aditya.restaurant_backend.dto.SuperAdminPaymentSummaryResponse;
import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.OrderItem;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.entity.PaymentMethod;
import com.aditya.restaurant_backend.entity.PaymentStatus;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.OrderRepository;

@Service
public class SuperAdminOrderService {

    private final OrderRepository
            orderRepository;

    public SuperAdminOrderService(
            OrderRepository orderRepository
    ) {
        this.orderRepository =
                orderRepository;
    }

    // ==========================================
    // LIST / FILTER ORDERS
    // ==========================================

    @Transactional(readOnly = true)
    public List<SuperAdminOrderResponse>
            getOrders(
                    Long restaurantId,
                    OrderStatus status,
                    PaymentMethod paymentMethod,
                    PaymentStatus paymentStatus
            ) {

        List<CustomerOrder> orders;

        if (
                restaurantId != null
                && status != null
        ) {

            orders =
                    orderRepository
                            .findByRestaurant_IdAndStatusOrderByCreatedAtDesc(
                                    restaurantId,
                                    status
                            );

        } else if (
                restaurantId != null
                && paymentMethod != null
        ) {

            orders =
                    orderRepository
                            .findByRestaurant_IdAndPaymentMethodOrderByCreatedAtDesc(
                                    restaurantId,
                                    paymentMethod
                            );

        } else if (
                restaurantId != null
                && paymentStatus != null
        ) {

            orders =
                    orderRepository
                            .findByRestaurant_IdAndPaymentStatusOrderByCreatedAtDesc(
                                    restaurantId,
                                    paymentStatus
                            );

        } else if (
                restaurantId != null
        ) {

            orders =
                    orderRepository
                            .findByRestaurant_IdOrderByCreatedAtDesc(
                                    restaurantId
                            );

        } else if (
                status != null
        ) {

            orders =
                    orderRepository
                            .findByStatusOrderByCreatedAtDesc(
                                    status
                            );

        } else if (
                paymentMethod != null
        ) {

            orders =
                    orderRepository
                            .findByPaymentMethodOrderByCreatedAtDesc(
                                    paymentMethod
                            );

        } else if (
                paymentStatus != null
        ) {

            orders =
                    orderRepository
                            .findByPaymentStatusOrderByCreatedAtDesc(
                                    paymentStatus
                            );

        } else {

            orders =
                    orderRepository
                            .findAllByOrderByCreatedAtDesc();
        }

        return orders
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // GET ONE ORDER
    // ==========================================

    @Transactional(readOnly = true)
    public SuperAdminOrderResponse
            getOrder(
                    Long orderId
            ) {

        CustomerOrder order =
                orderRepository
                        .findById(
                                orderId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Order not found with id: "
                                                + orderId
                                )
                        );

        return toResponse(
                order
        );
    }

    // ==========================================
    // PAYMENT SUMMARY
    // ==========================================

    @Transactional(readOnly = true)
    public SuperAdminPaymentSummaryResponse
            getPaymentSummary() {

        BigDecimal totalPaid =
                orderRepository
                        .sumTotalAmountByPaymentStatus(
                                PaymentStatus.PAID
                        );

        return new SuperAdminPaymentSummaryResponse(

                orderRepository.count(),

                orderRepository
                        .countByPaymentMethod(
                                PaymentMethod.CASH_ON_DELIVERY
                        ),

                orderRepository
                        .countByPaymentMethod(
                                PaymentMethod.DEMO_RAZORPAY
                        ),

                orderRepository
                        .countByPaymentStatus(
                                PaymentStatus.PAID
                        ),

                orderRepository
                        .countByPaymentStatus(
                                PaymentStatus.PENDING
                        ),

                orderRepository
                        .countByPaymentStatus(
                                PaymentStatus.FAILED
                        ),

                totalPaid == null
                        ? BigDecimal.ZERO
                        : totalPaid
        );
    }

    // ==========================================
    // ENTITY -> DTO
    // ==========================================

    private SuperAdminOrderResponse
            toResponse(
                    CustomerOrder order
            ) {

        Restaurant restaurant =
                order.getRestaurant();

        CustomerAccount customer =
                order.getCustomerAccount();

        List<SuperAdminOrderItemResponse>
                items =
                order.getItems()
                        .stream()
                        .map(
                                this::toItemResponse
                        )
                        .toList();

        return new SuperAdminOrderResponse(

                order.getId(),

                restaurant.getId(),
                restaurant.getName(),

                customer != null
                        ? customer.getId()
                        : null,

                order.getCustomerName(),

                customer != null
                        ? customer.getEmail()
                        : null,

                order.getPhone(),

                order.getDeliveryAddress(),
                order.getDeliveryLatitude(),
                order.getDeliveryLongitude(),

                order.getStatus(),

                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getTransactionId(),

                order.getOrderTiming(),
                order.getMealSlot(),
                order.getScheduledFor(),
                order.getPreparationStartAt(),

                order.getSubtotal(),
                order.getDiscountAmount(),
                order.getAppliedDiscountNames(),
                order.getDeliveryFee(),
                order.getTotalAmount(),

                order.getCreatedAt(),

                items
        );
    }

    private SuperAdminOrderItemResponse
            toItemResponse(
                    OrderItem item
            ) {

        return new SuperAdminOrderItemResponse(

                item.getId(),
                item.getMenuItemId(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }
}