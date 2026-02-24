package com.sky.mapper;

import java.util.List;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishFlavorMapper {

    void insert(List<DishFlavor> dishFlavor);

    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void delete(Long dishId);

    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
