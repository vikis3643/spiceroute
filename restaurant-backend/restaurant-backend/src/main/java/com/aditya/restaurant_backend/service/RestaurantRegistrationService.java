package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.security.SecureRandom;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.RestaurantRegistrationRequest;
import com.aditya.restaurant_backend.dto.RestaurantRegistrationResponse;
import com.aditya.restaurant_backend.dto.RestaurantRegistrationStatusResponse;
import com.aditya.restaurant_backend.entity.PlatformSetting;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantAdmin;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;
import com.aditya.restaurant_backend.repository.PlatformSettingRepository;
import com.aditya.restaurant_backend.repository.RestaurantAdminRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class RestaurantRegistrationService {

    private static final String PLACEHOLDER_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "abcdefghijklmnopqrstuvwxyz"
                    + "0123456789"
                    + "@#$!";

    private static final int PLACEHOLDER_LENGTH =
            32;

    private final SecureRandom secureRandom =
            new SecureRandom();

    private final RestaurantRepository
            restaurantRepository;

    private final RestaurantAdminRepository
            restaurantAdminRepository;

    private final PlatformSettingRepository
            platformSettingRepository;

    private final PasswordEncoder
            passwordEncoder;

    public RestaurantRegistrationService(
            RestaurantRepository restaurantRepository,
            RestaurantAdminRepository restaurantAdminRepository,
            PlatformSettingRepository platformSettingRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.restaurantRepository =
                restaurantRepository;

        this.restaurantAdminRepository =
                restaurantAdminRepository;

        this.platformSettingRepository =
                platformSettingRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    // ==========================================
    // REGISTER RESTAURANT
    // ==========================================

    @Transactional
    public RestaurantRegistrationResponse
            register(
                    RestaurantRegistrationRequest request
            ) {

        // ======================================
        // PLATFORM SETTINGS
        // ======================================

        PlatformSetting platformSetting =
                getOrCreatePlatformSetting();

        if (
                !platformSetting
                        .isRestaurantRegistrationEnabled()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Restaurant registration is currently disabled"
            );
        }

        String restaurantEmail =
                normalizeEmail(
                        request.restaurantEmail()
                );

        String ownerEmail =
                normalizeEmail(
                        request.ownerEmail()
                );

        // ======================================
        // RESTAURANT EMAIL DUPLICATE CHECK
        // ======================================

        if (
                restaurantRepository
                        .existsByEmailIgnoreCase(
                                restaurantEmail
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A restaurant with this email already exists"
            );
        }

        // ======================================
        // OWNER / ADMIN EMAIL DUPLICATE CHECK
        // ======================================

        if (
                restaurantAdminRepository
                        .existsByEmailIgnoreCase(
                                ownerEmail
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A restaurant admin with this email already exists"
            );
        }

        // ======================================
        // CREATE RESTAURANT
        // ======================================

        Restaurant restaurant =
                new Restaurant();

        restaurant.setName(
                request.restaurantName()
                        .trim()
        );

        restaurant.setDescription(
                trimToNull(
                        request.description()
                )
        );

        restaurant.setEmail(
                restaurantEmail
        );

        restaurant.setPhone(
                request.restaurantPhone()
                        .trim()
        );

        restaurant.setAddress(
                request.address()
                        .trim()
        );

        restaurant.setCity(
                request.city()
                        .trim()
        );

        restaurant.setState(
                request.state()
                        .trim()
        );

        restaurant.setLogoUrl(
                trimToNull(
                        request.logoUrl()
                )
        );

        // New restaurant always starts pending.
        restaurant.setApprovalStatus(
                RestaurantApprovalStatus.PENDING
        );

        // Pending restaurants must not appear
        // on the customer marketplace.
        restaurant.setActive(
                false
        );

        /*
         * New restaurants automatically receive
         * the platform default commission.
         */

        restaurant.setCommissionPercentage(
                platformSetting
                        .getDefaultCommissionPercentage()
        );

        Restaurant savedRestaurant =
                restaurantRepository.save(
                        restaurant
                );

        // ======================================
        // CREATE OWNER RESTAURANT ADMIN
        // ======================================

        RestaurantAdmin owner =
                new RestaurantAdmin();

        owner.setRestaurant(
                savedRestaurant
        );

        owner.setFullName(
                request.ownerName()
                        .trim()
        );

        owner.setEmail(
                ownerEmail
        );

        /*
         * Owner does NOT choose an admin password
         * during restaurant registration.
         *
         * passwordHash is NOT NULL in the database,
         * therefore an internal random unusable
         * placeholder is stored temporarily.
         *
         * On Super Admin approval this hash will be
         * replaced with the BCrypt hash of the real
         * temporary password emailed to the owner.
         */

        String placeholderPassword =
                generatePlaceholderPassword();

        owner.setPasswordHash(
                passwordEncoder.encode(
                        placeholderPassword
                )
        );

        /*
         * Pending application:
         *
         * Owner cannot login.
         */

        owner.setActive(
                false
        );

        /*
         * This is NOT yet the temporary password
         * issued after approval.
         */

        owner.setMustChangePassword(
                false
        );

        RestaurantAdmin savedOwner =
                restaurantAdminRepository.save(
                        owner
                );

        // ======================================
        // RESPONSE
        // ======================================

        return new RestaurantRegistrationResponse(

                savedRestaurant.getId(),
                savedRestaurant.getName(),
                savedRestaurant.getEmail(),

                savedRestaurant
                        .getApprovalStatus(),

                savedRestaurant
                        .isActive(),

                savedOwner.getId(),
                savedOwner.getFullName(),
                savedOwner.getEmail(),
                savedOwner.isActive(),

                savedRestaurant
                        .getCreatedAt(),

                "Restaurant registration submitted successfully. "
                        + "Your application is pending Super Admin approval."
        );
    }

    // ==========================================
    // GET REGISTRATION STATUS
    // ==========================================

    @Transactional(readOnly = true)
    public RestaurantRegistrationStatusResponse
            getRegistrationStatus(
                    String ownerEmail
            ) {

        String normalizedEmail =
                normalizeEmail(
                        ownerEmail
                );

        RestaurantAdmin owner =
                restaurantAdminRepository
                        .findByEmailIgnoreCase(
                                normalizedEmail
                        )
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Restaurant registration not found"
                                        )
                        );

        Restaurant restaurant =
                owner.getRestaurant();

        return new RestaurantRegistrationStatusResponse(

                restaurant.getId(),
                restaurant.getName(),

                restaurant
                        .getApprovalStatus(),

                restaurant
                        .isActive(),

                owner.isActive(),

                restaurant
                        .getCreatedAt()
        );
    }

    // ==========================================
    // GENERATE INTERNAL PLACEHOLDER PASSWORD
    // ==========================================

    private String generatePlaceholderPassword() {

        StringBuilder password =
                new StringBuilder(
                        PLACEHOLDER_LENGTH
                );

        for (
                int i = 0;
                i < PLACEHOLDER_LENGTH;
                i++
        ) {

            int index =
                    secureRandom.nextInt(
                            PLACEHOLDER_CHARACTERS.length()
                    );

            password.append(
                    PLACEHOLDER_CHARACTERS.charAt(
                            index
                    )
            );
        }

        return password.toString();
    }

    // ==========================================
    // PLATFORM SETTINGS
    // ==========================================

    private PlatformSetting
            getOrCreatePlatformSetting() {

        return platformSettingRepository
                .findTopByOrderByIdAsc()
                .orElseGet(
                        () -> {

                            PlatformSetting setting =
                                    new PlatformSetting();

                            setting.setPlatformName(
                                    "SpiceRoute"
                            );

                            setting.setDefaultCommissionPercentage(
                                    BigDecimal.ZERO
                            );

                            setting.setDefaultDeliveryFee(
                                    new BigDecimal(
                                            "40.00"
                                    )
                            );

                            setting.setMinimumOrderAmount(
                                    BigDecimal.ZERO
                            );

                            setting.setMaintenanceMode(
                                    false
                            );

                            setting.setRestaurantRegistrationEnabled(
                                    true
                            );

                            return platformSettingRepository
                                    .save(
                                            setting
                                    );
                        }
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

    private String trimToNull(
            String value
    ) {

        if (
                value == null
                || value.isBlank()
        ) {

            return null;
        }

        return value.trim();
    }
}