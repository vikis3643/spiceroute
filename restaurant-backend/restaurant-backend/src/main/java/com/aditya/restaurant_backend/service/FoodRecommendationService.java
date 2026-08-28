package com.aditya.restaurant_backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aditya.restaurant_backend.dto.FoodRecommendationRequest;
import com.aditya.restaurant_backend.dto.FoodRecommendationResponse;
import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.repository.MenuItemRepository;

@Service
public class FoodRecommendationService {

    private static final int MAXIMUM_RESULTS = 6;

    private final MenuItemRepository
            menuItemRepository;

    public FoodRecommendationService(
            MenuItemRepository menuItemRepository
    ) {
        this.menuItemRepository =
                menuItemRepository;
    }

    public List<FoodRecommendationResponse>
            recommendFood(
                    FoodRecommendationRequest request
            ) {

        List<MenuItem> availableItems =
                menuItemRepository
                        .findByAvailableTrue();

        List<FoodRecommendationResponse>
                recommendations =
                new ArrayList<>();

        for (MenuItem menuItem
                : availableItems) {

            if (menuItem.getPrice().compareTo(
                    request.maximumBudget()
            ) > 0) {
                continue;
            }

            if (request.vegetarian() != null
                    && menuItem.isVegetarian()
                    != request.vegetarian()) {
                continue;
            }

            int matchScore = 1;

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "Within your budget"
            );

            if (request.vegetarian() != null) {
                matchScore += 2;

                reasons.add(
                        request.vegetarian()
                                ? "Matches your vegetarian preference"
                                : "Matches your non-vegetarian preference"
                );
            }

            if (request.spiceLevel() != null
                    && request.spiceLevel()
                    == menuItem.getSpiceLevel()) {

                matchScore += 3;

                reasons.add(
                        "Matches your "
                                + readableName(
                                        request.spiceLevel()
                                                .name()
                                )
                                + " spice preference"
                );
            }

            if (request.tasteType() != null
                    && request.tasteType()
                    == menuItem.getTasteType()) {

                matchScore += 3;

                reasons.add(
                        "Matches your "
                                + readableName(
                                        request.tasteType()
                                                .name()
                                )
                                + " taste preference"
                );
            }

            if (request.proteinLevel() != null
                    && request.proteinLevel()
                    == menuItem.getProteinLevel()) {

                matchScore += 2;

                reasons.add(
                        request.proteinLevel()
                                .name()
                                .equals("HIGH")
                                ? "Matches your high-protein preference"
                                : "Matches your protein preference"
                );
            }

            recommendations.add(
                    new FoodRecommendationResponse(
                            menuItem,
                            matchScore,
                            reasons
                    )
            );
        }

        return recommendations
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        FoodRecommendationResponse
                                                ::matchScore
                                )
                                .reversed()
                                .thenComparing(
                                        recommendation ->
                                                recommendation
                                                        .menuItem()
                                                        .getPrice()
                                )
                )
                .limit(MAXIMUM_RESULTS)
                .toList();
    }

    private String readableName(
            String value
    ) {
        return value
                .toLowerCase()
                .replace("_", " ");
    }
}