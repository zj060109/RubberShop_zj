package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.Receipt;
import com.rubber.shop.mapper.ReceiptMapper;
import com.rubber.shop.service.ReceiptService;
import org.springframework.stereotype.Service;

@Service
public class ReceiptServiceImpl extends ServiceImpl<ReceiptMapper, Receipt> implements ReceiptService {
}
