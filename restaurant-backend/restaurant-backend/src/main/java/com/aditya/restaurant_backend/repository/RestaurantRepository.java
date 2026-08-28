package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;

public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByEmailIgnoreCase(
            String email
    );

    boolean existsByEmailIgnoreCase(
            String email
    );

    List<Restaurant>
            findByApprovalStatusOrderByCreatedAtDesc(
                    RestaurantApprovalStatus approvalStatus
            );

    List<Restaurant>
            findByActiveTrueAndApprovalStatusOrderByNameAsc(
                    RestaurantApprovalStatus approvalStatus
            );

    // ==========================================
    // SUPER ADMIN DASHBOARD
    // ==========================================

    long countByApprovalStatus(
            RestaurantApprovalStatus approvalStatus
    );

    long countByActiveTrue();
}