package com.aditya.restaurant_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.RestaurantAdminAccountResponse;
import com.aditya.restaurant_backend.dto.RestaurantAdminChangePasswordRequest;
import com.aditya.restaurant_backend.dto.UpdateRestaurantAdminAccountRequest;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantAdmin;
import com.aditya.restaurant_backend.repository.RestaurantAdminRepository;

@Service
public class RestaurantAdminAccountService {

    private final RestaurantAdminRepository
            restaurantAdminRepository;

    private final PasswordEncoder
            passwordEncoder;

    public RestaurantAdminAccountService(
            RestaurantAdminRepository restaurantAdminRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.restaurantAdminRepository =
                restaurantAdminRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    // ==========================================
    // GET OWN ACCOUNT
    // ==========================================

    @Transactional(readOnly = true)
    public RestaurantAdminAccountResponse
            getAccount(
                    Long adminId
            ) {

        return toResponse(
                findAdmin(
                        adminId
                )
        );
    }

    // ==========================================
    // UPDATE OWN ACCOUNT
    // ==========================================

    @Transactional
    public RestaurantAdminAccountResponse
            updateAccount(
                    Long adminId,
                    UpdateRestaurantAdminAccountRequest request
            ) {

        RestaurantAdmin admin =
                findAdmin(
                        adminId
                );

        String email =
                normalizeEmail(
                        request.email()
                );

        restaurantAdminRepository
                .findByEmailIgnoreCase(
                        email
                )
                .ifPresent(existing -> {

                    if (
                            !existing.getId()
                                    .equals(
                                            adminId
                                    )
                    ) {

                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Restaurant admin email already exists"
                        );
                    }
                });

        admin.setFullName(
                request.fullName()
                        .trim()
        );

        admin.setEmail(
                email
        );

        RestaurantAdmin savedAdmin =
                restaurantAdminRepository
                        .save(
                                admin
                        );

        return toResponse(
                savedAdmin
        );
    }

    // ==========================================
    // CHANGE PASSWORD
    // ==========================================

    @Transactional
    public void changePassword(
            Long adminId,
            RestaurantAdminChangePasswordRequest request
    ) {

        RestaurantAdmin admin =
                findAdmin(
                        adminId
                );

        if (
                !passwordEncoder.matches(
                        request.currentPassword(),
                        admin.getPasswordHash()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Current password is incorrect"
            );
        }

        if (
                passwordEncoder.matches(
                        request.newPassword(),
                        admin.getPasswordHash()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "New password must be different from current password"
            );
        }

        admin.setPasswordHash(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        restaurantAdminRepository
                .save(
                        admin
                );
    }

    // ==========================================
    // FIND ADMIN
    // ==========================================

    private RestaurantAdmin findAdmin(
            Long adminId
    ) {

        return restaurantAdminRepository
                .findById(
                        adminId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Restaurant admin not found with id: "
                                        + adminId
                        )
                );
    }

    // ==========================================
    // ENTITY -> RESPONSE
    // ==========================================

    private RestaurantAdminAccountResponse
            toResponse(
                    RestaurantAdmin admin
            ) {

        Restaurant restaurant =
                admin.getRestaurant();

        return new RestaurantAdminAccountResponse(

                admin.getId(),
                admin.getFullName(),
                admin.getEmail(),
                admin.isActive(),

                restaurant.getId(),
                restaurant.getName(),

                admin.getCreatedAt(),
                admin.getUpdatedAt()
        );
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private String normalizeEmail(
            String email
    ) {

        return email
                .trim()
                .toLowerCase();
    }
}