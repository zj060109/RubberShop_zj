package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.ProductRequest;
import com.rubber.shop.entity.OrderItem;
import com.rubber.shop.entity.Product;
import com.rubber.shop.entity.PurchaseItem;
import com.rubber.shop.service.OrderItemService;
import com.rubber.shop.service.ProductService;
import com.rubber.shop.service.PurchaseItemService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final OrderItemService orderItemService;
    private final PurchaseItemService purchaseItemService;
    private final ObjectMapper objectMapper;

    public ProductController(ProductService productService, OrderItemService orderItemService,
            PurchaseItemService purchaseItemService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.orderItemService = orderItemService;
        this.purchaseItemService = purchaseItemService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public Result<Page<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName_zj, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategory_id_zj, categoryId);
        }

        boolean isMerchant = isAdmin();
        if (!isMerchant) {
            wrapper.eq(Product::getStatus_zj, "on");
        }

        wrapper.orderByDesc(Product::getCreated_at_zj);
        return Result.success(productService.page(new Page<>(page, pageSize), wrapper));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!isAdmin() && !"on".equals(product.getStatus_zj())) {
            throw new BusinessException("商品已下架");
        }
        return Result.success(product);
    }

    @PostMapping
    public Result<?> create(@RequestBody @Valid ProductRequest req) {
        Product product = new Product();
        product.setCategory_id_zj(req.getCategoryId());
        product.setName_zj(req.getName());
        product.setDescription_zj(req.getDescription());
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            try {
                product.setImages_zj(objectMapper.writeValueAsString(req.getImages()));
            } catch (JsonProcessingException e) {
                throw new BusinessException("图片数据格式错误");
            }
        }
        product.setPrice_zj(req.getPrice());
        product.setStock_zj(req.getStock() != null ? req.getStock() : 0);
        product.setWarning_stock_zj(req.getWarningStock() != null ? req.getWarningStock() : 10);
        product.setStatus_zj("on");
        product.setFactory_id_zj(req.getFactoryId());
        product.setCreated_at_zj(LocalDateTime.now());
        productService.save(product);
        return Result.success("商品添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ProductRequest req) {
        Product product = productService.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (req.getCategoryId() != null) product.setCategory_id_zj(req.getCategoryId());
        if (req.getName() != null) {
            if (req.getName().isEmpty()) throw new BusinessException("商品名称不能为空");
            product.setName_zj(req.getName());
        }
        if (req.getDescription() != null) product.setDescription_zj(req.getDescription());
        if (req.getImages() != null) {
            try {
                product.setImages_zj(objectMapper.writeValueAsString(req.getImages()));
            } catch (JsonProcessingException e) {
                throw new BusinessException("图片数据格式错误");
            }
        }
        if (req.getPrice() != null) {
            if (req.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) throw new BusinessException("价格必须大于0");
            product.setPrice_zj(req.getPrice());
        }
        if (req.getStock() != null) {
            if (req.getStock() < 0) throw new BusinessException("库存不能为负数");
            product.setStock_zj(req.getStock());
        }
        if (req.getWarningStock() != null) {
            if (req.getWarningStock() < 0) throw new BusinessException("预警阈值不能为负数");
            product.setWarning_stock_zj(req.getWarningStock());
        }
        if (req.getFactoryId() != null) product.setFactory_id_zj(req.getFactoryId());
        product.setUpdated_at_zj(LocalDateTime.now());
        productService.updateById(product);
        return Result.success("商品修改成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<?> toggleStatus(@PathVariable Long id, @RequestParam String status) {
        if (!"on".equals(status) && !"off".equals(status)) {
            throw new BusinessException("状态值无效，仅支持 on 或 off");
        }
        Product product = productService.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus_zj(status);
        product.setUpdated_at_zj(LocalDateTime.now());
        productService.updateById(product);
        return Result.success("状态更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        long orderRefs = orderItemService.count(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getProduct_id_zj, id));
        long purchaseRefs = purchaseItemService.count(new LambdaQueryWrapper<PurchaseItem>().eq(PurchaseItem::getProduct_id_zj, id));
        if (orderRefs > 0 || purchaseRefs > 0) {
            throw new BusinessException("商品已被订单或采购单引用，无法删除");
        }
        productService.removeById(id);
        return Result.success("商品删除成功", null);
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_MERCHANT".equals(role)) {
                return true;
            }
        }
        return false;
    }
}
