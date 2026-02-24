package com.sky.controller.admin;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api("套餐接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    //新增套餐
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐");
        setmealService.save(setmealDTO);
        return Result.success();
    }
    //根据id查询套餐
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id) {
        log.info("根据id查询套餐");
        SetmealVO setmealVO = setmealService.getSetmealById(id);
        return Result.success(setmealVO);
    }
    //分页查询套餐
    @GetMapping("/page")
    @ApiOperation("套餐分页查询接口")
    public Result<PageResult> getPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐");
        PageResult pageResult = setmealService.getQueryPage(setmealPageQueryDTO);
        return Result.success(pageResult);
    }
    //批量删除套餐
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐");
        setmealService.delete(ids);
        return Result.success();
    }
    //修改套餐
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐");
        setmealService.update(setmealDTO);
        return Result.success();
    }
    //套餐起售停售
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result updateStatus(@PathVariable Integer status,Long id) {
        log.info("套餐起售停售");
        setmealService.updateStatus(status,id);
        return Result.success();
    }
}