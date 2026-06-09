package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.StockLog;
import com.rubber.shop.mapper.StockLogMapper;
import com.rubber.shop.service.StockLogService;
import org.springframework.stereotype.Service;

@Service
public class StockLogServiceImpl extends ServiceImpl<StockLogMapper, StockLog> implements StockLogService {
}
