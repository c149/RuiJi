package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.daomin.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
    String getNameById(Long id);
}
