package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.DiscountCalculationResult;
import com.aditya.restaurant_backend.entity.Discount;
import com.aditya.restaurant_backend.entity.DiscountScope;
import com.aditya.restaurant_backend.entity.DiscountType;
import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.entity.OrderItem;
import com.aditya.restaurant_backend.repository.DiscountRepository;
import com.aditya.restaurant_backend.repository.MenuItemRepository;

@Service
public class DiscountCalculationService {

    private static final BigDecimal
            ONE_HUNDRED =
            new BigDecimal("100.00");

    private final DiscountRepository
            discountRepository;

    private final MenuItemRepository
            menuItemRepository;

    public DiscountCalculationService(
            DiscountRepository discountRepository,
            MenuItemRepository menuItemRepository
    ) {
        this.discountRepository =
                discountRepository;

        this.menuItemRepository =
                menuItemRepository;
    }

    public DiscountCalculationResult
            calculateDiscount(
                    List<OrderItem> orderItems,
                    BigDecimal subtotal
            ) {

        if (orderItems == null
                || orderItems.isEmpty()) {

            return new DiscountCalculationResult(
                    BigDecimal.ZERO
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            ),
                    null
            );
        }

        List<Long> menuItemIds =
                orderItems.stream()
                        .map(
                                OrderItem
                                        ::getMenuItemId
                        )
                        .distinct()
                        .toList();

