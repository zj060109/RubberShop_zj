package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.entity.Product;
import com.rubber.shop.entity.StockLog;
import com.rubber.shop.service.ProductService;
import com.rubber.shop.service.StockLogService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockLogService stockLogService;
    private final ProductService productService;

    public StockController(StockLogService sls, ProductService ps) {
        this.stockLogService = sls; this.productService = ps;
    }

    @GetMapping("/logs")
    public Result<Page<StockLog>> logs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String type) {
        LambdaQueryWrapper<StockLog> w = new LambdaQueryWrapper<>();
        if (productId != null) w.eq(StockLog::getProduct_id_zj, productId);
        if (type != null && !type.isEmpty()) w.eq(StockLog::getType_zj, type);
        w.orderByDesc(StockLog::getCreated_at_zj);
        return Result.success(stockLogService.page(new Page<>(page, pageSize), w));
    }

    @PostMapping("/adjust")
    @Transactional
    public Result<?> adjust(@RequestParam Long productId, @RequestParam int quantity, @RequestParam String type) {
        if (quantity <= 0) throw new BusinessException("调整数量必须大于0");
        if (!"manual_in".equals(type) && !"manual_out".equals(type))
            throw new BusinessException("类型仅支持 manual_in 或 manual_out");

        Product p = productService.getById(productId);
        if (p == null) throw new BusinessException("商品不存在");

        int change = "manual_out".equals(type) ? -quantity : quantity;
        Integer currentStock = p.getStock_zj();
        if (currentStock == null) currentStock = 0;
        if ("manual_out".equals(type) && currentStock < quantity)
            throw new BusinessException("库存不足，当前库存：" + p.getStock_zj());

        LambdaUpdateWrapper<Product> pw = new LambdaUpdateWrapper<>();
        pw.eq(Product::getId_zj, productId)
          .setSql("stock_zj = stock_zj + {0}", change);
        productService.update(pw);

        Product updated = productService.getById(productId);
        StockLog sl = new StockLog();
        sl.setProduct_id_zj(productId);
        sl.setChange_quantity_zj(change);
        sl.setCurrent_stock_zj(updated.getStock_zj());
        sl.setType_zj(type);
        sl.setRemark_zj("手动调整");
        sl.setCreated_at_zj(LocalDateTime.now());
        stockLogService.save(sl);
        return Result.success("调整成功", null);
    }

    @GetMapping("/warnings")
    public Result<List<Product>> warnings() {
        List<Product> list = productService.list(new LambdaQueryWrapper<Product>().eq(Product::getStatus_zj, "on"));
        list.removeIf(p -> {
            Integer stock = p.getStock_zj();
            if (stock == null) stock = 0;
            int threshold = p.getWarning_stock_zj() != null && p.getWarning_stock_zj() > 0
                    ? p.getWarning_stock_zj() : 10;
            return stock > threshold;
        });
        return Result.success(list);
    }
}
