package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;

public interface MenuItemRepository
        extends JpaRepository<MenuItem, Long> {

    // ==========================================
    // EXISTING METHODS
    // ==========================================

    List<MenuItem> findByCategoryId(
            Long categoryId
    );

    List<MenuItem> findByAvailableTrue();

    List<MenuItem> findByNameContainingIgnoreCase(
            String name
    );

    // ==========================================
    // MULTI-RESTAURANT ADMIN METHODS
    // ==========================================

    List<MenuItem>
            findByRestaurantIdOrderByNameAsc(
                    Long restaurantId
            );

    List<MenuItem>
            findByRestaurantIdAndAvailableTrueOrderByNameAsc(
                    Long restaurantId
            );

    List<MenuItem>
            findByRestaurantIdAndCategoryIdOrderByNameAsc(
                    Long restaurantId,
                    Long categoryId
            );

    Optional<MenuItem>
            findByIdAndRestaurantId(
                    Long menuItemId,
                    Long restaurantId
            );

    List<MenuItem>
            findByRestaurantIdAndNameContainingIgnoreCaseOrderByNameAsc(
                    Long restaurantId,
                    String name
            );

    // ==========================================
    // CUSTOMER MARKETPLACE
    // ==========================================

    List<MenuItem>
            findByAvailableTrueAndRestaurantActiveTrueAndRestaurantApprovalStatusOrderByNameAsc(
                    RestaurantApprovalStatus approvalStatus
            );
}