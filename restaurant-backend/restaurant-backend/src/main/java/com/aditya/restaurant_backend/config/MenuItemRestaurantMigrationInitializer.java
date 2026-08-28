package com.aditya.restaurant_backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.MenuItemRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Component
public class MenuItemRestaurantMigrationInitializer
        implements CommandLineRunner {

    private final MenuItemRepository
            menuItemRepository;

    private final RestaurantRepository
            restaurantRepository;

    public MenuItemRestaurantMigrationInitializer(
            MenuItemRepository menuItemRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.menuItemRepository =
                menuItemRepository;

        this.restaurantRepository =
                restaurantRepository;
    }

    @Override
    public void run(String... args) {

        Restaurant spiceRoute =
                restaurantRepository
                        .findByEmailIgnoreCase(
                                "spiceroute@restaurant.local"
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "SpiceRoute restaurant not found"
                                )
                        );

        List<MenuItem> menuItems =
                menuItemRepository.findAll();

        boolean changed = false;

        for (MenuItem menuItem : menuItems) {

            if (menuItem.getRestaurant() == null) {
                menuItem.setRestaurant(
                        spiceRoute
                );

                changed = true;
            }
        }

        if (changed) {
            menuItemRepository
                    .saveAll(menuItems);
        }
    }
}