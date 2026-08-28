package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerRegisterRequest {

    @NotBlank(
            message = "Full name is required"
    )
    @Size(
            min = 2,
            max = 100,
            message = "Full name must contain 2 to 100 characters"
    )
    private String fullName;

    @NotBlank(
            message = "Email is required"
    )
    @Email(
            message = "Please enter a valid email address"
    )
    @Size(
            max = 150,
            message = "Email cannot exceed 150 characters"
    )
    private String email;

    @NotBlank(
            message = "Phone number is required"
    )
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;

    @NotBlank(
            message = "Password is required"
    )
    @Size(
            min = 8,
            max = 72,
            message = "Password must contain 8 to 72 characters"
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must include uppercase, lowercase and a number"
    )
    private String password;

    @NotBlank(
            message = "Please confirm your password"
    )
    private String confirmPassword;

    public CustomerRegisterRequest() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(
            String confirmPassword
    ) {
        this.confirmPassword =
                confirmPassword;
    }
}