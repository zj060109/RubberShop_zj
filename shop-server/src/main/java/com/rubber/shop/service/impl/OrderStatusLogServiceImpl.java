package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.OrderStatusLog;
import com.rubber.shop.mapper.OrderStatusLogMapper;
import com.rubber.shop.service.OrderStatusLogService;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusLogServiceImpl extends ServiceImpl<OrderStatusLogMapper, OrderStatusLog> implements OrderStatusLogService {
}
