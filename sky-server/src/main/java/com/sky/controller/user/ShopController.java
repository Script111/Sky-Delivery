package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController")
@Slf4j
@RequestMapping("/user/shop")
@Api("用户端查询店铺状态接口")
public class ShopController {

    @Autowired
    RedisTemplate redisTemplate;
    public static final String SHOP_KEY = "SHOP_STATUS";

    @ApiOperation("查询店铺状态")
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_KEY);
        return Result.success(status);
    }
}
