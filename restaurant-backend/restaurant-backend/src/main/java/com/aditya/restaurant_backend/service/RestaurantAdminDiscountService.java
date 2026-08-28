package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.entity.Category;
import com.aditya.restaurant_backend.entity.Discount;
import com.aditya.restaurant_backend.entity.DiscountScope;
import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.repository.CategoryRepository;
import com.aditya.restaurant_backend.repository.DiscountRepository;
import com.aditya.restaurant_backend.repository.MenuItemRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;

@Service
public class RestaurantAdminDiscountService {

    private final DiscountRepository
            discountRepository;

    private final RestaurantRepository
            restaurantRepository;

    private final CategoryRepository
            categoryRepository;

    private final MenuItemRepository
            menuItemRepository;

    public RestaurantAdminDiscountService(
            DiscountRepository discountRepository,
            RestaurantRepository restaurantRepository,
            CategoryRepository categoryRepository,
            MenuItemRepository menuItemRepository
    ) {
        this.discountRepository =
                discountRepository;

        this.restaurantRepository =
                restaurantRepository;

        this.categoryRepository =
                categoryRepository;

        this.menuItemRepository =
                menuItemRepository;
    }

    public List<Discount> getDiscounts(
            Long restaurantId
    ) {
        return discountRepository
                .findByRestaurantIdOrderByCreatedAtDesc(
                        restaurantId
                );
    }

    public Discount getDiscount(
            Long restaurantId,
            Long discountId
    ) {
        return getRestaurantDiscount(
                restaurantId,
                discountId
        );
    }

    @Transactional
    public Discount createDiscount(
            Long restaurantId,
            Discount request
    ) {
        Restaurant restaurant =
                getRestaurant(
                        restaurantId
                );

        Discount discount =
                new Discount();

        applyDiscountData(
                discount,
                request,
                restaurantId
        );

        discount.setRestaurant(
                restaurant
        );

        return discountRepository.save(
                discount
        );
    }

    @Transactional
    public Discount updateDiscount(
            Long restaurantId,
            Long discountId,
            Discount request
    ) {
        Discount discount =
                getRestaurantDiscount(
                        restaurantId,
                        discountId
                );

        applyDiscountData(
                discount,
                request,
                restaurantId
        );

        return discountRepository.save(
                discount
        );
    }

    @Transactional
    public void deleteDiscount(
            Long restaurantId,
            Long discountId
    ) {
        Discount discount =
                getRestaurantDiscount(
                        restaurantId,
                        discountId
                );

        discountRepository.delete(
                discount
        );
    }

    public long getDiscountCount(
            Long restaurantId
    ) {
        return discountRepository
                .countByRestaurantId(
                        restaurantId
                );
    }

    private void applyDiscountData(
            Discount discount,
            Discount request,
            Long restaurantId
    ) {
        discount.setName(
                request.getName()
                        .trim()
        );

        discount.setDescription(
                request.getDescription()
        );

        discount.setDiscountType(
                request.getDiscountType()
        );

        discount.setDiscountScope(
                request.getDiscountScope()
        );

        discount.setDiscountValue(
                request.getDiscountValue()
        );

        discount.setMinimumOrderAmount(
                request.getMinimumOrderAmount()
        );

        discount.setMaximumDiscountAmount(
                request.getMaximumDiscountAmount()
        );

        discount.setStartsAt(
                request.getStartsAt()
        );

        discount.setEndsAt(
                request.getEndsAt()
        );

        discount.setActive(
                request.isActive()
        );

        discount.setCategory(null);
        discount.setMenuItem(null);

        if (request.getDiscountScope()
                == DiscountScope.CATEGORY) {

            if (request.getCategory() == null
                    || request.getCategory()
                            .getId() == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Category is required for CATEGORY discount"
                );
            }

            Category category =
                    categoryRepository
                            .findByIdAndRestaurantId(
                                    request.getCategory()
                                            .getId(),
                                    restaurantId
                            )
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.BAD_REQUEST,
                                            "Category does not belong to this restaurant"
                                    )
                            );

            discount.setCategory(
                    category
            );
        }

        if (request.getDiscountScope()
                == DiscountScope.MENU_ITEM) {

            if (request.getMenuItem() == null
                    || request.getMenuItem()
                            .getId() == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Menu item is required for MENU_ITEM discount"
                );
            }

            MenuItem menuItem =
                    menuItemRepository
                            .findByIdAndRestaurantId(
                                    request.getMenuItem()
                                            .getId(),
                                    restaurantId
                            )
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.BAD_REQUEST,
                                            "Menu item does not belong to this restaurant"
                                    )
                            );

            discount.setMenuItem(
                    menuItem
            );
        }
    }

    private Discount getRestaurantDiscount(
            Long restaurantId,
            Long discountId
    ) {
        return discountRepository
                .findByIdAndRestaurantId(
                        discountId,
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Discount not found for this restaurant"
                        )
                );
    }

    private Restaurant getRestaurant(
            Long restaurantId
    ) {
        return restaurantRepository
                .findById(
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Restaurant not found"
                        )
                );
    }
}