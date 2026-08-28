package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.MarketplaceMenuItemResponse;
import com.aditya.restaurant_backend.service.MarketplaceService;

@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController {

    private final MarketplaceService
            marketplaceService;

    public MarketplaceController(
            MarketplaceService marketplaceService
    ) {
        this.marketplaceService =
                marketplaceService;
    }

    // ==========================================
    // PUBLIC MARKETPLACE ITEMS
    // ==========================================

    @GetMapping("/menu-items")
    public ResponseEntity<
            List<MarketplaceMenuItemResponse>
            > getMarketplaceMenuItems() {

        return ResponseEntity.ok(
                marketplaceService
                        .getAvailableMarketplaceItems()
        );
    }
}