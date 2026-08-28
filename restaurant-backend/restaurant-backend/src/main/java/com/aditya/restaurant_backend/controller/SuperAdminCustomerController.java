package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.SuperAdminCustomerActiveRequest;
import com.aditya.restaurant_backend.dto.SuperAdminCustomerResponse;
import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.service.SuperAdminCustomerService;

@RestController
@RequestMapping("/api/super-admin/customers")
public class SuperAdminCustomerController {

    private final SuperAdminCustomerService
            superAdminCustomerService;

    public SuperAdminCustomerController(
            SuperAdminCustomerService superAdminCustomerService
    ) {
        this.superAdminCustomerService =
                superAdminCustomerService;
    }

    // ==========================================
    // LIST ALL CUSTOMERS
    // ==========================================

    @GetMapping
    public ResponseEntity<
            List<SuperAdminCustomerResponse>
            > getAllCustomers() {

        return ResponseEntity.ok(
                superAdminCustomerService
                        .getAllCustomers()
        );
    }

    // ==========================================
    // GET ONE CUSTOMER
    // ==========================================

    @GetMapping("/{customerId}")
    public ResponseEntity<
            SuperAdminCustomerResponse
            > getCustomer(
                    @PathVariable
                    Long customerId
            ) {

        return ResponseEntity.ok(
                superAdminCustomerService
                        .getCustomer(
                                customerId
                        )
        );
    }

    // ==========================================
    // GET CUSTOMER ORDERS
    // ==========================================

    @GetMapping(
            "/{customerId}/orders"
    )
    public ResponseEntity<
            List<CustomerOrder>
            > getCustomerOrders(
                    @PathVariable
                    Long customerId
            ) {

        return ResponseEntity.ok(
                superAdminCustomerService
                        .getCustomerOrders(
                                customerId
                        )
        );
    }

    // ==========================================
    // ACTIVATE / DEACTIVATE CUSTOMER
    // ==========================================

    @PatchMapping(
            "/{customerId}/active"
    )
    public ResponseEntity<
            SuperAdminCustomerResponse
            > updateActiveStatus(
                    @PathVariable
                    Long customerId,

                    @RequestBody
                    SuperAdminCustomerActiveRequest request
            ) {

        return ResponseEntity.ok(
                superAdminCustomerService
                        .updateActiveStatus(
                                customerId,
                                request
                        )
        );
    }
}