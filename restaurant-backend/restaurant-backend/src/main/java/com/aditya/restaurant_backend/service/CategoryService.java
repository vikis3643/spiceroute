package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.entity.Category;
import com.aditya.restaurant_backend.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found with id: " + id
                ));
    }

    public Category createCategory(Category category) {
        String categoryName = category.getName().trim();

        if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Category already exists: " + categoryName
            );
        }

        category.setName(categoryName);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);
        String categoryName = updatedCategory.getName().trim();

        categoryRepository.findByNameIgnoreCase(categoryName)
                .filter(category -> !category.getId().equals(id))
                .ifPresent(category -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Category already exists: " + categoryName
                    );
                });

        existingCategory.setName(categoryName);
        existingCategory.setDescription(updatedCategory.getDescription());
        existingCategory.setActive(updatedCategory.isActive());

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}