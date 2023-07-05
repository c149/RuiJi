package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.daomin.SetmealDish;
import com.ruiji.dto.SetmealDto;

public interface SetMealDishService extends IService<SetmealDish> {
    void removeBySetmealId(Long id);
    void addSetmeal(SetmealDto setmealDto);
}
