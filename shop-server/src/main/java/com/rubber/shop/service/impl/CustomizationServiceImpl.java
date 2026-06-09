package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.Customization;
import com.rubber.shop.mapper.CustomizationMapper;
import com.rubber.shop.service.CustomizationService;
import org.springframework.stereotype.Service;

@Service
public class CustomizationServiceImpl extends ServiceImpl<CustomizationMapper, Customization> implements CustomizationService {
}
