package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.R;
import com.ruiji.daomin.Dish;
import com.ruiji.daomin.DishFlavor;
import com.ruiji.dto.DishDto;
import com.ruiji.service.CategoryService;
import com.ruiji.service.DishFlavorService;
import com.ruiji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @GetMapping("/page")
    public R<Page> getDish(String name, int page, int pageSize) {
        log.info("name={},page={},pageSize={}",name, page, pageSize);
        Page<Dish> dishPage = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
        if(name != null) {
            lqw.like(Dish::getName, name);
        }
        dishService.page(dishPage, lqw);
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> records1 = records.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Long id = dish.getCategoryId();
            String categoryName = categoryService.getNameById(id);
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(records1);
        return R.success(dishDtoPage);
    }
    @GetMapping("/{id}")
    public R<DishDto> queryDishById(@PathVariable long id) {
        LambdaQueryWrapper<DishFlavor> lq = new LambdaQueryWrapper<>();
        lq.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavorsList = dishFlavorService.list(lq);

        Dish dish = dishService.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavorsList);

        return R.success(dishDto);
    }
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }
    @PutMapping
    public R<String> editDish(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("success");
    }
    @PostMapping("/status/{statusCode}")
    public R<String> dishStatusByStatus(@PathVariable int statusCode, Long[] ids) {
        log.info("statusCode={},ids={}",statusCode,ids);
        List<Long> idsList = List.of(ids);
        List<Dish> dishes = dishService.listByIds(idsList);
        dishes = dishes.stream().map(dish -> {
            dish.setStatus(statusCode);
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishes);
        return R.success("");
    }
    @DeleteMapping
    public R<String> deleteDish(Long[] ids) {
        dishService.deleteWithFlavor(ids);
        return R.success("");
    }
//    @GetMapping("/list")
//    public R<List<Dish>> queryDishList(long categoryId) {
//        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(Dish::getCategoryId, categoryId);
//        List<Dish> list = dishService.list(lqw);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> queryDishList(long categoryId) {
        List<DishDto> dishDtos = dishService.listWithFlavor(categoryId);
        return R.success(dishDtos);
    }
}
