package com.aditya.restaurant_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_accounts")
public class CustomerAccount {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            name = "full_name",
            nullable = false,
            length = 100
    )
    private String fullName;

    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;

    @Column(
            name = "password_hash",
            length = 100
    )
    private String passwordHash;

    @Column(
            unique = true,
            length = 15
    )
    private String phone;

    @Column(
            name = "default_delivery_address",
            length = 1000
    )
    private String defaultDeliveryAddress;

    @Column(
            nullable = false,
            length = 20
    )
    private String provider = "LOCAL";

    @Column(
            name = "google_subject",
            unique = true,
            length = 255
    )
    private String googleSubject;

    @Column(
            name = "email_verified",
            nullable = false
    )
    private boolean emailVerified = false;

    @Column(nullable = false)
    private boolean active = true;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public CustomerAccount() {
    }

    @PrePersist
    public void beforeSave() {
        if (createdAt == null) {
            createdAt =
                    LocalDateTime.now();
        }

        if (provider == null
                || provider.isBlank()) {

            provider = "LOCAL";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(
            String fullName
    ) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(
            String passwordHash
    ) {
        this.passwordHash =
                passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone = phone;
    }

    public String
            getDefaultDeliveryAddress() {

        return defaultDeliveryAddress;
    }

    public void setDefaultDeliveryAddress(
            String defaultDeliveryAddress
    ) {
        this.defaultDeliveryAddress =
                defaultDeliveryAddress;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(
            String provider
    ) {
        this.provider = provider;
    }

    public String getGoogleSubject() {
        return googleSubject;
    }

    public void setGoogleSubject(
            String googleSubject
    ) {
        this.googleSubject =
                googleSubject;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(
            boolean emailVerified
    ) {
        this.emailVerified =
                emailVerified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active
    ) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}