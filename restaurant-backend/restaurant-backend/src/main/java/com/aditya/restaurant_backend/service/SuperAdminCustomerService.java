package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.SuperAdminCustomerActiveRequest;
import com.aditya.restaurant_backend.dto.SuperAdminCustomerResponse;
import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;
import com.aditya.restaurant_backend.repository.OrderRepository;

@Service
public class SuperAdminCustomerService {

    private final CustomerAccountRepository
            customerAccountRepository;

    private final OrderRepository
            orderRepository;

    public SuperAdminCustomerService(
            CustomerAccountRepository customerAccountRepository,
            OrderRepository orderRepository
    ) {
        this.customerAccountRepository =
                customerAccountRepository;

        this.orderRepository =
                orderRepository;
    }

    // ==========================================
    // LIST ALL CUSTOMERS
    // ==========================================

    @Transactional(readOnly = true)
    public List<SuperAdminCustomerResponse>
            getAllCustomers() {

        return customerAccountRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // GET ONE CUSTOMER
    // ==========================================

    @Transactional(readOnly = true)
    public SuperAdminCustomerResponse
            getCustomer(
                    Long customerId
            ) {

        return toResponse(
                findCustomer(
                        customerId
                )
        );
    }

    // ==========================================
    // GET CUSTOMER ORDERS
    // ==========================================

    @Transactional(readOnly = true)
    public List<CustomerOrder>
            getCustomerOrders(
                    Long customerId
            ) {

        findCustomer(
                customerId
        );

        return orderRepository
                .findByCustomerAccountIdOrderByCreatedAtDesc(
                        customerId
                );
    }

    // ==========================================
    // ACTIVATE / DEACTIVATE CUSTOMER
    // ==========================================

    @Transactional
    public SuperAdminCustomerResponse
            updateActiveStatus(
                    Long customerId,
                    SuperAdminCustomerActiveRequest request
            ) {

        CustomerAccount customer =
                findCustomer(
                        customerId
                );

        customer.setActive(
                request.active()
        );

        CustomerAccount savedCustomer =
                customerAccountRepository
                        .save(
                                customer
                        );

        return toResponse(
                savedCustomer
        );
    }

    // ==========================================
    // FIND CUSTOMER
    // ==========================================

    private CustomerAccount findCustomer(
            Long customerId
    ) {

        return customerAccountRepository
                .findById(
                        customerId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Customer not found with id: "
                                        + customerId
                        )
                );
    }

    // ==========================================
    // ENTITY -> RESPONSE DTO
    // ==========================================

    private SuperAdminCustomerResponse
            toResponse(
                    CustomerAccount customer
            ) {

        long totalOrders =
                orderRepository
                        .countByCustomerAccountId(
                                customer.getId()
                        );

        return new SuperAdminCustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getDefaultDeliveryAddress(),
                customer.getProvider(),
                customer.isEmailVerified(),
                customer.isActive(),
                totalOrders,
                customer.getCreatedAt()
        );
    }
}