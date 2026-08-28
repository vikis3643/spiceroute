package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.CreateRestaurantAdminRequest;
import com.aditya.restaurant_backend.dto.RestaurantAdminActiveRequest;
import com.aditya.restaurant_backend.dto.RestaurantAdminPasswordResetRequest;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantAdminResponse;
import com.aditya.restaurant_backend.dto.UpdateRestaurantAdminRequest;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantAdmin;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;
import com.aditya.restaurant_backend.repository.RestaurantAdminRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class SuperAdminRestaurantAdminService {

    private final RestaurantAdminRepository
            restaurantAdminRepository;

    private final RestaurantRepository
            restaurantRepository;

    private final PasswordEncoder
            passwordEncoder;

    public SuperAdminRestaurantAdminService(
            RestaurantAdminRepository restaurantAdminRepository,
            RestaurantRepository restaurantRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.restaurantAdminRepository =
                restaurantAdminRepository;

        this.restaurantRepository =
                restaurantRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    // ==========================================
    // LIST ALL RESTAURANT ADMINS
    // ==========================================

    @Transactional(readOnly = true)
    public List<SuperAdminRestaurantAdminResponse>
            getAllAdmins() {

        return restaurantAdminRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // LIST ADMINS BY RESTAURANT
    // ==========================================

    @Transactional(readOnly = true)
    public List<SuperAdminRestaurantAdminResponse>
            getAdminsByRestaurant(
                    Long restaurantId
            ) {

        findRestaurant(restaurantId);

        return restaurantAdminRepository
                .findByRestaurantIdOrderByCreatedAtDesc(
                        restaurantId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // GET ONE ADMIN
    // ==========================================

    @Transactional(readOnly = true)
    public SuperAdminRestaurantAdminResponse
            getAdmin(
                    Long adminId
            ) {

        return toResponse(
                findAdmin(adminId)
        );
    }

    // ==========================================
    // CREATE RESTAURANT ADMIN
    // ==========================================

    @Transactional
    public SuperAdminRestaurantAdminResponse
            createAdmin(
                    CreateRestaurantAdminRequest request
            ) {

        String email =
                request.email()
                        .trim()
                        .toLowerCase();

        if (
                restaurantAdminRepository
                        .existsByEmailIgnoreCase(email)
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Restaurant Admin email already exists"
            );
        }

        Restaurant restaurant =
                findRestaurant(
                        request.restaurantId()
                );

        if (
                restaurant.getApprovalStatus()
                        != RestaurantApprovalStatus.APPROVED
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Restaurant must be approved before creating an admin"
            );
        }

        RestaurantAdmin admin =
                new RestaurantAdmin();

        admin.setRestaurant(
                restaurant
        );

        admin.setFullName(
                request.fullName().trim()
        );

        admin.setEmail(
                email
        );

        admin.setPasswordHash(
                passwordEncoder.encode(
                        request.password()
                )
        );

        admin.setActive(true);

        RestaurantAdmin savedAdmin =
                restaurantAdminRepository
                        .save(admin);

        return toResponse(
                savedAdmin
        );
    }

    // ==========================================
    // UPDATE ADMIN DETAILS
    // ==========================================

    @Transactional
    public SuperAdminRestaurantAdminResponse
            updateAdmin(
                    Long adminId,
                    UpdateRestaurantAdminRequest request
            ) {

        RestaurantAdmin admin =
                findAdmin(adminId);

        String newEmail =
                request.email()
                        .trim()
                        .toLowerCase();

        restaurantAdminRepository
                .findByEmailIgnoreCase(
                        newEmail
                )
                .ifPresent(existingAdmin -> {

                    if (
                            !existingAdmin
                                    .getId()
                                    .equals(adminId)
                    ) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Restaurant Admin email already exists"
                        );
                    }
                });

        admin.setFullName(
                request.fullName().trim()
        );

        admin.setEmail(
                newEmail
        );

        return toResponse(
                restaurantAdminRepository
                        .save(admin)
        );
    }

    // ==========================================
    // ACTIVATE / DEACTIVATE ADMIN
    // ==========================================

    @Transactional
    public SuperAdminRestaurantAdminResponse
            updateActiveStatus(
                    Long adminId,
                    RestaurantAdminActiveRequest request
            ) {

        RestaurantAdmin admin =
                findAdmin(adminId);

        if (request.active()) {

            Restaurant restaurant =
                    admin.getRestaurant();

            if (
                    restaurant.getApprovalStatus()
                            != RestaurantApprovalStatus.APPROVED
                    ||
                    !restaurant.isActive()
            ) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Admin cannot be activated because the restaurant is not approved and active"
                );
            }
        }

        admin.setActive(
                request.active()
        );

        return toResponse(
                restaurantAdminRepository
                        .save(admin)
        );
    }

    // ==========================================
    // RESET ADMIN PASSWORD
    // ==========================================

    @Transactional
    public void resetPassword(
            Long adminId,
            RestaurantAdminPasswordResetRequest request
    ) {

        RestaurantAdmin admin =
                findAdmin(adminId);

        admin.setPasswordHash(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        restaurantAdminRepository
                .save(admin);
    }

    // ==========================================
    // FIND RESTAURANT ADMIN
    // ==========================================

    private RestaurantAdmin findAdmin(
            Long adminId
    ) {

        return restaurantAdminRepository
                .findById(adminId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Restaurant Admin not found with id: "
                                        + adminId
                        )
                );
    }

    // ==========================================
    // FIND RESTAURANT
    // ==========================================

    private Restaurant findRestaurant(
            Long restaurantId
    ) {

        return restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Restaurant not found with id: "
                                        + restaurantId
                        )
                );
    }

    // ==========================================
    // ENTITY -> RESPONSE DTO
    // ==========================================

    private SuperAdminRestaurantAdminResponse
            toResponse(
                    RestaurantAdmin admin
            ) {

        Restaurant restaurant =
                admin.getRestaurant();

        return new SuperAdminRestaurantAdminResponse(
                admin.getId(),
                restaurant.getId(),
                restaurant.getName(),
                admin.getFullName(),
                admin.getEmail(),
                admin.isActive(),
                admin.getCreatedAt(),
                admin.getUpdatedAt()
        );
    }
}