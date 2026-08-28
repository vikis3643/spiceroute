package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.SuperAdminCommissionRequest;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantActiveRequest;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantResponse;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantStatusRequest;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantAdmin;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;
import com.aditya.restaurant_backend.repository.RestaurantAdminRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class SuperAdminRestaurantService {

    private static final String TEMP_PASSWORD_CHARACTERS =
            "ABCDEFGHJKLMNPQRSTUVWXYZ"
                    + "abcdefghijkmnopqrstuvwxyz"
                    + "23456789"
                    + "@#$!";

    private static final int TEMP_PASSWORD_LENGTH =
            12;

    private final SecureRandom secureRandom =
            new SecureRandom();

    private final RestaurantRepository
            restaurantRepository;

    private final RestaurantAdminRepository
            restaurantAdminRepository;

    private final PasswordEncoder
            passwordEncoder;

    private final EmailService
            emailService;

    public SuperAdminRestaurantService(
            RestaurantRepository restaurantRepository,
            RestaurantAdminRepository restaurantAdminRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {

        this.restaurantRepository =
                restaurantRepository;

        this.restaurantAdminRepository =
                restaurantAdminRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.emailService =
                emailService;
    }

    // ==========================================
    // LIST ALL RESTAURANTS
    // ==========================================

    @Transactional(readOnly = true)
    public List<SuperAdminRestaurantResponse>
            getAllRestaurants() {

        return restaurantRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // FILTER BY APPROVAL STATUS
    // ==========================================

    @Transactional(readOnly = true)
    public List<SuperAdminRestaurantResponse>
            getRestaurantsByStatus(
                    RestaurantApprovalStatus status
            ) {

        return restaurantRepository
                .findByApprovalStatusOrderByCreatedAtDesc(
                        status
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // GET ONE RESTAURANT
    // ==========================================

    @Transactional(readOnly = true)
    public SuperAdminRestaurantResponse
            getRestaurant(
                    Long restaurantId
            ) {

        Restaurant restaurant =
                findRestaurant(
                        restaurantId
                );

        return toResponse(
                restaurant
        );
    }

    // ==========================================
    // UPDATE APPROVAL STATUS
    // ==========================================

    @Transactional
    public SuperAdminRestaurantResponse
            updateApprovalStatus(
                    Long restaurantId,
                    SuperAdminRestaurantStatusRequest request
            ) {

        Restaurant restaurant =
                findRestaurant(
                        restaurantId
                );

        RestaurantApprovalStatus oldStatus =
                restaurant.getApprovalStatus();

        RestaurantApprovalStatus newStatus =
                request.approvalStatus();

        if (newStatus == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Restaurant approval status is required"
            );
        }

        /*
         * FIRST APPROVAL means:
         *
         * PENDING  -> APPROVED
         * REJECTED -> APPROVED
         *
         * But:
         *
         * APPROVED -> APPROVED
         *
         * must NOT regenerate credentials.
         */

        boolean firstApproval =
                oldStatus
                        != RestaurantApprovalStatus.APPROVED
                &&
                newStatus
                        == RestaurantApprovalStatus.APPROVED;

        restaurant.setApprovalStatus(
                newStatus
        );

        /*
         * APPROVED:
         * Restaurant becomes active.
         *
         * PENDING / REJECTED:
         * Restaurant becomes inactive.
         */

        boolean shouldBeActive =
                newStatus
                        == RestaurantApprovalStatus.APPROVED;

        restaurant.setActive(
                shouldBeActive
        );

        Restaurant savedRestaurant =
                restaurantRepository.save(
                        restaurant
                );

        List<RestaurantAdmin> admins =
                restaurantAdminRepository
                        .findByRestaurantIdOrderByCreatedAtDesc(
                                restaurantId
                        );

        /*
         * Self-registration creates the owner
         * RestaurantAdmin before approval.
         *
         * Therefore an approval request should
         * normally always have at least one admin.
         */

        if (
                firstApproval
                && admins.isEmpty()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Restaurant owner admin account was not found"
            );
        }

        /*
         * Temporary credentials are generated only
         * for the primary owner admin.
         *
         * The self-registration flow creates the
         * owner as the first RestaurantAdmin.
         */

        RestaurantAdmin ownerAdmin =
                admins.isEmpty()
                        ? null
                        : admins.get(
                                admins.size() - 1
                        );

        String temporaryPassword =
                null;

        for (
                RestaurantAdmin admin : admins
        ) {

            admin.setActive(
                    shouldBeActive
            );

            /*
             * Only FIRST APPROVAL creates new
             * temporary credentials.
             *
             * Other linked admins are simply
             * activated.
             */

            if (
                    firstApproval
                    && admin.getId()
                            .equals(
                                    ownerAdmin.getId()
                            )
            ) {

                temporaryPassword =
                        generateTemporaryPassword();

                admin.setPasswordHash(
                        passwordEncoder.encode(
                                temporaryPassword
                        )
                );

                admin.setMustChangePassword(
                        true
                );
            }
        }

        if (!admins.isEmpty()) {

            restaurantAdminRepository
                    .saveAll(
                            admins
                    );
        }

        /*
         * Send credentials only after the owner
         * credentials have been generated.
         */

        if (
                firstApproval
                && ownerAdmin != null
                && temporaryPassword != null
        ) {

            emailService
                    .sendRestaurantApprovalEmail(
                            ownerAdmin.getEmail(),
                            ownerAdmin.getFullName(),
                            savedRestaurant.getName(),
                            temporaryPassword
                    );
        }

        return toResponse(
                savedRestaurant
        );
    }

    // ==========================================
    // ACTIVATE / DEACTIVATE RESTAURANT
    // ==========================================

    @Transactional
    public SuperAdminRestaurantResponse
            updateActiveStatus(
                    Long restaurantId,
                    SuperAdminRestaurantActiveRequest request
            ) {

        Restaurant restaurant =
                findRestaurant(
                        restaurantId
                );

        /*
         * Only APPROVED restaurants are
         * allowed to become active.
         */

        if (
                request.active()
                &&
                restaurant.getApprovalStatus()
                        != RestaurantApprovalStatus.APPROVED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only approved restaurants can be activated"
            );
        }

        restaurant.setActive(
                request.active()
        );

        Restaurant savedRestaurant =
                restaurantRepository.save(
                        restaurant
                );

        /*
         * Restaurant deactivation also disables
         * all Restaurant Admin accounts.
         *
         * Manual restaurant reactivation does NOT
         * automatically reactivate individual admins.
         */

        if (!request.active()) {

            List<RestaurantAdmin> admins =
                    restaurantAdminRepository
                            .findByRestaurantIdOrderByCreatedAtDesc(
                                    restaurantId
                            );

            for (
                    RestaurantAdmin admin : admins
            ) {

                admin.setActive(
                        false
                );
            }

            if (!admins.isEmpty()) {

                restaurantAdminRepository
                        .saveAll(
                                admins
                        );
            }
        }

        return toResponse(
                savedRestaurant
        );
    }

    // ==========================================
    // UPDATE COMMISSION
    // ==========================================

    @Transactional
    public SuperAdminRestaurantResponse
            updateCommission(
                    Long restaurantId,
                    SuperAdminCommissionRequest request
            ) {

        Restaurant restaurant =
                findRestaurant(
                        restaurantId
                );

        BigDecimal commission =
                request.commissionPercentage();

        restaurant.setCommissionPercentage(
                commission
        );

        Restaurant savedRestaurant =
                restaurantRepository.save(
                        restaurant
                );

        return toResponse(
                savedRestaurant
        );
    }

    // ==========================================
    // GENERATE TEMPORARY PASSWORD
    // ==========================================

    private String generateTemporaryPassword() {

        StringBuilder password =
                new StringBuilder(
                        TEMP_PASSWORD_LENGTH
                );

        for (
                int i = 0;
                i < TEMP_PASSWORD_LENGTH;
                i++
        ) {

            int index =
                    secureRandom.nextInt(
                            TEMP_PASSWORD_CHARACTERS.length()
                    );

            password.append(
                    TEMP_PASSWORD_CHARACTERS.charAt(
                            index
                    )
            );
        }

        return password.toString();
    }

    // ==========================================
    // FIND RESTAURANT
    // ==========================================

    private Restaurant findRestaurant(
            Long restaurantId
    ) {

        return restaurantRepository
                .findById(
                        restaurantId
                )
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Restaurant not found with id: "
                                                + restaurantId
                                )
                );
    }

    // ==========================================
    // ENTITY -> DTO
    // ==========================================

    private SuperAdminRestaurantResponse
            toResponse(
                    Restaurant restaurant
            ) {

        long adminCount =
                restaurantAdminRepository
                        .countByRestaurantId(
                                restaurant.getId()
                        );

        return new SuperAdminRestaurantResponse(

                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getEmail(),
                restaurant.getPhone(),
                restaurant.getAddress(),
                restaurant.getCity(),
                restaurant.getState(),
                restaurant.getLogoUrl(),

                restaurant.getApprovalStatus(),
                restaurant.isActive(),

                restaurant.getCommissionPercentage(),

                adminCount,

                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
        );
    }
}