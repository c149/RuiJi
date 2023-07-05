package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.daomin.Dish;
import com.ruiji.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);
    void updateWithFlavor(DishDto dishDto);
    void deleteWithFlavor(Long[] ids);
    List<DishDto> listWithFlavor(long categoryId);
}
