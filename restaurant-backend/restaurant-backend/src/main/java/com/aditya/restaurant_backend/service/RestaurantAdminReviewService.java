package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.entity.CustomerReview;
import com.aditya.restaurant_backend.repository.CustomerReviewRepository;

@Service
public class RestaurantAdminReviewService {

    private final CustomerReviewRepository
            customerReviewRepository;

    public RestaurantAdminReviewService(
            CustomerReviewRepository customerReviewRepository
    ) {
        this.customerReviewRepository =
                customerReviewRepository;
    }

    public List<CustomerReview> getReviews(
            Long restaurantId
    ) {

        return customerReviewRepository
                .findByOrder_Restaurant_IdOrderByCreatedAtDesc(
                        restaurantId
                );
    }

    public CustomerReview getReview(
            Long restaurantId,
            Long reviewId
    ) {

        return customerReviewRepository
                .findByIdAndOrder_Restaurant_Id(
                        reviewId,
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Review not found for this restaurant"
                        )
                );
    }

    public CustomerReview getReviewByOrder(
            Long restaurantId,
            Long orderId
    ) {

        return customerReviewRepository
                .findByOrderIdAndOrder_Restaurant_Id(
                        orderId,
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Review not found for this restaurant order"
                        )
                );
    }

    public long getReviewCount(
            Long restaurantId
    ) {

        return customerReviewRepository
                .countByOrder_Restaurant_Id(
                        restaurantId
                );
    }
}