package com.aditya.restaurant_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.entity.CustomerReview;
import com.aditya.restaurant_backend.service.RestaurantAdminReviewService;

@RestController
@RequestMapping("/api/restaurant-admin/reviews")
public class RestaurantAdminReviewController {

    private final RestaurantAdminReviewService
            restaurantAdminReviewService;

    public RestaurantAdminReviewController(
            RestaurantAdminReviewService restaurantAdminReviewService
    ) {
        this.restaurantAdminReviewService =
                restaurantAdminReviewService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerReview>>
            getReviews(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminReviewService
                        .getReviews(
                                restaurantId
                        )
        );
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<CustomerReview>
            getReview(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long reviewId
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminReviewService
                        .getReview(
                                restaurantId,
                                reviewId
                        )
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<CustomerReview>
            getReviewByOrder(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long orderId
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminReviewService
                        .getReviewByOrder(
                                restaurantId,
                                orderId
                        )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>>
            getReviewCount(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        long count =
                restaurantAdminReviewService
                        .getReviewCount(
                                restaurantId
                        );

        return ResponseEntity.ok(
                Map.of(
                        "count",
                        count
                )
        );
    }
}