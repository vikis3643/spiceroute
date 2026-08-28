package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.DiscountQuoteRequest;
import com.aditya.restaurant_backend.dto.DiscountQuoteResponse;
import com.aditya.restaurant_backend.dto.PlaceOrderRequest;
import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService
            orderService;

    public OrderController(
            OrderService orderService
    ) {
        this.orderService =
                orderService;
    }

    // ==========================================
    // ORDER QUOTE
    // ==========================================

    @PostMapping("/quote")
    public DiscountQuoteResponse
            calculateDiscountQuote(
                    @Valid
                    @RequestBody
                    DiscountQuoteRequest request
            ) {

        return orderService
                .calculateDiscountQuote(
                        request
                );
    }

    // ==========================================
    // PLACE ORDER
    // ==========================================

    @PostMapping
    public ResponseEntity<CustomerOrder>
            placeOrder(
                    @Valid
                    @RequestBody
                    PlaceOrderRequest request,

                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        CustomerOrder savedOrder =
                orderService
                        .placeOrder(
                                request,
                                jwt.getSubject()
                        );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        savedOrder
                );
    }

    // ==========================================
    // CUSTOMER OWN ORDERS
    // ==========================================

    @GetMapping("/my-orders")
    public List<CustomerOrder>
            getMyOrders(
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        return orderService
                .getOrdersForCustomer(
                        jwt.getSubject()
                );
    }

    // ==========================================
    // CUSTOMER CANCEL OWN ORDER
    // ==========================================

    @PatchMapping("/{id}/cancel")
    public CustomerOrder
            cancelMyOrder(
                    @PathVariable
                    Long id,

                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        return orderService
                .cancelCustomerOrder(
                        id,
                        jwt.getSubject()
                );
    }
}