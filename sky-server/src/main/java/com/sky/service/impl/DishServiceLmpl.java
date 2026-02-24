package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceLmpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @ApiOperation("添加菜品")
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //添加菜品
        dishMapper.insert(dish);
        //添加口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //获取dish_Id
        Long id = dish.getId();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> {
                flavor.setDishId(id);
            });
            dishFlavorMapper.insert(flavors);
        }
    }

    @ApiOperation("菜品分页查询")
    @Override
    public PageResult getPage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.getpage(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void delete(List<Long> ids) {
        //菜品是否可售
        ids.forEach(dishId -> {
            Dish dish = dishMapper.getByDishId(dishId);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        //菜品是否关联套餐
        List<Long> list = setmealDishMapper.getByDishId(ids);
        if(list != null && list.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品
        ids.forEach(dishId -> {
            dishMapper.delete(dishId);
            //删除口味
            dishFlavorMapper.delete(dishId);
        });
    }

    //根据id查询菜品
    @Override
    public DishVO getDishWithFlavorById(Long id) {
        //获取菜品相关属性
        Dish byDishId = dishMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(byDishId, dishVO);
        //根据id获取菜品口味
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        //获取菜品信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //删除口味信息
        dishFlavorMapper.delete(dishDTO.getId());
        //插入新的口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });
        }
    }

    @Override
    public List<Dish> getDishByCategoryId(Long categoryId) {
        Dish dish = new Dish().builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        List<Dish> dishes = dishMapper.getDishByCategoryId(dish);
        return dishes;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        //停售起售菜品
        Dish dish = new Dish().builder().id(id).status(status).build();
        dishMapper.update(dish);
        //若停售，则相应套餐也停售
        if(status == StatusConstant.DISABLE){
            List<Long> dishes = new ArrayList<>();
            dishes.add(id);
            List<Long> setmealIds = setmealDishMapper.getByDishId(dishes);
            if(setmealIds != null && setmealIds.size() > 0){
                for(Long setmealId : setmealIds){
                    Setmeal setmeal = new Setmeal().builder().id(setmealId).status(StatusConstant.ENABLE).build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.getDishByCategoryId(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
