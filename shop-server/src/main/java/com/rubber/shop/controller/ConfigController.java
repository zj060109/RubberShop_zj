package com.rubber.shop.controller;

import com.rubber.shop.common.Result;
import com.rubber.shop.entity.SysConfig;
import com.rubber.shop.service.SysConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configs")
public class ConfigController {

    private final SysConfigService sysConfigService;

    public ConfigController(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    @GetMapping
    public Result<List<SysConfig>> list() {
        return Result.success(sysConfigService.list());
    }
}
