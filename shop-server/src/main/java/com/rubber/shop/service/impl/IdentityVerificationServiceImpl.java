package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.IdentityVerification;
import com.rubber.shop.mapper.IdentityVerificationMapper;
import com.rubber.shop.service.IdentityVerificationService;
import org.springframework.stereotype.Service;

@Service
public class IdentityVerificationServiceImpl extends ServiceImpl<IdentityVerificationMapper, IdentityVerification> implements IdentityVerificationService {
}
