package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.WishlistItem;

public interface WishlistRepository
        extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem>
            findByCustomerAccountIdOrderByCreatedAtDesc(
                    Long customerId
            );

    Optional<WishlistItem>
            findByCustomerAccountIdAndMenuItemId(
                    Long customerId,
                    Long menuItemId
            );

    boolean existsByCustomerAccountIdAndMenuItemId(
            Long customerId,
            Long menuItemId
    );

    void deleteByCustomerAccountIdAndMenuItemId(
            Long customerId,
            Long menuItemId
    );
}