package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
@Api("店铺状态相关接口")
public class ShopController {

    public static final String SHOP_KEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("设置店铺状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态为{}", status == 1 ? "营业中" : "已打烊");
        redisTemplate.opsForValue().set(SHOP_KEY, status);
        return Result.success();
    }

    @ApiOperation("查询店铺状态")
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_KEY);
        return Result.success(status);
    }
}
