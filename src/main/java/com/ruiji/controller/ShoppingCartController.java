package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.ruiji.common.BaseContext;
import com.ruiji.common.R;
import com.ruiji.daomin.ShoppingCart;
import com.ruiji.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.lang.invoke.LambdaConversionException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @GetMapping("/list")
    public R<List<ShoppingCart>> cartListApi() {
        Long id = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, id).orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);
    }
    @PostMapping("/add")
    public R<ShoppingCart> addCartApi(@RequestBody ShoppingCart shoppingCart) {
        Long id = BaseContext.getCurrentId();
        shoppingCart.setUserId(id);
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, id);
        if(dishId != null) {
            lqw.eq(ShoppingCart::getDishId,dishId);
        }else if(setmealId != null) {
            lqw.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart meal = shoppingCartService.getOne(lqw);
        if(meal == null) {
            meal = shoppingCart;
            meal.setNumber(1);
            meal.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }else {
            meal.setNumber(meal.getNumber() + 1);
            shoppingCartService.updateById(meal);
        }
        return R.success(meal);
    }
    @PostMapping("/sub")
    public R<String> updateCartApi(@RequestBody ShoppingCart shoppingCart) {
        Long id = BaseContext.getCurrentId();
        shoppingCart.setUserId(id);
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, id);
        if(dishId != null) {
            lqw.eq(ShoppingCart::getDishId,dishId);
        }else if(setmealId != null) {
            lqw.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart meal = shoppingCartService.getOne(lqw);
        meal.setNumber(meal.getNumber() - 1);
        shoppingCartService.updateById(meal);
        return R.success("success");
    }
    @DeleteMapping("/clean")
    public R<String> clearCartApi() {
        Long currentId = BaseContext.getCurrentId();
        QueryWrapper<ShoppingCart> qw = new QueryWrapper<>();
        qw.eq("user_id",currentId);
        shoppingCartService.remove(qw);
        return R.success("success");
    }
}
