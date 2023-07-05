package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.daomin.SetMeal;
import com.ruiji.daomin.SetmealDish;
import com.ruiji.dto.SetmealDto;

public interface SetMealService extends IService<SetMeal> {
    void addSetMealWithDish(SetmealDto setmealDish);
    void editSetMealWithDish(SetmealDto setmealDish);
    void deleteSetMealWithWithDishs(Long[] ids);
    SetmealDto getSetMealByIdWithDish(Long id);
}
