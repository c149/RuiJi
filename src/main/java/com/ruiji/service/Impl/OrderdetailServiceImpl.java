package com.ruiji.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.dao.OrderDetailDao;
import com.ruiji.dao.OrdersDao;
import com.ruiji.daomin.OrderDetail;
import com.ruiji.daomin.Orders;
import com.ruiji.service.OrderDetailService;
import com.ruiji.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrderdetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService{
}
