package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.Receivable;
import com.rubber.shop.mapper.ReceivableMapper;
import com.rubber.shop.service.ReceivableService;
import org.springframework.stereotype.Service;

@Service
public class ReceivableServiceImpl extends ServiceImpl<ReceivableMapper, Receivable> implements ReceivableService {
}
