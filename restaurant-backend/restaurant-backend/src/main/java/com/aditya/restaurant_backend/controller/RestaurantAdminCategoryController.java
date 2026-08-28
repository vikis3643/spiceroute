package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.entity.Category;
import com.aditya.restaurant_backend.service.RestaurantAdminCategoryService;

@RestController
@RequestMapping("/api/restaurant-admin/categories")
public class RestaurantAdminCategoryController {

    private final RestaurantAdminCategoryService
            restaurantAdminCategoryService;

    public RestaurantAdminCategoryController(
            RestaurantAdminCategoryService restaurantAdminCategoryService
    ) {
        this.restaurantAdminCategoryService =
                restaurantAdminCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>>
            getCategories(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminCategoryService
                        .getCategories(
                                restaurantId
                        )
        );
    }

    @PostMapping
    public ResponseEntity<Category>
            createCategory(
                    @AuthenticationPrincipal Jwt jwt,
                    @RequestBody Category category
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        Category createdCategory =
                restaurantAdminCategoryService
                        .createCategory(
                                restaurantId,
                                category
                        );

        return ResponseEntity.ok(
                createdCategory
        );
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category>
            updateCategory(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long categoryId,
                    @RequestBody Category category
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        Category updatedCategory =
                restaurantAdminCategoryService
                        .updateCategory(
                                restaurantId,
                                categoryId,
                                category
                        );

        return ResponseEntity.ok(
                updatedCategory
        );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void>
            deleteCategory(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long categoryId
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        restaurantAdminCategoryService
                .deleteCategory(
                        restaurantId,
                        categoryId
                );

        return ResponseEntity.noContent()
                .build();
    }
}