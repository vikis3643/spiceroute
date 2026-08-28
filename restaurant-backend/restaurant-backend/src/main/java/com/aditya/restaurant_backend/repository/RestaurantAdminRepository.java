package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.RestaurantAdmin;

public interface RestaurantAdminRepository
        extends JpaRepository<RestaurantAdmin, Long> {

    Optional<RestaurantAdmin>
            findByEmailIgnoreCase(
                    String email
            );

    boolean existsByEmailIgnoreCase(
            String email
    );

    List<RestaurantAdmin>
            findByRestaurantIdOrderByCreatedAtDesc(
                    Long restaurantId
            );

    List<RestaurantAdmin>
            findByRestaurantIdAndActiveTrueOrderByCreatedAtDesc(
                    Long restaurantId
            );

    long countByRestaurantId(
            Long restaurantId
    );
}