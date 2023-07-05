package com.ruiji.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.common.CustomException;
import com.ruiji.dao.CategoryDao;
import com.ruiji.dao.EmployeeDao;
import com.ruiji.daomin.Category;
import com.ruiji.daomin.Dish;
import com.ruiji.daomin.Employee;
import com.ruiji.daomin.SetMeal;
import com.ruiji.service.CategoryService;
import com.ruiji.service.DishService;
import com.ruiji.service.EmployeeService;
import com.ruiji.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;
    @Override
    public boolean removeById(Serializable id) {
        log.info("SerializableId={}",id);
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper();
        LambdaQueryWrapper<SetMeal> qw1 = new LambdaQueryWrapper();
        qw.eq(Dish::getCategoryId, id);
        qw1.eq(SetMeal::getCategoryId, id);
        long count = dishService.count(qw), count1 = setMealService.count(qw1);
        if(count > 0) throw new CustomException("关联了菜品");
        if(count1 > 0) throw new CustomException("关联了套餐");
//        return true;
        return super.removeById(id);
    }

    @Override
    public void remove(Long id) {}

    @Override
    public String getNameById(Long id) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        Category one = this.getOne(queryWrapper);
        if(one == null) return null;
        return one.getName();
    }
    //extends ServiceImpl<EmployeeDao, Employee> implements EmployeeService
}
