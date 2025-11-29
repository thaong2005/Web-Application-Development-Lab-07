package com.example.productmanagement.repository;

import com.example.productmanagement.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Spring Data JPA generates implementation automatically!
    
    // Custom query methods (derived from method names)
    List<Product> findByCategory(String category);
    
    List<Product> findByNameContaining(String keyword);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByCategoryOrderByPriceAsc(String category);
    
    boolean existsByProductCode(String productCode);
    
    // EXERCISE 5: ADVANCED SEARCH
    
    // Task 5.1: Multi-Criteria Search
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR p.name LIKE %:name%) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> searchProducts(@Param("name") String name,
                                @Param("category") String category,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice);
    
    // Task 5.2: Category Filter
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();
    
    // Task 5.3: Search with Pagination
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
    
    // EXERCISE 8: STATISTICS DASHBOARD
    
    // Task 8.1: Statistics Methods
    
    // Count products by category
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    long countByCategory(@Param("category") String category);
    
    // Calculate total inventory value (price * quantity for all products)
    @Query("SELECT SUM(p.price * p.quantity) FROM Product p")
    BigDecimal calculateTotalValue();
    
    // Calculate average product price
    @Query("SELECT AVG(p.price) FROM Product p")
    BigDecimal calculateAveragePrice();
    
    // Find products with low stock (quantity below threshold)
    @Query("SELECT p FROM Product p WHERE p.quantity < :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
    
    // Get category counts (additional helper for dashboard)
    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countProductsByCategory();
    
    // All basic CRUD methods inherited from JpaRepository:
    // - findAll()
    // - findById(Long id)
    // - save(Product product)
    // - deleteById(Long id)
    // - count()
    // - existsById(Long id)
}
