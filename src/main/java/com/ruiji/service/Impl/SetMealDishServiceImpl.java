package com.ruiji.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.dao.SetMealDao;
import com.ruiji.dao.SetMealDishDao;
import com.ruiji.daomin.SetMeal;
import com.ruiji.daomin.SetmealDish;
import com.ruiji.dto.SetmealDto;
import com.ruiji.service.SetMealDishService;
import com.ruiji.service.SetMealService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealDishServiceImpl  extends ServiceImpl<SetMealDishDao, SetmealDish> implements SetMealDishService {
    @Override
    public void removeBySetmealId(Long id) {
        LambdaQueryWrapper<SetmealDish> lq = new LambdaQueryWrapper<>();
        lq.eq(SetmealDish::getSetmealId, id);
    }

    @Override
    public void addSetmeal(SetmealDto setmealDto) {
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(setmealDishes);
    }
}