        Map<Long, MenuItem> menuItemsById =
                menuItemRepository
                        .findAllById(menuItemIds)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        MenuItem::getId,
                                        Function.identity()
                                )
                        );

        Long restaurantId =
                determineRestaurantId(
                        orderItems,
                        menuItemsById
                );

        BigDecimal totalDiscount =
                BigDecimal.ZERO;

        List<String> discountNames =
                new ArrayList<>();

        LocalDateTime now =
                LocalDateTime.now();

        List<Discount> restaurantDiscounts =
                discountRepository
                        .findByRestaurantIdAndActiveTrueOrderByCreatedAtDesc(
                                restaurantId
                        );

        for (Discount discount
                : restaurantDiscounts) {

            if (!isCurrentlyValid(
                    discount,
                    subtotal,
                    now
            )) {
                continue;
            }

            BigDecimal eligibleAmount =
                    findEligibleAmount(
                            discount,
                            orderItems,
                            menuItemsById,
                            subtotal,
                            restaurantId
                    );

            if (eligibleAmount
                    .compareTo(
                            BigDecimal.ZERO
                    ) <= 0) {
                continue;
            }

            BigDecimal discountAmount =
                    calculateDiscountAmount(
                            discount,
                            eligibleAmount
                    );

            if (discountAmount
                    .compareTo(
                            BigDecimal.ZERO
                    ) > 0) {

                totalDiscount =
                        totalDiscount.add(
                                discountAmount
                        );

                discountNames.add(
                        discount.getName()
                );
            }
        }

        if (totalDiscount
                .compareTo(subtotal) > 0) {

            totalDiscount = subtotal;
        }

        return new DiscountCalculationResult(
                totalDiscount.setScale(
                        2,
                        RoundingMode.HALF_UP
                ),
                discountNames.isEmpty()
                        ? null
                        : String.join(
                                ", ",
                                discountNames
                        )
        );
    }

    private Long determineRestaurantId(
            List<OrderItem> orderItems,
            Map<Long, MenuItem> menuItemsById
    ) {

        Long restaurantId = null;

        for (OrderItem orderItem
                : orderItems) {

            MenuItem menuItem =
                    menuItemsById.get(
                            orderItem
                                    .getMenuItemId()
                    );

            if (menuItem == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Menu item not found with id: "
                                + orderItem
                                        .getMenuItemId()
                );
            }

            if (menuItem.getRestaurant() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Menu item is not assigned to a restaurant: "
                                + menuItem.getName()
                );
            }

            Long menuItemRestaurantId =
                    menuItem
                            .getRestaurant()
                            .getId();

            if (restaurantId == null) {

                restaurantId =
                        menuItemRestaurantId;

                continue;
            }

            if (!restaurantId.equals(
                    menuItemRestaurantId
            )) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "All items in an order must belong to the same restaurant"
                );
            }
        }

        if (restaurantId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unable to determine restaurant for this order"
            );
        }

        return restaurantId;
    }

    private boolean isCurrentlyValid(
            Discount discount,
            BigDecimal subtotal,
            LocalDateTime now
    ) {

        if (!discount.isActive()) {
            return false;
        }

        if (discount.getStartsAt() != null
                && now.isBefore(
                        discount.getStartsAt()
                )) {
            return false;
        }

        if (discount.getEndsAt() != null
                && now.isAfter(
                        discount.getEndsAt()
                )) {
            return false;
        }

        BigDecimal minimumOrderAmount =
                discount.getMinimumOrderAmount()
                        == null
                        ? BigDecimal.ZERO
                        : discount
                                .getMinimumOrderAmount();

        return subtotal.compareTo(
                minimumOrderAmount
        ) >= 0;
    }

    private BigDecimal findEligibleAmount(
            Discount discount,
            List<OrderItem> orderItems,
            Map<Long, MenuItem> menuItemsById,
            BigDecimal subtotal,
            Long restaurantId
    ) {

        if (discount.getRestaurant() == null
                || !discount
                        .getRestaurant()
                        .getId()
                        .equals(restaurantId)) {

            return BigDecimal.ZERO;
        }

        if (discount.getDiscountScope()
                == DiscountScope.ENTIRE_ORDER) {

            return subtotal;
        }

        BigDecimal eligibleAmount =
                BigDecimal.ZERO;

        for (OrderItem orderItem
                : orderItems) {

            MenuItem menuItem =
                    menuItemsById.get(
                            orderItem
                                    .getMenuItemId()
                    );

            if (menuItem == null
                    || menuItem.getRestaurant() == null
                    || !menuItem
                            .getRestaurant()
                            .getId()
                            .equals(restaurantId)) {

                continue;
            }

            boolean matches = false;

            if (discount.getDiscountScope()
                    == DiscountScope.MENU_ITEM
                    && discount.getMenuItem()
                    != null
                    && discount
                            .getMenuItem()
                            .getRestaurant()
                    != null
                    && discount
                            .getMenuItem()
                            .getRestaurant()
                            .getId()
                            .equals(restaurantId)) {

                matches =
                        discount.getMenuItem()
                                .getId()
                                .equals(
                                        menuItem.getId()
                                );
            }

            if (discount.getDiscountScope()
                    == DiscountScope.CATEGORY
                    && discount.getCategory()
                    != null
                    && discount
                            .getCategory()
                            .getRestaurant()
                    != null
                    && discount
                            .getCategory()
                            .getRestaurant()
                            .getId()
                            .equals(restaurantId)
                    && menuItem.getCategory()
                    != null) {

                matches =
                        discount.getCategory()
                                .getId()
                                .equals(
                                        menuItem
                                                .getCategory()
                                                .getId()
                                );
            }

            if (matches) {

                eligibleAmount =
                        eligibleAmount.add(
                                orderItem
                                        .getLineTotal()
                        );
            }
        }

        return eligibleAmount;
    }

    private BigDecimal
            calculateDiscountAmount(
                    Discount discount,
                    BigDecimal eligibleAmount
            ) {

        BigDecimal discountAmount;

        if (discount.getDiscountType()
                == DiscountType.PERCENTAGE) {

            discountAmount =
                    eligibleAmount
                            .multiply(
                                    discount
                                            .getDiscountValue()
                            )
                            .divide(
                                    ONE_HUNDRED,
                                    2,
                                    RoundingMode.HALF_UP
                            );

        } else {

            discountAmount =
                    discount
                            .getDiscountValue()
                            .min(
                                    eligibleAmount
                            );
        }

        if (discount
                .getMaximumDiscountAmount()
                != null) {

            discountAmount =
                    discountAmount.min(
                            discount
                                    .getMaximumDiscountAmount()
                    );
        }

        return discountAmount;
    }
}