package com.aditya.restaurant_backend.dto;

public class CustomerProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String defaultDeliveryAddress;
    private String provider;
    private boolean emailVerified;

    public CustomerProfileResponse(
            Long id,
            String fullName,
            String email,
            String phone,
            String defaultDeliveryAddress,
            String provider,
            boolean emailVerified
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.defaultDeliveryAddress =
                defaultDeliveryAddress;

        this.provider = provider;
        this.emailVerified = emailVerified;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDefaultDeliveryAddress() {
        return defaultDeliveryAddress;
    }

    public String getProvider() {
        return provider;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}
