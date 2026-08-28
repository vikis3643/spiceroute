package com.aditya.restaurant_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.CustomerProfileRequest;
import com.aditya.restaurant_backend.dto.CustomerProfileResponse;
import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;

@Service
public class CustomerProfileService {

    private final CustomerAccountRepository
            customerAccountRepository;

    public CustomerProfileService(
            CustomerAccountRepository
                    customerAccountRepository
    ) {
        this.customerAccountRepository =
                customerAccountRepository;
    }

    public CustomerProfileResponse getProfile(
            String customerEmail
    ) {
        CustomerAccount customer =
                findCustomer(customerEmail);

        return createResponse(customer);
    }

    @Transactional
    public CustomerProfileResponse updateProfile(
            String customerEmail,
            CustomerProfileRequest request
    ) {
        CustomerAccount customer =
                findCustomer(customerEmail);

        customer.setFullName(
                request.getFullName().trim()
        );

        customer.setPhone(
                request.getPhone().trim()
        );

        customer.setDefaultDeliveryAddress(
                request.getDefaultDeliveryAddress()
                        .trim()
        );

        CustomerAccount savedCustomer =
                customerAccountRepository.save(
                        customer
                );

        return createResponse(savedCustomer);
    }

    private CustomerAccount findCustomer(
            String customerEmail
    ) {
        return customerAccountRepository
                .findByEmailIgnoreCase(
                        customerEmail
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Customer account not found"
                        )
                );
    }

    private CustomerProfileResponse createResponse(
            CustomerAccount customer
    ) {
        return new CustomerProfileResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getDefaultDeliveryAddress(),
                customer.getProvider(),
                customer.isEmailVerified()
        );
    }
}