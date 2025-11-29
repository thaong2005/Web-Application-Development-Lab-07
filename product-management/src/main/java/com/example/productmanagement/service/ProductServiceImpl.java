package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // EXERCISE 7: SORTING
    @Override
    public List<Product> getAllProducts(Sort sort) {
        return productRepository.findAll(sort);
    }
    
    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @Override
    public Product saveProduct(Product product) {
        // Validation logic can go here
        return productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }
    
    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    // EXERCISE 5: ADVANCED SEARCH
    
    // Task 5.1: Multi-Criteria Search
    @Override
    public List<Product> advancedSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.searchProducts(name, category, minPrice, maxPrice);
    }
    
    // Task 5.2: Category Filter
    @Override
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
    
    // Task 5.3: Search with Pagination
    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable);
    }
    
    // EXERCISE 8: STATISTICS DASHBOARD
    
    // Task 8.1: Statistics Methods
    @Override
    public long countByCategory(String category) {
        return productRepository.countByCategory(category);
    }
    
    @Override
    public BigDecimal calculateTotalValue() {
        BigDecimal total = productRepository.calculateTotalValue();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal calculateAveragePrice() {
        BigDecimal average = productRepository.calculateAveragePrice();
        return average != null ? average : BigDecimal.ZERO;
    }
    
    @Override
    public List<Product> findLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }
    
    @Override
    public Map<String, Long> getProductCountByCategory() {
        List<Object[]> results = productRepository.countProductsByCategory();
        return results.stream()
            .collect(Collectors.toMap(
                result -> (String) result[0],  // category name
                result -> (Long) result[1]      // count
            ));
    }
}
