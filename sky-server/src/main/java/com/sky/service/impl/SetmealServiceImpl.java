package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类业务层
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private View error;
    @Autowired
    private DishMapper dishMapper;

    @Transactional
    @Override
    public void save(SetmealDTO setmealDTO) {
        //新增套餐基础信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.save(setmeal);
        Long id = setmeal.getId();
        //新增套餐相关菜品
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        dishes.forEach(dish -> {
            dish.setSetmealId(id);
        });
        //套餐和菜品关联信息
        setmealDishMapper.insertBatch(dishes);
    }

    @Override
    //根据id查询套餐
    public SetmealVO getSetmealById(Long id) {
        SetmealVO setmealById = setmealMapper.getSetmealById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealById.setSetmealDishes(setmealDishes);
        return setmealById;
    }

    @Override
    public PageResult getQueryPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> setmealVOS = setmealMapper.getQueryPage(setmealPageQueryDTO);
        return new PageResult(setmealVOS.getTotal(),setmealVOS.getResult());
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() != StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        for(Long id : ids){
            setmealMapper.delete(id);
            setmealDishMapper.deleteBySetmealId(id);
        }
    }

    @Override
    //修改套餐
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if(setmealDishList != null && setmealDishList.size() > 0){
            for (SetmealDish setmealDish : setmealDishList) {
                setmealDish.setSetmealId(setmeal.getId());
            }
            setmealDishMapper.insertBatch(setmealDishList);
        }
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        if(status == StatusConstant.ENABLE){
            //判断菜品是否有停售，若菜品停售则抛出异常
            List<Dish> dishes = dishMapper.getBySetmealId(id);
            for(Dish dish : dishes){
                if(dish.getStatus() == StatusConstant.DISABLE){
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = new Setmeal().builder().id(id).status(status).build();
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
