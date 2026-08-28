package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.service.MenuItemService;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private final MenuItemService
            menuItemService;

    public MenuItemController(
            MenuItemService menuItemService
    ) {
        this.menuItemService =
                menuItemService;
    }

    // ==========================================
    // PUBLIC MENU LIST
    // ==========================================

    @GetMapping
    public List<MenuItem>
            getAllMenuItems() {

        return menuItemService
                .getAllMenuItems();
    }

    // ==========================================
    // PUBLIC SINGLE MENU ITEM
    // ==========================================

    @GetMapping("/{id}")
    public MenuItem
            getMenuItemById(
                    @PathVariable
                    Long id
            ) {

        return menuItemService
                .getMenuItemById(
                        id
                );
    }

    // ==========================================
    // PUBLIC CATEGORY MENU
    // ==========================================

    @GetMapping("/category/{categoryId}")
    public List<MenuItem>
            getMenuItemsByCategory(
                    @PathVariable
                    Long categoryId
            ) {

        return menuItemService
                .getMenuItemsByCategory(
                        categoryId
                );
    }

    // ==========================================
    // PUBLIC AVAILABLE MENU ITEMS
    // ==========================================

    @GetMapping("/available")
    public List<MenuItem>
            getAvailableMenuItems() {

        return menuItemService
                .getAvailableMenuItems();
    }

    // ==========================================
    // PUBLIC MENU SEARCH
    // ==========================================

    @GetMapping("/search")
    public List<MenuItem>
            searchMenuItems(
                    @RequestParam
                    String name
            ) {

        return menuItemService
                .searchMenuItems(
                        name
                );
    }
}