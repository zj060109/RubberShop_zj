package com.rubber.shop.controller;

import com.rubber.shop.common.Result;
import com.rubber.shop.entity.Product;
import com.rubber.shop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<List<Product>> list() {
        return Result.success(productService.list());
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }
}
