package com.scaler.capstone.product.services;

import com.scaler.capstone.product.dto.CategoryDTO;
import com.scaler.capstone.product.models.product.Category;
import com.scaler.capstone.product.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Optional<Category> existingCategory = categoryRepository.findByNameAndIsDeletedFalse(categoryDTO.getName());
        if (existingCategory.isPresent() && !existingCategory.get().isDeleted()) {
            throw new RuntimeException("Category with name '" + categoryDTO.getName() + "' already exists.");
        }
        Category category = new Category();
        category.setName(categoryDTO.getName());
        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.fromCategory(savedCategory);
    }



    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll().stream()
                .filter(cat -> !cat.isDeleted())
                .collect(Collectors.toList());


        return categories.stream()
                .map(CategoryDTO::fromCategory)
                .collect(Collectors.toList());
    }



    public Optional<CategoryDTO> getCategoryById(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id)
                .filter(cat -> !cat.isDeleted());

        return categoryOpt.map(CategoryDTO::fromCategory);
    }

    public Optional<Category> getCategoryByName(String categoryName) {
        Optional<Category> categoryOpt = categoryRepository.findByNameAndIsDeletedFalse(categoryName)
                .filter(cat -> !cat.isDeleted());

        return categoryOpt;
    }


    @Transactional

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        if (category.isDeleted()) {
            throw new RuntimeException("Cannot update a deleted category.");
        }

        category.setName(categoryDTO.getName());
        Category updatedCategory = categoryRepository.save(category);

        return CategoryDTO.fromCategory(updatedCategory);
    }


    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        category.setDeleted(true);
        categoryRepository.save(category);
    }


}
