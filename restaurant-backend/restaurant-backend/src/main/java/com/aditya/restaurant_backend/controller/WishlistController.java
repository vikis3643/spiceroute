package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.entity.WishlistItem;
import com.aditya.restaurant_backend.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService
            wishlistService;

    public WishlistController(
            WishlistService wishlistService
    ) {
        this.wishlistService =
                wishlistService;
    }

    @GetMapping
    public List<WishlistItem> getWishlist(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return wishlistService.getWishlist(
                jwt.getSubject()
        );
    }

    @PostMapping("/{menuItemId}")
    public ResponseEntity<WishlistItem>
            addToWishlist(
                    @PathVariable
                    Long menuItemId,
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        WishlistItem savedItem =
                wishlistService.addToWishlist(
                        menuItemId,
                        jwt.getSubject()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedItem);
    }

    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void>
            removeFromWishlist(
                    @PathVariable
                    Long menuItemId,
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        wishlistService.removeFromWishlist(
                menuItemId,
                jwt.getSubject()
        );

        return ResponseEntity.noContent().build();
    }
}