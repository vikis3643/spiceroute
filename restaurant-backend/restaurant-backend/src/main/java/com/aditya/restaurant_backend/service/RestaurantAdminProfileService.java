package com.aditya.restaurant_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.RestaurantProfileUpdateRequest;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class RestaurantAdminProfileService {

    private final RestaurantRepository
            restaurantRepository;

    public RestaurantAdminProfileService(
            RestaurantRepository restaurantRepository
    ) {
        this.restaurantRepository =
                restaurantRepository;
    }

    public Restaurant getProfile(
            Long restaurantId
    ) {
        return getRestaurant(
                restaurantId
        );
    }

    @Transactional
    public Restaurant updateProfile(
            Long restaurantId,
            RestaurantProfileUpdateRequest request
    ) {

        Restaurant restaurant =
                getRestaurant(
                        restaurantId
                );

        restaurant.setName(
                request.getName()
                        .trim()
        );

        restaurant.setDescription(
                normalizeNullable(
                        request.getDescription()
                )
        );

        restaurant.setEmail(
                request.getEmail()
                        .trim()
        );

        restaurant.setPhone(
                normalizeNullable(
                        request.getPhone()
                )
        );

        restaurant.setAddress(
                normalizeNullable(
                        request.getAddress()
                )
        );

        restaurant.setCity(
                normalizeNullable(
                        request.getCity()
                )
        );

        restaurant.setState(
                normalizeNullable(
                        request.getState()
                )
        );

        restaurant.setLogoUrl(
                normalizeNullable(
                        request.getLogoUrl()
                )
        );

        return restaurantRepository.save(
                restaurant
        );
    }

    private Restaurant getRestaurant(
            Long restaurantId
    ) {
        return restaurantRepository
                .findById(
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Restaurant not found"
                        )
                );
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String trimmed =
                value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }
}