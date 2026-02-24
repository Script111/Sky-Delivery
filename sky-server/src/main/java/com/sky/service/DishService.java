package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    public void saveWithFlavor(DishDTO dishDTO);

    PageResult getPage(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    DishVO getDishWithFlavorById(Long id);

    void update(DishDTO dishDTO);

    List<Dish> getDishByCategoryId(Long categoryId);

    void startOrStop(Integer status, Long id);

    List<DishVO> listWithFlavor(Dish dish);
}
