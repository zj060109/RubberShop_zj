package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.CategoryRequest;
import com.rubber.shop.dto.CategoryTreeResponse;
import com.rubber.shop.entity.Category;
import com.rubber.shop.entity.Product;
import com.rubber.shop.service.CategoryService;
import com.rubber.shop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping
    public Result<List<CategoryTreeResponse>> tree() {
        List<Category> all = categoryService.list();
        Map<Long, List<Category>> childrenMap = all.stream()
                .filter(c -> c.getParent_id_zj() != null && c.getParent_id_zj() > 0)
                .collect(Collectors.groupingBy(Category::getParent_id_zj));

        List<CategoryTreeResponse> roots = new ArrayList<>();
        for (Category c : all) {
            if (c.getParent_id_zj() == null || c.getParent_id_zj() == 0) {
                roots.add(buildTree(c, childrenMap));
            }
        }
        return Result.success(roots);
    }

    @PostMapping
    public Result<?> add(@RequestBody @Valid CategoryRequest req) {
        Category category = new Category();
        category.setName_zj(req.getName_zj());
        category.setParent_id_zj(req.getParent_id_zj() != null ? req.getParent_id_zj() : 0L);
        category.setSort_zj(req.getSort_zj() != null ? req.getSort_zj() : 0);
        category.setIcon_zj(req.getIcon_zj());
        categoryService.save(category);
        return Result.success("分类添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody @Valid CategoryRequest req) {
        Category exist = categoryService.getById(id);
        if (exist == null) {
            throw new BusinessException("分类不存在");
        }
        exist.setName_zj(req.getName_zj());
        if (req.getParent_id_zj() != null) exist.setParent_id_zj(req.getParent_id_zj());
        if (req.getSort_zj() != null) exist.setSort_zj(req.getSort_zj());
        if (req.getIcon_zj() != null) exist.setIcon_zj(req.getIcon_zj());
        categoryService.updateById(exist);
        return Result.success("分类修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (categoryService.getById(id) == null) {
            throw new BusinessException("分类不存在");
        }
        LambdaQueryWrapper<Category> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Category::getParent_id_zj, id);
        if (categoryService.count(childWrapper) > 0) {
            throw new BusinessException("存在子分类，无法删除");
        }
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getCategory_id_zj, id);
        if (productService.count(productWrapper) > 0) {
            throw new BusinessException("该分类下有关联商品，无法删除");
        }
        categoryService.removeById(id);
        return Result.success("分类删除成功", null);
    }

    private CategoryTreeResponse buildTree(Category category, Map<Long, List<Category>> childrenMap) {
        CategoryTreeResponse node = new CategoryTreeResponse();
        node.setId(category.getId_zj());
        node.setName(category.getName_zj());
        node.setParentId(category.getParent_id_zj());
        node.setSort(category.getSort_zj());
        node.setIcon(category.getIcon_zj());

        List<Category> children = childrenMap.get(category.getId_zj());
        if (children != null) {
            for (Category child : children) {
                node.getChildren().add(buildTree(child, childrenMap));
            }
        }
        return node;
    }
}
