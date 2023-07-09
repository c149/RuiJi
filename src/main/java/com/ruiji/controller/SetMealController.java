package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.R;
import com.ruiji.daomin.Dish;
import com.ruiji.daomin.SetMeal;
import com.ruiji.daomin.SetmealDish;
import com.ruiji.dto.SetmealDto;
import com.ruiji.service.CategoryService;
import com.ruiji.service.SetMealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Api(tags = "套餐相关接口")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true),
            @ApiImplicitParam(name = "name", value = "查询信息", required = false)
    })
    public R<Page> getSetmealPage(String name, int page, int pageSize){
        Page<SetMeal> page1 = new Page<>(page, pageSize);
        LambdaQueryWrapper<SetMeal> lqw = new LambdaQueryWrapper<>();
        if(name != null)  lqw.like(SetMeal::getName, name);
        setMealService.page(page1, lqw);
        Page<SetmealDto> page2 = new Page<>();
        BeanUtils.copyProperties(page1, page2, "records");
        List<SetMeal> setMeals = page1.getRecords();
        List<SetmealDto> setmealDtos = setMeals.stream().map(setMeal -> {
            Long categoryId = setMeal.getCategoryId();
            String nameById = categoryService.getNameById(categoryId);
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setMeal, setmealDto);
            setmealDto.setCategoryName(nameById);
            return setmealDto;
        }).collect(Collectors.toList());
        page2.setRecords(setmealDtos);
        return R.success(page2);
    }
    @GetMapping("/{id}")
    public R<SetmealDto> querySetmealById(@PathVariable Long id) {
        SetmealDto setMealByIdWithDish = setMealService.getSetMealByIdWithDish(id);
        return R.success(setMealByIdWithDish);
    }
    @PostMapping
    @CacheEvict(value = "setmeal" , allEntries = true)
    @ApiOperation(value = "新增套餐")
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto) {
        setMealService.addSetMealWithDish(setmealDto);
        return R.success("success");
    }
    @PutMapping
    @CacheEvict(value = "setmeal" , allEntries = true)
    public R<String> editSetmeal(@RequestBody SetmealDto setmealDto) {
        setMealService.editSetMealWithDish(setmealDto);
        return R.success("success");
    }
    @PostMapping("/status/{statusCode}")
    public R<String> setmealStatusByStatus(@PathVariable int statusCode, Long[] ids) {
        List<Long> idsList = List.of(ids);
        List<SetMeal> setMeals = setMealService.listByIds(idsList);
        setMeals = setMeals.stream().map(setMeal -> {
            setMeal.setStatus(statusCode);
            return setMeal;
        }).collect(Collectors.toList());
        setMealService.updateBatchById(setMeals);
        return R.success("");
    }
    @DeleteMapping
    @CacheEvict(value = "setmeal" , allEntries = true)
    public R<String> deleteSetmeal(Long[] ids) {
        setMealService.deleteSetMealWithWithDishs(ids);
        return R.success("");
    }
    @GetMapping("/list")
    @Cacheable(value = "setmeal" , key = "#categoryId+'_'+#status")
    public R<List<SetMeal>> listSetMeal(Long categoryId, Integer status) {
        if(categoryId == null || status == null) return R.error(null);
        LambdaQueryWrapper<SetMeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetMeal::getCategoryId,categoryId).eq(SetMeal::getStatus,status).orderByDesc(SetMeal::getUpdateTime);
        List<SetMeal> list = setMealService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
