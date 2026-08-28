package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerLoginRequest {

    @NotBlank(
            message = "Email or phone number is required"
    )
    @Size(
            max = 150,
            message = "Email or phone number is too long"
    )
    private String identifier;

    @NotBlank(
            message = "Password is required"
    )
    private String password;

    public CustomerLoginRequest() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(
            String identifier
    ) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }
}