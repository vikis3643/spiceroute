package com.aditya.restaurant_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.ReviewRequest;
import com.aditya.restaurant_backend.dto.ReviewResponse;
import com.aditya.restaurant_backend.service.CustomerReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class CustomerReviewController {

    private final CustomerReviewService
            reviewService;

    public CustomerReviewController(
            CustomerReviewService reviewService
    ) {
        this.reviewService =
                reviewService;
    }

    // ==========================================
    // CUSTOMER SUBMIT REVIEW
    // ==========================================

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<ReviewResponse>
            submitReview(
                    @PathVariable
                    Long orderId,

                    @Valid
                    @RequestBody
                    ReviewRequest request,

                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        ReviewResponse savedReview =
                reviewService
                        .submitReview(
                                orderId,
                                request,
                                jwt.getSubject()
                        );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        savedReview
                );
    }

    // ==========================================
    // CUSTOMER GET OWN ORDER REVIEW
    // ==========================================

    @GetMapping("/orders/{orderId}")
    public ReviewResponse
            getReviewForOrder(
                    @PathVariable
                    Long orderId,

                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        return reviewService
                .getReviewForOrder(
                        orderId,
                        jwt.getSubject()
                );
    }
}