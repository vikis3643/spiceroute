package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.entity.Category;
import com.aditya.restaurant_backend.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService
            categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService =
                categoryService;
    }

    // ==========================================
    // PUBLIC CATEGORY LIST
    // ==========================================

    @GetMapping
    public List<Category>
            getAllCategories() {

        return categoryService
                .getAllCategories();
    }

    // ==========================================
    // PUBLIC SINGLE CATEGORY
    // ==========================================

    @GetMapping("/{id}")
    public Category
            getCategoryById(
                    @PathVariable
                    Long id
            ) {

        return categoryService
                .getCategoryById(
                        id
                );
    }
}