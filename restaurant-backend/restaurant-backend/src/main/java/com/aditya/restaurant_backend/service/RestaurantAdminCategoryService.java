package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.entity.Category;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.CategoryRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class RestaurantAdminCategoryService {

    private final CategoryRepository
            categoryRepository;

    private final RestaurantRepository
            restaurantRepository;

    public RestaurantAdminCategoryService(
            CategoryRepository categoryRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.categoryRepository =
                categoryRepository;

        this.restaurantRepository =
                restaurantRepository;
    }

    public List<Category> getCategories(
            Long restaurantId
    ) {
        return categoryRepository
                .findByRestaurantIdOrderByNameAsc(
                        restaurantId
                );
    }

    @Transactional
    public Category createCategory(
            Long restaurantId,
            Category category
    ) {
        Restaurant restaurant =
                getRestaurant(
                        restaurantId
                );

        String categoryName =
                category.getName()
                        .trim();

        boolean exists =
                categoryRepository
                        .existsByRestaurantIdAndNameIgnoreCase(
                                restaurantId,
                                categoryName
                        );

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Category already exists for this restaurant"
            );
        }

        Category newCategory =
                new Category();

        newCategory.setRestaurant(
                restaurant
        );

        newCategory.setName(
                categoryName
        );

        newCategory.setDescription(
                category.getDescription()
        );

        newCategory.setActive(
                category.isActive()
        );

        return categoryRepository
                .save(newCategory);
    }

    @Transactional
    public Category updateCategory(
            Long restaurantId,
            Long categoryId,
            Category request
    ) {
        Category category =
                getRestaurantCategory(
                        restaurantId,
                        categoryId
                );

        String categoryName =
                request.getName()
                        .trim();

        categoryRepository
                .findByRestaurantIdAndNameIgnoreCase(
                        restaurantId,
                        categoryName
                )
                .filter(existing ->
                        !existing.getId()
                                .equals(categoryId)
                )
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Category already exists for this restaurant"
                    );
                });

        category.setName(
                categoryName
        );

        category.setDescription(
                request.getDescription()
        );

        category.setActive(
                request.isActive()
        );

        return categoryRepository
                .save(category);
    }

    @Transactional
    public void deleteCategory(
            Long restaurantId,
            Long categoryId
    ) {
        Category category =
                getRestaurantCategory(
                        restaurantId,
                        categoryId
                );

        categoryRepository.delete(
                category
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
                                HttpStatus.NOT_FOUND,
                                "Category not found for this restaurant"
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