package com.aditya.restaurant_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantAdmin;
import com.aditya.restaurant_backend.repository.RestaurantAdminRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Component
public class RestaurantAdminDataInitializer
        implements CommandLineRunner {

    private final RestaurantRepository
            restaurantRepository;

    private final RestaurantAdminRepository
            restaurantAdminRepository;

    private final PasswordEncoder
            passwordEncoder;

    public RestaurantAdminDataInitializer(
            RestaurantRepository restaurantRepository,
            RestaurantAdminRepository restaurantAdminRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.restaurantRepository =
                restaurantRepository;

        this.restaurantAdminRepository =
                restaurantAdminRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    @Override
    public void run(String... args) {

        String adminEmail =
                "spiceroute.admin@restaurant.local";

        boolean adminExists =
                restaurantAdminRepository
                        .existsByEmailIgnoreCase(
                                adminEmail
                        );

        if (adminExists) {
            return;
        }

        Restaurant restaurant =
                restaurantRepository
                        .findByEmailIgnoreCase(
                                "spiceroute@restaurant.local"
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "SpiceRoute restaurant not found"
                                )
                        );

        RestaurantAdmin admin =
                new RestaurantAdmin();

        admin.setRestaurant(
                restaurant
        );

        admin.setFullName(
                "SpiceRoute Admin"
        );

        admin.setEmail(
                adminEmail
        );

        admin.setPasswordHash(
                passwordEncoder.encode(
                        "Admin@123"
                )
        );

        admin.setActive(true);

        restaurantAdminRepository
                .save(admin);
    }
}