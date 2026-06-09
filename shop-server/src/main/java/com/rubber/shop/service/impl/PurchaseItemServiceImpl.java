package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.PurchaseItem;
import com.rubber.shop.mapper.PurchaseItemMapper;
import com.rubber.shop.service.PurchaseItemService;
import org.springframework.stereotype.Service;

@Service
public class PurchaseItemServiceImpl extends ServiceImpl<PurchaseItemMapper, PurchaseItem> implements PurchaseItemService {
}
