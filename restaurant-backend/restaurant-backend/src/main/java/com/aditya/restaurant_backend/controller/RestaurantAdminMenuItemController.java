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

import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.service.RestaurantAdminMenuItemService;

@RestController
@RequestMapping("/api/restaurant-admin/menu-items")
public class RestaurantAdminMenuItemController {

    private final RestaurantAdminMenuItemService
            restaurantAdminMenuItemService;

    public RestaurantAdminMenuItemController(
            RestaurantAdminMenuItemService restaurantAdminMenuItemService
    ) {
        this.restaurantAdminMenuItemService =
                restaurantAdminMenuItemService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>>
            getMenuItems(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminMenuItemService
                        .getMenuItems(
                                restaurantId
                        )
        );
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<MenuItem>
            getMenuItem(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long menuItemId
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminMenuItemService
                        .getMenuItem(
                                restaurantId,
                                menuItemId
                        )
        );
    }

    @PostMapping
    public ResponseEntity<MenuItem>
            createMenuItem(
                    @AuthenticationPrincipal Jwt jwt,
                    @RequestBody MenuItem menuItem
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        MenuItem createdMenuItem =
                restaurantAdminMenuItemService
                        .createMenuItem(
                                restaurantId,
                                menuItem
                        );

        return ResponseEntity.ok(
                createdMenuItem
        );
    }

    @PutMapping("/{menuItemId}")
    public ResponseEntity<MenuItem>
            updateMenuItem(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long menuItemId,
                    @RequestBody MenuItem menuItem
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        MenuItem updatedMenuItem =
                restaurantAdminMenuItemService
                        .updateMenuItem(
                                restaurantId,
                                menuItemId,
                                menuItem
                        );

        return ResponseEntity.ok(
                updatedMenuItem
        );
    }

    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void>
            deleteMenuItem(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long menuItemId
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        restaurantAdminMenuItemService
                .deleteMenuItem(
                        restaurantId,
                        menuItemId
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}