package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.entity.SysConfig;
import com.rubber.shop.service.SysConfigService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/{key}")
    @Transactional
    public Result<?> update(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        if (value == null) throw new BusinessException("配置值不能为空");

        LambdaQueryWrapper<SysConfig> w = new LambdaQueryWrapper<>();
        w.eq(SysConfig::getConfig_key_zj, key);
        SysConfig config = sysConfigService.getOne(w);
        if (config == null) throw new BusinessException("配置项不存在");

        config.setConfig_value_zj(value);
        if (body.containsKey("remark")) config.setRemark_zj(body.get("remark"));
        config.setUpdated_at_zj(LocalDateTime.now());
        sysConfigService.updateById(config);
        return Result.success("修改成功", null);
    }
}
