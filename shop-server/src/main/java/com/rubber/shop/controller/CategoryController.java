package com.rubber.shop.controller;

import com.rubber.shop.common.Result;
import com.rubber.shop.entity.Category;
import com.rubber.shop.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }
}
