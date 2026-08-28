package com.aditya.restaurant_backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.CustomerAuthResponse;
import com.aditya.restaurant_backend.dto.CustomerLoginRequest;
import com.aditya.restaurant_backend.dto.CustomerRegisterRequest;
import com.aditya.restaurant_backend.dto.ForgotPasswordRequest;
import com.aditya.restaurant_backend.dto.GoogleLoginRequest;
import com.aditya.restaurant_backend.dto.ResetPasswordRequest;
import com.aditya.restaurant_backend.service.CustomerAuthService;
import com.aditya.restaurant_backend.service.PasswordResetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer-auth")
public class CustomerAuthController {

    private final CustomerAuthService
            customerAuthService;

    private final PasswordResetService
            passwordResetService;

    public CustomerAuthController(
            CustomerAuthService
                    customerAuthService,
            PasswordResetService
                    passwordResetService
    ) {
        this.customerAuthService =
                customerAuthService;

        this.passwordResetService =
                passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid
            @RequestBody
            CustomerRegisterRequest request
    ) {
        try {
            CustomerAuthResponse response =
                    customerAuthService
                            .register(
                                    request
                            );

            return ResponseEntity
                    .status(
                            HttpStatus.CREATED
                    )
                    .body(response);

        } catch (
                IllegalStateException
                        exception
        ) {
            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT
                    )
                    .body(
                            Map.of(
                                    "message",
                                    exception
                                            .getMessage()
                            )
                    );

        } catch (
                IllegalArgumentException
                        exception
        ) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    exception
                                            .getMessage()
                            )
                    );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid
            @RequestBody
            CustomerLoginRequest request
    ) {
        try {
            CustomerAuthResponse response =
                    customerAuthService
                            .login(
                                    request
                            );

            return ResponseEntity.ok(
                    response
            );

        } catch (
                BadCredentialsException
                        exception
        ) {
            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid email, phone number or password"
                            )
                    );
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(
            @Valid
            @RequestBody
            GoogleLoginRequest request
    ) {
        try {
            CustomerAuthResponse response =
                    customerAuthService
                            .loginWithGoogle(
                                    request
                            );

            return ResponseEntity.ok(
                    response
            );

        } catch (
                BadCredentialsException
                        exception
        ) {
            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "message",
                                    exception
                                            .getMessage()
                            )
                    );
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?>
            forgotPassword(
                    @Valid
                    @RequestBody
                    ForgotPasswordRequest
                            request
            ) {

        passwordResetService
                .requestPasswordReset(
                        request.getEmail()
                );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "If an account exists with this email, a reset link has been sent."
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?>
            resetPassword(
                    @Valid
                    @RequestBody
                    ResetPasswordRequest
                            request
            ) {

        try {
            passwordResetService
                    .resetPassword(
                            request
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Your password has been reset successfully."
                    )
            );

        } catch (
                IllegalArgumentException
                        exception
        ) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    exception
                                            .getMessage()
                            )
                    );
        }
    }
}