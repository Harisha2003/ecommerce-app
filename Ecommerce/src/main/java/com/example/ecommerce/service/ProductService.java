package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.ProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        List<ProductResponse> products = productRepository.findAll().stream().map(this::toResponse).toList();
        log.info("Total products found: {}", products.size());
        return products;
    }

    public List<ProductResponse> searchProducts(String name) {
        log.info("Searching products with name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name).stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> getByCategory(Long categoryId) {
        log.info("Fetching products for categoryId: {}", categoryId);
        return productRepository.findByCategoryId(categoryId).stream().map(this::toResponse).toList();
    }

    public ProductResponse getById(Long id) {
        log.info("Fetching product with id: {}", id);
        return toResponse(findProduct(id));
    }

    public ProductResponse create(ProductRequest request) {
        log.info("Creating product: {}", request.name());
        Product product = new Product();
        mapToEntity(request, product);
        Product saved = productRepository.save(product);
        log.info("Product created with id: {}", saved.getId());
        return toResponse(saved);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);
        Product product = findProduct(id);
        mapToEntity(request, product);
        Product saved = productRepository.save(product);
        log.info("Product updated: {}", saved.getName());
        return toResponse(saved);
    }

    public void delete(Long id) {
        log.info("Deleting product with id: {}", id);
        productRepository.deleteById(id);
        log.info("Product deleted with id: {}", id);
    }

    private void mapToEntity(ProductRequest request, Product product) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(),
                p.getCategory() != null ? p.getCategory().getName() : null
        );
    }
}
