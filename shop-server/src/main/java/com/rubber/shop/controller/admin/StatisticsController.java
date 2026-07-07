package com.rubber.shop.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rubber.shop.common.AuthUtils;
import com.rubber.shop.common.Result;
import com.rubber.shop.entity.Order;
import com.rubber.shop.entity.Product;
import com.rubber.shop.service.OrderService;
import com.rubber.shop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
        AuthUtils.requireMerchant();
        Map<String, Object> data = new HashMap<>();

        QueryWrapper<Order> sumWrapper = new QueryWrapper<>();
        sumWrapper.select("COALESCE(SUM(actual_amount_zj), 0) as totalSales")
                 .eq("status_zj", "completed");
        Map<String, Object> sumResult = orderService.getMap(sumWrapper);
        BigDecimal totalSales = BigDecimal.ZERO;
        if (sumResult != null && sumResult.get("totalSales") != null) {
            totalSales = new BigDecimal(sumResult.get("totalSales").toString());
        }

        long totalOrders = orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getStatus_zj, "completed"));

        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        long todayOrders = orderService.count(new LambdaQueryWrapper<Order>().ge(Order::getCreated_at_zj, todayStart));

        long warningCount = productService.count(new QueryWrapper<Product>()
                .eq("status_zj", "on")
                .apply("stock_zj <= COALESCE(warning_stock_zj, 10)"));

        data.put("totalSales", totalSales);
        data.put("totalOrders", totalOrders);
        data.put("todayOrders", todayOrders);
        data.put("warningCount", warningCount);

        java.util.List<BigDecimal> last7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = todayStart.minusDays(i);
            LocalDateTime end = start.plusDays(1);
            QueryWrapper<Order> dw = new QueryWrapper<>();
            dw.select("COALESCE(SUM(actual_amount_zj), 0) as daySales")
              .eq("status_zj", "completed")
              .ge("created_at_zj", start)
              .lt("created_at_zj", end);
            Map<String, Object> dayResult = orderService.getMap(dw);
            BigDecimal daySales = BigDecimal.ZERO;
            if (dayResult != null && dayResult.get("daySales") != null) {
                daySales = new BigDecimal(dayResult.get("daySales").toString());
            }
            last7Days.add(daySales);
        }
        data.put("last7DaysSales", last7Days);

        return Result.success(data);
    }
}
