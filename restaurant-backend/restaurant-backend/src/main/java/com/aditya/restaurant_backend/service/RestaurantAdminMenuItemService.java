package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.entity.Category;
import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.CategoryRepository;
import com.aditya.restaurant_backend.repository.MenuItemRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class RestaurantAdminMenuItemService {

    private final MenuItemRepository
            menuItemRepository;

    private final CategoryRepository
            categoryRepository;

    private final RestaurantRepository
            restaurantRepository;

    public RestaurantAdminMenuItemService(
            MenuItemRepository menuItemRepository,
            CategoryRepository categoryRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.menuItemRepository =
                menuItemRepository;

        this.categoryRepository =
                categoryRepository;

        this.restaurantRepository =
                restaurantRepository;
    }

    public List<MenuItem> getMenuItems(
            Long restaurantId
    ) {
        return menuItemRepository
                .findByRestaurantIdOrderByNameAsc(
                        restaurantId
                );
    }

    public MenuItem getMenuItem(
            Long restaurantId,
            Long menuItemId
    ) {
        return getRestaurantMenuItem(
                restaurantId,
                menuItemId
        );
    }

    @Transactional
    public MenuItem createMenuItem(
            Long restaurantId,
            MenuItem request
    ) {
        Restaurant restaurant =
                getRestaurant(
                        restaurantId
                );

        Category category =
                getRestaurantCategory(
                        restaurantId,
                        request.getCategory()
                                .getId()
                );

        MenuItem menuItem =
                new MenuItem();

        menuItem.setRestaurant(
                restaurant
        );

        menuItem.setName(
                request.getName().trim()
        );

        menuItem.setDescription(
                request.getDescription()
        );

        menuItem.setPrice(
                request.getPrice()
        );

        menuItem.setImageUrl(
                request.getImageUrl()
        );

        menuItem.setVegetarian(
                request.isVegetarian()
        );

        menuItem.setAvailable(
                request.isAvailable()
        );

        menuItem.setSpiceLevel(
                request.getSpiceLevel()
        );

        menuItem.setTasteType(
                request.getTasteType()
        );

        menuItem.setProteinLevel(
                request.getProteinLevel()
        );

        menuItem.setCategory(
                category
        );

        return menuItemRepository
                .save(menuItem);
    }

    @Transactional
    public MenuItem updateMenuItem(
            Long restaurantId,
            Long menuItemId,
            MenuItem request
    ) {
        MenuItem menuItem =
                getRestaurantMenuItem(
                        restaurantId,
                        menuItemId
                );

        Category category =
                getRestaurantCategory(
                        restaurantId,
                        request.getCategory()
                                .getId()
                );

        menuItem.setName(
                request.getName().trim()
        );

        menuItem.setDescription(
                request.getDescription()
        );

        menuItem.setPrice(
                request.getPrice()
        );

        menuItem.setImageUrl(
                request.getImageUrl()
        );

        menuItem.setVegetarian(
                request.isVegetarian()
        );

        menuItem.setAvailable(
                request.isAvailable()
        );

        menuItem.setSpiceLevel(
                request.getSpiceLevel()
        );

        menuItem.setTasteType(
                request.getTasteType()
        );

        menuItem.setProteinLevel(
                request.getProteinLevel()
        );

        menuItem.setCategory(
                category
        );

        return menuItemRepository
                .save(menuItem);
    }

    @Transactional
    public void deleteMenuItem(
            Long restaurantId,
            Long menuItemId
    ) {
        MenuItem menuItem =
                getRestaurantMenuItem(
                        restaurantId,
                        menuItemId
                );

        menuItemRepository.delete(
                menuItem
        );
    }

    private MenuItem getRestaurantMenuItem(
            Long restaurantId,
            Long menuItemId
    ) {
        return menuItemRepository
                .findByIdAndRestaurantId(
                        menuItemId,
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Menu item not found for this restaurant"
                        )
                );
    }

    private Category getRestaurantCategory(
            Long restaurantId,
            Long categoryId
    ) {
        return categoryRepository
                .findByIdAndRestaurantId(
                        categoryId,
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Category does not belong to this restaurant"
                        )
                );
    }

    private Restaurant getRestaurant(
            Long restaurantId
    ) {
        return restaurantRepository
                .findById(
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Restaurant not found"
                        )
                );
    }
}