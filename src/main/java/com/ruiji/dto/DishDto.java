package com.ruiji.dto;

import com.ruiji.daomin.Dish;
import com.ruiji.daomin.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {//继承Dish

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
