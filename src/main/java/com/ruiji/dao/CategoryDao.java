package com.ruiji.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruiji.daomin.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryDao extends BaseMapper<Category> {
}
