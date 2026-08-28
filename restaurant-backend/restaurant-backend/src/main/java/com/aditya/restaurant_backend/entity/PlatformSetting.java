package com.aditya.restaurant_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "platform_settings")
public class PlatformSetting {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            name = "platform_name",
            nullable = false,
            length = 100
    )
    private String platformName;

    @Column(
            name = "support_email",
            length = 150
    )
    private String supportEmail;

    @Column(
            name = "support_phone",
            length = 20
    )
    private String supportPhone;

    @Column(
            name = "default_commission_percentage",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal defaultCommissionPercentage =
            BigDecimal.ZERO;

    @Column(
            name = "default_delivery_fee",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal defaultDeliveryFee =
            BigDecimal.ZERO;

    @Column(
            name = "minimum_order_amount",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal minimumOrderAmount =
            BigDecimal.ZERO;

    @Column(
            name = "maintenance_mode",
            nullable = false
    )
    private boolean maintenanceMode =
            false;

    @Column(
            name = "restaurant_registration_enabled",
            nullable = false
    )
    private boolean restaurantRegistrationEnabled =
            true;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public PlatformSetting() {
    }

    @PrePersist
    public void prePersist() {

        LocalDateTime now =
                LocalDateTime.now();

        if (defaultCommissionPercentage == null) {
            defaultCommissionPercentage =
                    BigDecimal.ZERO;
        }

        if (defaultDeliveryFee == null) {
            defaultDeliveryFee =
                    BigDecimal.ZERO;
        }

        if (minimumOrderAmount == null) {
            minimumOrderAmount =
                    BigDecimal.ZERO;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt =
                LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(
            String platformName
    ) {
        this.platformName =
                platformName;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(
            String supportEmail
    ) {
        this.supportEmail =
                supportEmail;
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public void setSupportPhone(
            String supportPhone
    ) {
        this.supportPhone =
                supportPhone;
    }

    public BigDecimal
            getDefaultCommissionPercentage() {
        return defaultCommissionPercentage;
    }

    public void setDefaultCommissionPercentage(
            BigDecimal defaultCommissionPercentage
    ) {
        this.defaultCommissionPercentage =
                defaultCommissionPercentage;
    }

    public BigDecimal getDefaultDeliveryFee() {
        return defaultDeliveryFee;
    }

    public void setDefaultDeliveryFee(
            BigDecimal defaultDeliveryFee
    ) {
        this.defaultDeliveryFee =
                defaultDeliveryFee;
    }

    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(
            BigDecimal minimumOrderAmount
    ) {
        this.minimumOrderAmount =
                minimumOrderAmount;
    }

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public void setMaintenanceMode(
            boolean maintenanceMode
    ) {
        this.maintenanceMode =
                maintenanceMode;
    }

    public boolean
            isRestaurantRegistrationEnabled() {
        return restaurantRegistrationEnabled;
    }

    public void setRestaurantRegistrationEnabled(
            boolean restaurantRegistrationEnabled
    ) {
        this.restaurantRegistrationEnabled =
                restaurantRegistrationEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
