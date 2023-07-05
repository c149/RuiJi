package com.ruiji.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruiji.daomin.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartDao extends BaseMapper<ShoppingCart> {
}
