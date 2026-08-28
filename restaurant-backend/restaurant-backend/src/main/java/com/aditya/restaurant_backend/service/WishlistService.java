package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.entity.WishlistItem;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;
import com.aditya.restaurant_backend.repository.MenuItemRepository;
import com.aditya.restaurant_backend.repository.WishlistRepository;

@Service
public class WishlistService {

    private final WishlistRepository
            wishlistRepository;

    private final CustomerAccountRepository
            customerAccountRepository;

    private final MenuItemRepository
            menuItemRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            CustomerAccountRepository
                    customerAccountRepository,
            MenuItemRepository menuItemRepository
    ) {
        this.wishlistRepository =
                wishlistRepository;

        this.customerAccountRepository =
                customerAccountRepository;

        this.menuItemRepository =
                menuItemRepository;
    }

    public List<WishlistItem> getWishlist(
            String customerEmail
    ) {
        CustomerAccount customer =
                findCustomer(customerEmail);

        return wishlistRepository
                .findByCustomerAccountIdOrderByCreatedAtDesc(
                        customer.getId()
                );
    }

    @Transactional
    public WishlistItem addToWishlist(
            Long menuItemId,
            String customerEmail
    ) {
        CustomerAccount customer =
                findCustomer(customerEmail);

        MenuItem menuItem =
                menuItemRepository
                        .findById(menuItemId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Menu item not found"
                                )
                        );

        return wishlistRepository
                .findByCustomerAccountIdAndMenuItemId(
                        customer.getId(),
                        menuItemId
                )
                .orElseGet(() -> {
                    WishlistItem wishlistItem =
                            new WishlistItem();

                    wishlistItem.setCustomerAccount(
                            customer
                    );

                    wishlistItem.setMenuItem(
                            menuItem
                    );

                    return wishlistRepository.save(
                            wishlistItem
                    );
                });
    }

    @Transactional
    public void removeFromWishlist(
            Long menuItemId,
            String customerEmail
    ) {
        CustomerAccount customer =
                findCustomer(customerEmail);

        if (!wishlistRepository
                .existsByCustomerAccountIdAndMenuItemId(
                        customer.getId(),
                        menuItemId
                )) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Wishlist item not found"
            );
        }

        wishlistRepository
                .deleteByCustomerAccountIdAndMenuItemId(
                        customer.getId(),
                        menuItemId
                );
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