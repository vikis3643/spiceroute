package com.aditya.restaurant_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.RestaurantAdminAuthResponse;
import com.aditya.restaurant_backend.dto.RestaurantAdminChangePasswordRequest;
import com.aditya.restaurant_backend.dto.RestaurantAdminLoginRequest;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantAdmin;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;
import com.aditya.restaurant_backend.repository.RestaurantAdminRepository;

@Service
public class RestaurantAdminAuthService {

    private final RestaurantAdminRepository
            restaurantAdminRepository;

    private final PasswordEncoder
            passwordEncoder;

    private final JwtService
            jwtService;

    public RestaurantAdminAuthService(
            RestaurantAdminRepository restaurantAdminRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.restaurantAdminRepository =
                restaurantAdminRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService =
                jwtService;
    }

    // ==========================================
    // RESTAURANT ADMIN LOGIN
    // ==========================================

    public RestaurantAdminAuthResponse login(
            RestaurantAdminLoginRequest request
    ) {

        String email =
                request.email() == null
                        ? ""
                        : request.email()
                                .trim()
                                .toLowerCase();

        String password =
                request.password() == null
                        ? ""
                        : request.password();

        if (
                email.isBlank()
                || password.isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email and password are required"
            );
        }

        RestaurantAdmin admin =
                restaurantAdminRepository
                        .findByEmailIgnoreCase(
                                email
                        )
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "Invalid email or password"
                                        )
                        );

        if (!admin.isActive()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant admin account is inactive"
            );
        }

        if (
                !passwordEncoder.matches(
                        password,
                        admin.getPasswordHash()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        Restaurant restaurant =
                admin.getRestaurant();

        if (restaurant == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant is not assigned to this admin"
            );
        }

        if (
                restaurant.getApprovalStatus()
                != RestaurantApprovalStatus.APPROVED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant is not approved"
            );
        }

        if (!restaurant.isActive()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant is inactive"
            );
        }

        String token =
                jwtService
                        .generateRestaurantAdminToken(
                                admin.getEmail(),
                                admin.getId(),
                                restaurant.getId()
                        );

        return new RestaurantAdminAuthResponse(
                token,
                "RESTAURANT_ADMIN",
                admin.getId(),
                admin.getFullName(),
                admin.getEmail(),
                restaurant.getId(),
                restaurant.getName(),
                admin.isMustChangePassword()
        );
    }

    // ==========================================
    // CHANGE RESTAURANT ADMIN PASSWORD
    // ==========================================

    public void changePassword(
            Long adminId,
            Long restaurantId,
            RestaurantAdminChangePasswordRequest request
    ) {

        if (
                adminId == null
                || restaurantId == null
        ) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Invalid Restaurant Admin token"
            );
        }

        RestaurantAdmin admin =
                restaurantAdminRepository
                        .findById(
                                adminId
                        )
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Restaurant Admin not found"
                                        )
                        );

        // ======================================
        // ADMIN ACTIVE CHECK
        // ======================================

        if (!admin.isActive()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant Admin account is inactive"
            );
        }

        // ======================================
        // RESTAURANT ACCESS CHECK
        // ======================================

        Restaurant restaurant =
                admin.getRestaurant();

        if (
                restaurant == null
                || !restaurant.getId()
                        .equals(
                                restaurantId
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant access mismatch"
            );
        }

        // ======================================
        // RESTAURANT APPROVAL CHECK
        // ======================================

        if (
                restaurant.getApprovalStatus()
                != RestaurantApprovalStatus.APPROVED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant is not approved"
            );
        }

        if (!restaurant.isActive()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant is inactive"
            );
        }

        String currentPassword =
                request.currentPassword() == null
                        ? ""
                        : request.currentPassword();

        String newPassword =
                request.newPassword() == null
                        ? ""
                        : request.newPassword();

        // ======================================
        // CURRENT PASSWORD CHECK
        // ======================================

        if (
                !passwordEncoder.matches(
                        currentPassword,
                        admin.getPasswordHash()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Current password is incorrect"
            );
        }

        // ======================================
        // PREVENT SAME PASSWORD
        // ======================================

        if (
                passwordEncoder.matches(
                        newPassword,
                        admin.getPasswordHash()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "New password must be different from current password"
            );
        }

        // ======================================
        // SAVE NEW PASSWORD
        // ======================================

        admin.setPasswordHash(
                passwordEncoder.encode(
                        newPassword
                )
        );

        // First-login password requirement
        // is now completed.
        admin.setMustChangePassword(
                false
        );

        restaurantAdminRepository.save(
                admin
        );
    }
}