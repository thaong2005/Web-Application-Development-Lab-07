package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * EXERCISE 8: STATISTICS DASHBOARD
 * Controller for displaying statistics and analytics dashboard
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    private final ProductService productService;
    
    @Autowired
    public DashboardController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Task 8.2: Show Dashboard
     * Display comprehensive statistics and analytics
     */
    @GetMapping
    public String showDashboard(Model model) {
        
        // Total products count
        long totalProducts = productService.getAllProducts().size();
        
        // Products by category (for pie chart/list)
        Map<String, Long> categoryCount = productService.getProductCountByCategory();
        
        // Total inventory value (sum of price * quantity)
        BigDecimal totalValue = productService.calculateTotalValue();
        
        // Average product price
        BigDecimal averagePrice = productService.calculateAveragePrice();
        
        // Low stock alerts (quantity < 10)
        List<Product> lowStockProducts = productService.findLowStockProducts(10);
        
        // Recent products (last 5 added - sorted by ID descending)
        List<Product> recentProducts = productService.getAllProducts(Sort.by("id").descending())
                .stream()
                .limit(5)
                .toList();
        
        // All categories for dropdown/filter
        List<String> categories = productService.getAllCategories();
        
        // Add all statistics to model
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("categoryCount", categoryCount);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("averagePrice", averagePrice);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("lowStockCount", lowStockProducts.size());
        model.addAttribute("recentProducts", recentProducts);
        model.addAttribute("categories", categories);
        
        return "dashboard";
    }
}
