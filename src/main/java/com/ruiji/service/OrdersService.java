package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.dao.OrdersDao;
import com.ruiji.daomin.Orders;

import javax.imageio.spi.IIOServiceProvider;

public interface OrdersService extends IService<Orders> {
    void addOrderWithDetail(Orders orders);
}
