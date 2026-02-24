package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api("菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("添加菜品")
    public Result saveWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("添加菜品");
        dishService.saveWithFlavor(dishDTO);
        String key = "dish:" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> getPage(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询");
        PageResult pageResult = dishService.getPage(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result deleteWithFlavor(@RequestParam List<Long> ids) {
        log.info("删除菜品");
        dishService.delete(ids);
        clearCache("dish:*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getDishWithFlavorById(@PathVariable Long id) {
        log.info("根据菜品id{}查询菜品",id);
        DishVO dishVOResult = dishService.getDishWithFlavorById(id);
        return Result.success(dishVOResult);
    }

    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息");
        dishService.update(dishDTO);
        clearCache("dish:*");
        return Result.success();
    }

    //根据分类id查询菜品
    @GetMapping("list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getDishByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品");
        List<Dish> dishes = dishService.getDishByCategoryId(categoryId);
        return Result.success(dishes);
    }

    //菜品起售停售
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("菜品起售停售");
        dishService.startOrStop(status,id);
        clearCache("dish:*");
        return Result.success();
    }

    private void clearCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}