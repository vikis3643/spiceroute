package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RestaurantProfileUpdateRequest {

    @NotBlank(
            message = "Restaurant name is required"
    )
    @Size(
            max = 150,
            message = "Restaurant name cannot exceed 150 characters"
    )
    private String name;

    @Size(
            max = 1000,
            message = "Description cannot exceed 1000 characters"
    )
    private String description;

    @Email(
            message = "Invalid email address"
    )
    @NotBlank(
            message = "Email is required"
    )
    private String email;

    @Size(
            max = 20,
            message = "Phone cannot exceed 20 characters"
    )
    private String phone;

    @Size(
            max = 1000,
            message = "Address cannot exceed 1000 characters"
    )
    private String address;

    @Size(
            max = 100,
            message = "City cannot exceed 100 characters"
    )
    private String city;

    @Size(
            max = 100,
            message = "State cannot exceed 100 characters"
    )
    private String state;

    @Size(
            max = 1000,
            message = "Logo URL cannot exceed 1000 characters"
    )
    private String logoUrl;

    public RestaurantProfileUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(
            String address
    ) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(
            String city
    ) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(
            String state
    ) {
        this.state = state;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(
            String logoUrl
    ) {
        this.logoUrl = logoUrl;
    }
}