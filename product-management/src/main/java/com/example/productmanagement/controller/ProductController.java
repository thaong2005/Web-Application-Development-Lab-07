package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // EXERCISE 7: SORTING & FILTERING
    // List all products with sorting and filtering
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category,
            Model model) {
        
        List<Product> products;
        
        // Task 7.3: Combined Sorting and Filtering
        // Apply category filter first
        if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
            
            // Then apply sorting if specified
            if (sortBy != null && !sortBy.isEmpty()) {
                Sort sort = sortDir.equals("asc") ? 
                    Sort.by(sortBy).ascending() : 
                    Sort.by(sortBy).descending();
                // Sort the filtered list manually (since we already filtered)
                products = productService.getAllProducts(sort).stream()
                    .filter(p -> p.getCategory().equals(category))
                    .toList();
            }
        } else {
            // No category filter, just apply sorting
            if (sortBy != null && !sortBy.isEmpty()) {
                Sort sort = sortDir.equals("asc") ? 
                    Sort.by(sortBy).ascending() : 
                    Sort.by(sortBy).descending();
                products = productService.getAllProducts(sort);
            } else {
                products = productService.getAllProducts();
            }
        }
        
        List<String> categories = productService.getAllCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("selectedCategory", category);
        
        return "product-list";  // Returns product-list.html
    }
    
    // Show form for new product
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "product-form";
    }
    
    // Show form for editing product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }
    
    // Save product (create or update)
    // EXERCISE 6: VALIDATION
    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (result.hasErrors()) {
            // Return to form with error messages
            return "product-form";
        }
        
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("message", 
                    product.getId() == null ? "Product added successfully!" : "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving product: " + e.getMessage());
        }
        return "redirect:/products";
    }
    
    // Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }
    
    // Search products (Basic - without pagination)
    @GetMapping("/search-basic")
    public String searchProductsBasic(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchProducts(keyword);
        List<String> categories = productService.getAllCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        return "product-list";
    }
    
    // EXERCISE 5: ADVANCED SEARCH
    
    // Task 5.1: Multi-Criteria Search
    @GetMapping("/advanced-search")
    public String advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {
        
        List<Product> products = productService.advancedSearch(name, category, minPrice, maxPrice);
        List<String> categories = productService.getAllCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("searchName", name);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        
        return "product-list";
    }
    
    // Task 5.2: Category Filter (integrated with advanced search)
    @GetMapping("/filter")
    public String filterByCategory(@RequestParam(required = false) String category, Model model) {
        List<Product> products;
        
        if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllProducts();
        }
        
        List<String> categories = productService.getAllCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        
        return "product-list";
    }
    
    // Task 5.3: Search with Pagination
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.searchProducts(keyword, pageable);
        List<String> categories = productService.getAllCategories();
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        
        return "product-list";
    }
}
