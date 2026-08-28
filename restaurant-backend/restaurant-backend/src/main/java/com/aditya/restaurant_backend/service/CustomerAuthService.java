package com.aditya.restaurant_backend.service;

import java.util.Locale;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aditya.restaurant_backend.dto.CustomerAuthResponse;
import com.aditya.restaurant_backend.dto.CustomerLoginRequest;
import com.aditya.restaurant_backend.dto.CustomerRegisterRequest;
import com.aditya.restaurant_backend.dto.GoogleLoginRequest;
import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;
import com.aditya.restaurant_backend.service.GoogleIdentityService.GoogleIdentity;

@Service
public class CustomerAuthService {

    private final CustomerAccountRepository
            customerAccountRepository;

    private final PasswordEncoder
            passwordEncoder;

    private final JwtService jwtService;

    private final GoogleIdentityService
            googleIdentityService;

    public CustomerAuthService(
            CustomerAccountRepository
                    customerAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            GoogleIdentityService
                    googleIdentityService
    ) {
        this.customerAccountRepository =
                customerAccountRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService = jwtService;

        this.googleIdentityService =
                googleIdentityService;
    }

    public CustomerAuthResponse register(
            CustomerRegisterRequest request
    ) {
        String normalizedEmail =
                normalizeEmail(
                        request.getEmail()
                );

        String normalizedPhone =
                normalizePhone(
                        request.getPhone()
                );

        if (!request.getPassword().equals(
                request.getConfirmPassword()
        )) {
            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        if (customerAccountRepository
                .existsByEmailIgnoreCase(
                        normalizedEmail
                )) {

            throw new IllegalStateException(
                    "An account already exists with this email"
            );
        }

        if (customerAccountRepository
                .existsByPhone(
                        normalizedPhone
                )) {

            throw new IllegalStateException(
                    "An account already exists with this phone number"
            );
        }

        CustomerAccount customer =
                new CustomerAccount();

        customer.setFullName(
                request.getFullName().trim()
        );

        customer.setEmail(
                normalizedEmail
        );

        customer.setPhone(
                normalizedPhone
        );

        customer.setPasswordHash(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        customer.setProvider("LOCAL");
        customer.setEmailVerified(false);
        customer.setActive(true);

        CustomerAccount savedCustomer =
                customerAccountRepository.save(
                        customer
                );

        return createResponse(
                savedCustomer
        );
    }

    public CustomerAuthResponse login(
            CustomerLoginRequest request
    ) {
        String identifier =
                normalizeIdentifier(
                        request.getIdentifier()
                );

        CustomerAccount customer =
                customerAccountRepository
                        .findByEmailIgnoreCaseOrPhone(
                                identifier,
                                identifier
                        )
                        .orElseThrow(
                                this::invalidCredentials
                        );

        if (!customer.isActive()
                || customer.getPasswordHash()
                        == null
                || !passwordEncoder.matches(
                        request.getPassword(),
                        customer.getPasswordHash()
                )) {

            throw invalidCredentials();
        }

        return createResponse(customer);
    }

    @Transactional
    public CustomerAuthResponse loginWithGoogle(
            GoogleLoginRequest request
    ) {
        GoogleIdentity googleIdentity =
                googleIdentityService.verify(
                        request.getCredential()
                );

        String normalizedEmail =
                normalizeEmail(
                        googleIdentity.email()
                );

        CustomerAccount customer =
                customerAccountRepository
                        .findByGoogleSubject(
                                googleIdentity.subject()
                        )
                        .orElseGet(() ->
                                findOrCreateGoogleCustomer(
                                        googleIdentity,
                                        normalizedEmail
                                )
                        );

        if (!customer.isActive()) {
            throw new BadCredentialsException(
                    "This customer account is inactive"
            );
        }

        return createResponse(customer);
    }

    private CustomerAccount
            findOrCreateGoogleCustomer(
                    GoogleIdentity googleIdentity,
                    String normalizedEmail
            ) {

        return customerAccountRepository
                .findByEmailIgnoreCase(
                        normalizedEmail
                )
                .map(existingCustomer -> {
                    existingCustomer
                            .setGoogleSubject(
                                    googleIdentity
                                            .subject()
                            );

                    existingCustomer
                            .setEmailVerified(
                                    true
                            );

                    if (existingCustomer
                            .getPasswordHash()
                            != null) {

                        existingCustomer
                                .setProvider(
                                        "LOCAL_GOOGLE"
                                );
                    } else {
                        existingCustomer
                                .setProvider(
                                        "GOOGLE"
                                );
                    }

                    return customerAccountRepository
                            .save(
                                    existingCustomer
                            );
                })
                .orElseGet(() -> {
                    CustomerAccount
                            newCustomer =
                            new CustomerAccount();

                    newCustomer.setFullName(
                            googleIdentity
                                    .fullName()
                                    .trim()
                    );

                    newCustomer.setEmail(
                            normalizedEmail
                    );

                    newCustomer.setPhone(null);

                    newCustomer.setPasswordHash(
                            null
                    );

                    newCustomer.setProvider(
                            "GOOGLE"
                    );

                    newCustomer.setGoogleSubject(
                            googleIdentity
                                    .subject()
                    );

                    newCustomer.setEmailVerified(
                            true
                    );

                    newCustomer.setActive(
                            true
                    );

                    return customerAccountRepository
                            .save(
                                    newCustomer
                            );
                });
    }

    private CustomerAuthResponse createResponse(
            CustomerAccount customer
    ) {
        String token =
                jwtService
                        .generateCustomerToken(
                                customer.getEmail(),
                                customer.getId()
                        );

        return new CustomerAuthResponse(
                token,
                "Bearer",
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                "CUSTOMER"
        );
    }

    private String normalizeIdentifier(
            String identifier
    ) {
        String normalized =
                identifier.trim();

        if (normalized.contains("@")) {
            return normalizeEmail(
                    normalized
            );
        }

        return normalizePhone(
                normalized
        );
    }

    private String normalizeEmail(
            String email
    ) {
        return email
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private String normalizePhone(
            String phone
    ) {
        return phone.trim();
    }

    private BadCredentialsException
            invalidCredentials() {

        return new BadCredentialsException(
                "Invalid email, phone number or password"
        );
    }
}