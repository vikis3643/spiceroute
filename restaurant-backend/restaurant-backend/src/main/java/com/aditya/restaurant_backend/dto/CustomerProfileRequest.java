package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Full name must contain 2 to 100 characters"
    )
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;

    @NotBlank(
            message = "Default delivery address is required"
    )
    @Size(
            min = 10,
            max = 1000,
            message = "Address must contain 10 to 1000 characters"
    )
    private String defaultDeliveryAddress;

    public CustomerProfileRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(
            String fullName
    ) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDefaultDeliveryAddress() {
        return defaultDeliveryAddress;
    }

    public void setDefaultDeliveryAddress(
            String defaultDeliveryAddress
    ) {
        this.defaultDeliveryAddress =
                defaultDeliveryAddress;
    }
}