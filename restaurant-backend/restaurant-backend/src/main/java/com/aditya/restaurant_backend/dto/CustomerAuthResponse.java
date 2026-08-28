package com.aditya.restaurant_backend.dto;

public class CustomerAuthResponse {

    private String token;
    private String tokenType;
    private Long customerId;
    private String fullName;
    private String email;
    private String role;

    public CustomerAuthResponse(
            String token,
            String tokenType,
            Long customerId,
            String fullName,
            String email,
            String role
    ) {
        this.token = token;
        this.tokenType = tokenType;
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}