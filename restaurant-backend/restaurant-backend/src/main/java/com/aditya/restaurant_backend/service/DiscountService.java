package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.entity.Category;
import com.aditya.restaurant_backend.entity.Discount;
import com.aditya.restaurant_backend.entity.DiscountScope;
import com.aditya.restaurant_backend.entity.DiscountType;
import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.repository.CategoryRepository;
import com.aditya.restaurant_backend.repository.DiscountRepository;
import com.aditya.restaurant_backend.repository.MenuItemRepository;

@Service
public class DiscountService {

    private final DiscountRepository
            discountRepository;

    private final MenuItemRepository
            menuItemRepository;

    private final CategoryRepository
            categoryRepository;

    public DiscountService(
            DiscountRepository discountRepository,
            MenuItemRepository menuItemRepository,
            CategoryRepository categoryRepository
    ) {
        this.discountRepository =
                discountRepository;

        this.menuItemRepository =
                menuItemRepository;

        this.categoryRepository =
                categoryRepository;
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public Discount getDiscountById(Long id) {
        return discountRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Discount not found with id: "
                                        + id
                        )
                );
    }

    @Transactional
    public Discount createDiscount(
            Discount request
    ) {
        validateDiscount(request);

        Discount discount =
                new Discount();

        copyDiscountValues(
                request,
                discount
        );

        return discountRepository.save(
                discount
        );
    }

    @Transactional
    public Discount updateDiscount(
            Long id,
            Discount request
    ) {
        Discount discount =
                getDiscountById(id);

        validateDiscount(request);

        copyDiscountValues(
                request,
                discount
        );

        return discountRepository.save(
                discount
        );
    }

    @Transactional
    public void deleteDiscount(Long id) {
        Discount discount =
                getDiscountById(id);

        discountRepository.delete(discount);
    }

    private void validateDiscount(
            Discount discount
    ) {
        if (discount.getDiscountType()
                == DiscountType.PERCENTAGE
                && discount.getDiscountValue()
                .compareTo(
                        new BigDecimal("100.00")
                ) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Percentage discount cannot exceed 100%"
            );
        }

        if (discount.getStartsAt() != null
                && discount.getEndsAt() != null
                && !discount.getEndsAt()
                .isAfter(
                        discount.getStartsAt()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Discount end time must be after its start time"
            );
        }

        if (discount.getDiscountScope()
                == DiscountScope.MENU_ITEM
                && (
                        discount.getMenuItem()
                                == null
                        || discount.getMenuItem()
                                .getId()
                                == null
                )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a menu item"
            );
        }

        if (discount.getDiscountScope()
                == DiscountScope.CATEGORY
                && (
                        discount.getCategory()
                                == null
                        || discount.getCategory()
                                .getId()
                                == null
                )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a category"
            );
        }
    }

    private void copyDiscountValues(
            Discount source,
            Discount target
    ) {
        target.setName(
                source.getName().trim()
        );

        target.setDescription(
                source.getDescription()
        );

        target.setDiscountType(
                source.getDiscountType()
        );

        target.setDiscountScope(
                source.getDiscountScope()
        );

        target.setDiscountValue(
                source.getDiscountValue()
        );

        target.setMinimumOrderAmount(
                source.getMinimumOrderAmount()
                        == null
                        ? BigDecimal.ZERO
                        : source
                                .getMinimumOrderAmount()
        );

        target.setMaximumDiscountAmount(
                source.getMaximumDiscountAmount()
        );

        target.setStartsAt(
                source.getStartsAt()
        );

        target.setEndsAt(
                source.getEndsAt()
        );

        target.setActive(
                source.isActive()
        );

        configureDiscountTarget(
                source,
                target
        );
    }

    private void configureDiscountTarget(
            Discount source,
            Discount target
    ) {
        target.setMenuItem(null);
        target.setCategory(null);

        if (source.getDiscountScope()
                == DiscountScope.MENU_ITEM) {

            MenuItem menuItem =
                    menuItemRepository
                            .findById(
                                    source.getMenuItem()
                                            .getId()
                            )
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Selected menu item was not found"
                                    )
                            );

            target.setMenuItem(menuItem);
            return;
        }

        if (source.getDiscountScope()
                == DiscountScope.CATEGORY) {

            Category category =
                    categoryRepository
                            .findById(
                                    source.getCategory()
                                            .getId()
                            )
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Selected category was not found"
                                    )
                            );

            target.setCategory(category);
        }
    }
}