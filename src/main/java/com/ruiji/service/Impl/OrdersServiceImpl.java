package com.ruiji.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.common.BaseContext;
import com.ruiji.common.CustomException;
import com.ruiji.dao.OrdersDao;
import com.ruiji.daomin.*;
import com.ruiji.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Override
    @Transactional
    public void addOrderWithDetail(Orders orders) {
        Long currentId = BaseContext.getCurrentId();
        orders.setUserId(currentId);//用户id
        long orderId = IdWorker.getId();
        orders.setNumber("" + orderId);//订单号
        orders.setOrderTime(LocalDateTime.now());//订单创建时间
        orders.setCheckoutTime(LocalDateTime.now());//订单支付时间
        orders.setStatus(2);//待派送
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lqw);//detail
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空");
        }
        User user = userService.getById(currentId);//username
        orders.setUserName(user.getName());
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());//phone,address,consignee
        if (addressBook == null) {
            throw new CustomException("地址有误");
        }
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAmount(countSum(shoppingCarts, orderId));
        //全部设置完成后清空购物车
        shoppingCartService.remove(lqw);
        this.save(orders);
    }
    public BigDecimal countSum(List<ShoppingCart> shoppingCarts, long orderId) {
        BigDecimal sum = new BigDecimal("0");
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart item : shoppingCarts) {
            //设置OrderDetail属性
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            orderDetails.add(orderDetail);
            //计算值
            BigDecimal amount = item.getAmount();
            BigDecimal number = BigDecimal.valueOf(item.getNumber());
            BigDecimal mul = amount.multiply(number);
//            System.out.println(mul);
            sum = sum.add(mul);
//            System.out.println(sum);
        }
        orderDetailService.saveBatch(orderDetails);
        return sum;
    }
}
