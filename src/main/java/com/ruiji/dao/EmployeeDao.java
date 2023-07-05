package com.ruiji.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruiji.daomin.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeDao extends BaseMapper<Employee> {
}
