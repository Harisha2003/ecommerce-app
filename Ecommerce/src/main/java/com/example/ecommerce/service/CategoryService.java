package com.example.ecommerce.service;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        log.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        log.info("Total categories found: {}", categories.size());
        return categories;
    }

    public Category create(String name) {
        log.info("Creating category: {}", name);
        Category category = new Category();
        category.setName(name);
        Category saved = categoryRepository.save(category);
        log.info("Category created with id: {}", saved.getId());
        return saved;
    }

    public void delete(Long id) {
        log.info("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            log.error("Category not found with id: {}", id);
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Category deleted with id: {}", id);
    }
}
