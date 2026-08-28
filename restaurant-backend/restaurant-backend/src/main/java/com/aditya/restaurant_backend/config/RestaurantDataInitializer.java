package com.aditya.restaurant_backend.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Component
public class RestaurantDataInitializer
        implements CommandLineRunner {

    private final RestaurantRepository
            restaurantRepository;

    public RestaurantDataInitializer(
            RestaurantRepository restaurantRepository
    ) {
        this.restaurantRepository =
                restaurantRepository;
    }

    @Override
    public void run(String... args) {

        boolean spiceRouteExists =
                restaurantRepository
                        .existsByEmailIgnoreCase(
                                "spiceroute@restaurant.local"
                        );

        if (spiceRouteExists) {
            return;
        }

        Restaurant restaurant =
                new Restaurant();

        restaurant.setName("SpiceRoute");

        restaurant.setDescription(
                "Original SpiceRoute restaurant"
        );

        restaurant.setEmail(
                "spiceroute@restaurant.local"
        );

        restaurant.setPhone("");

        restaurant.setAddress("");

        restaurant.setCity("");

        restaurant.setState("");

        restaurant.setLogoUrl("");

        restaurant.setApprovalStatus(
                RestaurantApprovalStatus.APPROVED
        );

        restaurant.setActive(true);

        restaurant.setCommissionPercentage(
                BigDecimal.ZERO
        );

        restaurantRepository.save(restaurant);
    }
}