package com.ruiji.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruiji.daomin.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookDao extends BaseMapper<AddressBook> {
}
