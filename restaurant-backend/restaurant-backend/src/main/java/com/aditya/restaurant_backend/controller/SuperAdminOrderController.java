package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.SuperAdminOrderResponse;
import com.aditya.restaurant_backend.dto.SuperAdminPaymentSummaryResponse;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.entity.PaymentMethod;
import com.aditya.restaurant_backend.entity.PaymentStatus;
import com.aditya.restaurant_backend.service.SuperAdminOrderService;

@RestController
@RequestMapping("/api/super-admin/orders")
public class SuperAdminOrderController {

    private final SuperAdminOrderService
            superAdminOrderService;

    public SuperAdminOrderController(
            SuperAdminOrderService superAdminOrderService
    ) {
        this.superAdminOrderService =
                superAdminOrderService;
    }

    @GetMapping
    public ResponseEntity<
            List<SuperAdminOrderResponse>
            > getOrders(

                    @RequestParam(
                            required = false
                    )
                    Long restaurantId,

                    @RequestParam(
                            required = false
                    )
                    OrderStatus status,

                    @RequestParam(
                            required = false
                    )
                    PaymentMethod paymentMethod,

                    @RequestParam(
                            required = false
                    )
                    PaymentStatus paymentStatus
            ) {

        return ResponseEntity.ok(
                superAdminOrderService
                        .getOrders(
                                restaurantId,
                                status,
                                paymentMethod,
                                paymentStatus
                        )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<
            SuperAdminOrderResponse
            > getOrder(
                    @PathVariable
                    Long orderId
            ) {

        return ResponseEntity.ok(
                superAdminOrderService
                        .getOrder(
                                orderId
                        )
        );
    }

    @GetMapping("/payments/summary")
    public ResponseEntity<
            SuperAdminPaymentSummaryResponse
            > getPaymentSummary() {

        return ResponseEntity.ok(
                superAdminOrderService
                        .getPaymentSummary()
        );
    }
}