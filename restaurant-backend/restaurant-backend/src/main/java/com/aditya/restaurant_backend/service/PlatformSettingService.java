package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aditya.restaurant_backend.dto.PlatformSettingResponse;
import com.aditya.restaurant_backend.dto.UpdatePlatformSettingRequest;
import com.aditya.restaurant_backend.entity.PlatformSetting;
import com.aditya.restaurant_backend.repository.PlatformSettingRepository;

@Service
public class PlatformSettingService {

    private final PlatformSettingRepository
            platformSettingRepository;

    public PlatformSettingService(
            PlatformSettingRepository platformSettingRepository
    ) {
        this.platformSettingRepository =
                platformSettingRepository;
    }

    // ==========================================
    // GET CURRENT PLATFORM SETTINGS
    // ==========================================

    @Transactional
    public PlatformSettingResponse
            getSettings() {

        PlatformSetting setting =
                getOrCreateSetting();

        return toResponse(
                setting
        );
    }

    // ==========================================
    // UPDATE PLATFORM SETTINGS
    // ==========================================

    @Transactional
    public PlatformSettingResponse
            updateSettings(
                    UpdatePlatformSettingRequest request
            ) {

        PlatformSetting setting =
                getOrCreateSetting();

        setting.setPlatformName(
                request.platformName()
                        .trim()
        );

        setting.setSupportEmail(
                trimToNull(
                        request.supportEmail()
                )
        );

        setting.setSupportPhone(
                trimToNull(
                        request.supportPhone()
                )
        );

        setting.setDefaultCommissionPercentage(
                request.defaultCommissionPercentage()
        );

        setting.setDefaultDeliveryFee(
                request.defaultDeliveryFee()
        );

        setting.setMinimumOrderAmount(
                request.minimumOrderAmount()
        );

        setting.setMaintenanceMode(
                request.maintenanceMode()
        );

        setting.setRestaurantRegistrationEnabled(
                request.restaurantRegistrationEnabled()
        );

        PlatformSetting savedSetting =
                platformSettingRepository.save(
                        setting
                );

        return toResponse(
                savedSetting
        );
    }

    // ==========================================
    // GET OR CREATE SINGLE SETTINGS ROW
    // ==========================================

    private PlatformSetting
            getOrCreateSetting() {

        return platformSettingRepository
                .findTopByOrderByIdAsc()
                .orElseGet(() -> {

                    PlatformSetting setting =
                            new PlatformSetting();

                    setting.setPlatformName(
                            "SpiceRoute"
                    );

                    setting.setSupportEmail(
                            null
                    );

                    setting.setSupportPhone(
                            null
                    );

                    setting.setDefaultCommissionPercentage(
                            BigDecimal.ZERO
                    );

                    setting.setDefaultDeliveryFee(
                            new BigDecimal("40.00")
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
                });
    }

    // ==========================================
    // ENTITY -> DTO
    // ==========================================

    private PlatformSettingResponse
            toResponse(
                    PlatformSetting setting
            ) {

        return new PlatformSettingResponse(

                setting.getId(),

                setting.getPlatformName(),

                setting.getSupportEmail(),
                setting.getSupportPhone(),

                setting.getDefaultCommissionPercentage(),
                setting.getDefaultDeliveryFee(),
                setting.getMinimumOrderAmount(),

                setting.isMaintenanceMode(),
                setting.isRestaurantRegistrationEnabled(),

                setting.getCreatedAt(),
                setting.getUpdatedAt()
        );
    }

    // ==========================================
    // HELPERS
    // ==========================================

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