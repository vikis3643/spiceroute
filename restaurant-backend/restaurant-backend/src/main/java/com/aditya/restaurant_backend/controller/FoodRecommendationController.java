package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.FoodRecommendationRequest;
import com.aditya.restaurant_backend.dto.FoodRecommendationResponse;
import com.aditya.restaurant_backend.service.FoodRecommendationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recommendations")
public class FoodRecommendationController {

    private final FoodRecommendationService
            foodRecommendationService;

    public FoodRecommendationController(
            FoodRecommendationService
                    foodRecommendationService
    ) {
        this.foodRecommendationService =
                foodRecommendationService;
    }

    @PostMapping
    public List<FoodRecommendationResponse>
            getRecommendations(
                    @Valid
                    @RequestBody
                    FoodRecommendationRequest request
            ) {

        return foodRecommendationService
                .recommendFood(request);
    }
}