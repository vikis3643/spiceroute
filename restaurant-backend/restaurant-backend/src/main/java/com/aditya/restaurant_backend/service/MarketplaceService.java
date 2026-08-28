package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aditya.restaurant_backend.dto.MarketplaceMenuItemResponse;
import com.aditya.restaurant_backend.entity.Category;
import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;
import com.aditya.restaurant_backend.repository.MenuItemRepository;

@Service
public class MarketplaceService {

    private final MenuItemRepository
            menuItemRepository;

    public MarketplaceService(
            MenuItemRepository menuItemRepository
    ) {
        this.menuItemRepository =
                menuItemRepository;
    }

    // ==========================================
    // GET ALL CUSTOMER MARKETPLACE ITEMS
    // ==========================================

    @Transactional(readOnly = true)
    public List<MarketplaceMenuItemResponse>
            getAvailableMarketplaceItems() {

        List<MenuItem> menuItems =
                menuItemRepository
                        .findByAvailableTrueAndRestaurantActiveTrueAndRestaurantApprovalStatusOrderByNameAsc(
                                RestaurantApprovalStatus.APPROVED
                        );

        return menuItems
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // ENTITY -> MARKETPLACE DTO
    // ==========================================

    private MarketplaceMenuItemResponse toResponse(
            MenuItem menuItem
    ) {

        Restaurant restaurant =
                menuItem.getRestaurant();

        Category category =
                menuItem.getCategory();

        return new MarketplaceMenuItemResponse(

                menuItem.getId(),

                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getImageUrl(),

                menuItem.isVegetarian(),
                menuItem.isAvailable(),

                menuItem.getSpiceLevel() == null
                        ? null
                        : menuItem.getSpiceLevel().name(),

                menuItem.getTasteType() == null
                        ? null
                        : menuItem.getTasteType().name(),

                menuItem.getProteinLevel() == null
                        ? null
                        : menuItem.getProteinLevel().name(),

                category == null
                        ? null
                        : category.getId(),

                category == null
                        ? null
                        : category.getName(),

                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCity(),
                restaurant.getState()
        );
    }
}