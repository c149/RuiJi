package com.ruiji.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.dao.DishDao;
import com.ruiji.daomin.Dish;
import com.ruiji.daomin.DishFlavor;
import com.ruiji.dto.DishDto;
import com.ruiji.service.DishFlavorService;
import com.ruiji.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        String key = "dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
//        Set keys = redisTemplate.keys("*");
//        redisTemplate.delete(keys);  //清理所有key
        String key = "dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);
        this.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());
        //移除原先
        dishFlavorService.remove(lqw);
        List<DishFlavor> flavors = dishDto.getFlavors();
        //设置id
        flavors = flavors.stream().map(flavor -> {
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());
        //保存
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void deleteWithFlavor(Long[] ids) {
        List<Long> idsList = List.of(ids);
        this.removeBatchByIds(idsList);
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.in(DishFlavor::getDishId,idsList);
        dishFlavorService.remove(lqw);
    }

    @Override
    public List<DishDto> listWithFlavor(long categoryId) {
        List<DishDto> dishDtos = null;
        String key = "dish_" + categoryId;
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtos != null) {
            return dishDtos;
        }
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId, categoryId);
        List<Dish> list = this.list(lqw);
        dishDtos =list.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
            flavorWrapper.eq(DishFlavor::getDishId,dish.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(flavorWrapper);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key, dishDtos, 60, TimeUnit.MINUTES);
        return dishDtos;
    }
}
