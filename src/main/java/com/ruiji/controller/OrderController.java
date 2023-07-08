package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.BaseContext;
import com.ruiji.common.R;
import com.ruiji.dao.OrdersDao;
import com.ruiji.daomin.Category;
import com.ruiji.daomin.OrderDetail;
import com.ruiji.daomin.Orders;
import com.ruiji.dto.OrdersDto;
import com.ruiji.service.OrderDetailService;
import com.ruiji.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;
    @PostMapping("/submit")
    public R<String> addOrderApi(@RequestBody Orders orders) {
        ordersService.addOrderWithDetail(orders);
        return R.success("下单成功");
    }
    @GetMapping("/userPage")
    public R<Page> orderPagingApi(int page, int pageSize) {
        //查询用户订单
        QueryWrapper<Orders> qw = new QueryWrapper<>();
        qw.eq("user_id", BaseContext.getCurrentId()).orderByDesc("order_time");
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        ordersService.page(ordersPage,qw);
        List<Orders> ordersList = ordersPage.getRecords();
        //遍历订单并封装成ordersDto
        List<OrdersDto> ordersDtoList = ordersList.stream().map(order -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(order, ordersDto);
            String number = order.getNumber();
            QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
            orderDetailQueryWrapper.eq("order_id", number);
            List<OrderDetail> orderDetails = orderDetailService.list(orderDetailQueryWrapper);
            ordersDto.setOrderDetails(orderDetails);
            return ordersDto;
        }).collect(Collectors.toList());
        //创建新page并复制除record外的数据
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        //设置records
        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }
    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize) {
        Page pagec = new Page(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Orders::getOrderTime);
        ordersService.page(pagec, queryWrapper);
        return R.success(pagec);
    }

}
