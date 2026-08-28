package com.aditya.restaurant_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(length = 1000)
    private String address;

    private String city;

    private String state;

    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantApprovalStatus approvalStatus;

    @Column(nullable = false)
    private boolean active;

    @Column(
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal commissionPercentage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now =
                LocalDateTime.now();

        if (approvalStatus == null) {
            approvalStatus =
                    RestaurantApprovalStatus.PENDING;
        }

        if (commissionPercentage == null) {
            commissionPercentage =
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description =
                description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email =
                email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone =
                phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(
            String address
    ) {
        this.address =
                address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(
            String city
    ) {
        this.city =
                city;
    }

    public String getState() {
        return state;
    }

    public void setState(
            String state
    ) {
        this.state =
                state;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(
            String logoUrl
    ) {
        this.logoUrl =
                logoUrl;
    }

    public RestaurantApprovalStatus
            getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(
            RestaurantApprovalStatus approvalStatus
    ) {
        this.approvalStatus =
                approvalStatus;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active
    ) {
        this.active =
                active;
    }

    public BigDecimal
            getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(
            BigDecimal commissionPercentage
    ) {
        this.commissionPercentage =
                commissionPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}