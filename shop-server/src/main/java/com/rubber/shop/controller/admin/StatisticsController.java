package com.rubber.shop.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rubber.shop.common.Result;
import com.rubber.shop.entity.Order;
import com.rubber.shop.entity.Product;
import com.rubber.shop.service.OrderService;
import com.rubber.shop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    private final OrderService orderService;
    private final ProductService productService;

    public StatisticsController(OrderService os, ProductService ps) {
        this.orderService = os; this.productService = ps;
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();

        LambdaQueryWrapper<Order> ow = new LambdaQueryWrapper<>();
        ow.eq(Order::getStatus_zj, "completed");
        List<Order> completedOrders = orderService.list(ow);

        BigDecimal totalSales = BigDecimal.ZERO;
        int totalOrders = completedOrders.size();
        for (Order o : completedOrders) totalSales = totalSales.add(o.getActual_amount_zj());

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<Order> tw = new LambdaQueryWrapper<>();
        tw.ge(Order::getCreated_at_zj, today).eq(Order::getStatus_zj, "completed");
        int todayOrders = (int) orderService.count(tw);

        LambdaQueryWrapper<Product> pw = new LambdaQueryWrapper<>();
        pw.eq(Product::getStatus_zj, "on")
          .le(Product::getStock_zj, 10);
        int warningCount = (int) productService.count(pw);

        data.put("totalSales", totalSales);
        data.put("totalOrders", totalOrders);
        data.put("todayOrders", todayOrders);
        data.put("warningCount", warningCount);

        return Result.success(data);
    }
}
