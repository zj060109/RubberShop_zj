package com.rubber.shop.dto;

import com.rubber.shop.entity.Order;
import com.rubber.shop.entity.OrderItem;
import com.rubber.shop.entity.OrderStatusLog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderDetailResponse {

    private Order order;
    private List<OrderItem> items;
    private List<OrderStatusLog> statusLogs;
}
