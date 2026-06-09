package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.CustomizationItem;
import com.rubber.shop.mapper.CustomizationItemMapper;
import com.rubber.shop.service.CustomizationItemService;
import org.springframework.stereotype.Service;

@Service
public class CustomizationItemServiceImpl extends ServiceImpl<CustomizationItemMapper, CustomizationItem> implements CustomizationItemService {
}
