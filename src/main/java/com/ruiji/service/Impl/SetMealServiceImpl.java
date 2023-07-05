package com.ruiji.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.dao.SetMealDao;
import com.ruiji.daomin.DishFlavor;
import com.ruiji.daomin.SetMeal;
import com.ruiji.daomin.SetmealDish;
import com.ruiji.dto.SetmealDto;
import com.ruiji.service.SetMealDishService;
import com.ruiji.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealDao, SetMeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;
    @Override
    @Transactional
    public void addSetMealWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        setMealDishService.addSetmeal(setmealDto);
    }

    @Override
    @Transactional
    public void editSetMealWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        //del
        setMealDishService.removeBySetmealId(setmealDto.getId());
        //add
        setMealDishService.addSetmeal(setmealDto);
    }

    @Override
    public void deleteSetMealWithWithDishs(Long[] ids) {
        List<Long> idsList = List.of(ids);
        this.removeBatchByIds(idsList);
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId,idsList);
        setMealDishService.remove(lqw);
    }

    @Override
    public SetmealDto getSetMealByIdWithDish(Long id) {
        SetMeal setMeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setMeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> lq = new LambdaQueryWrapper<>();
        lq.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setMealDishService.list(lq);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }
}
