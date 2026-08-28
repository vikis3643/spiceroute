package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.ReviewRequest;
import com.aditya.restaurant_backend.dto.ReviewResponse;
import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.CustomerReview;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;
import com.aditya.restaurant_backend.repository.CustomerReviewRepository;
import com.aditya.restaurant_backend.repository.OrderRepository;

@Service
public class CustomerReviewService {

    private final CustomerReviewRepository
            reviewRepository;

    private final CustomerAccountRepository
            customerAccountRepository;

    private final OrderRepository
            orderRepository;

    public CustomerReviewService(
            CustomerReviewRepository
                    reviewRepository,
            CustomerAccountRepository
                    customerAccountRepository,
            OrderRepository orderRepository
    ) {
        this.reviewRepository =
                reviewRepository;

        this.customerAccountRepository =
                customerAccountRepository;

        this.orderRepository =
                orderRepository;
    }

    @Transactional
    public ReviewResponse submitReview(
            Long orderId,
            ReviewRequest request,
            String customerEmail
    ) {
        CustomerAccount customer =
                findCustomer(customerEmail);

        CustomerOrder order =
                findCustomerOrder(
                        orderId,
                        customer.getId()
                );

        if (order.getStatus()
                != OrderStatus.DELIVERED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You can review an order only after delivery"
            );
        }

        if (reviewRepository
                .existsByOrderId(orderId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A review has already been submitted for this order"
            );
        }

        CustomerReview review =
                new CustomerReview();

        review.setOrder(order);

        review.setCustomerAccount(
                customer
        );

        review.setFoodRating(
                request.foodRating()
        );

        review.setCustomerServiceRating(
                request.customerServiceRating()
        );

        String comment = request.comment();

        review.setComment(
                comment == null
                        || comment.isBlank()
                        ? null
                        : comment.trim()
        );

        return ReviewResponse.from(
                reviewRepository.save(review)
        );
    }

    public ReviewResponse getReviewForOrder(
            Long orderId,
            String customerEmail
    ) {
        CustomerAccount customer =
                findCustomer(customerEmail);

        findCustomerOrder(
                orderId,
                customer.getId()
        );

        return reviewRepository
                .findByOrderId(orderId)
                .map(ReviewResponse::from)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Review not found for this order"
                        )
                );
    }

    public List<ReviewResponse>
            getAllReviews() {

        return reviewRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    private CustomerOrder findCustomerOrder(
            Long orderId,
            Long customerId
    ) {
        CustomerOrder order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Order not found"
                                )
                        );

        if (order.getCustomerAccount() == null
                || !order.getCustomerAccount()
                        .getId()
                        .equals(customerId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot review this order"
            );
        }

        return order;
    }

    private CustomerAccount findCustomer(
            String customerEmail
    ) {
        return customerAccountRepository
                .findByEmailIgnoreCase(
                        customerEmail
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Customer account not found"
                        )
                );
    }
}