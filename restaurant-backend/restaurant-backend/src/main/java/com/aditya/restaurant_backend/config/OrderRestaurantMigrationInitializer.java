package com.aditya.restaurant_backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.OrderRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Component
public class OrderRestaurantMigrationInitializer
        implements CommandLineRunner {

    private final OrderRepository orderRepository;

    private final RestaurantRepository
            restaurantRepository;

    public OrderRestaurantMigrationInitializer(
            OrderRepository orderRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.orderRepository =
                orderRepository;

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

        List<CustomerOrder> orders =
                orderRepository.findAll();

        boolean changed = false;

        for (CustomerOrder order : orders) {

            if (order.getRestaurant() == null) {

                order.setRestaurant(
                        spiceRoute
                );

                changed = true;
            }
        }

        if (changed) {
            orderRepository.saveAll(
                    orders
            );
        }
    }
}