package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.R;
import com.ruiji.daomin.Category;
import com.ruiji.daomin.SetMeal;
import com.ruiji.service.CategoryService;
import com.ruiji.service.DishService;
import com.ruiji.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> getCategoryPage(int page, int pageSize) {
        Page pagec = new Page(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pagec, queryWrapper);
        return R.success(pagec);
    }
    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        boolean save = categoryService.save(category);
        if(save) return R.success("");
        else return R.error("error");
    }
    @PutMapping
    public R<String> editCategory(@RequestBody Category category) {
        boolean b = categoryService.updateById(category);
        if(b) return R.success("");
        else return R.error("error");
    }

    @DeleteMapping
    public R<String> delCategory(long id) {
        boolean b = categoryService.removeById(id);
        if(b) return R.success("");
        else return R.error("error");
    }

    @GetMapping("/list")
    public R<IPage> dishList(int type) {
        IPage<Category> categoryIPage = new Page<>();
        LambdaQueryWrapper<Category> lq = new LambdaQueryWrapper<>();
        lq.eq(Category::getType, type);
        lq.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        categoryService.page(categoryIPage, lq);
        return R.success(categoryIPage);
    }
    @GetMapping("/rlist")
    public R<List<Category>> dishList1(Integer type) {
        LambdaQueryWrapper<Category> lq = new LambdaQueryWrapper<>();
        if (type != null) lq.eq(Category::getType, type);
        lq.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lq);
        return R.success(list);
    }
}
