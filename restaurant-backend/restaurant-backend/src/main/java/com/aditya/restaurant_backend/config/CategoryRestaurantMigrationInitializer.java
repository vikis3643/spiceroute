package com.aditya.restaurant_backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aditya.restaurant_backend.entity.Category;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.CategoryRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Component
public class CategoryRestaurantMigrationInitializer
        implements CommandLineRunner {

    private final CategoryRepository
            categoryRepository;

    private final RestaurantRepository
            restaurantRepository;

    public CategoryRestaurantMigrationInitializer(
            CategoryRepository categoryRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.categoryRepository =
                categoryRepository;

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

        List<Category> categories =
                categoryRepository.findAll();

        boolean changed = false;

        for (Category category : categories) {

            if (category.getRestaurant() == null) {
                category.setRestaurant(
                        spiceRoute
                );

                changed = true;
            }
        }

        if (changed) {
            categoryRepository
                    .saveAll(categories);
        }
    }
}