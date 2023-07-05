package com.ruiji.dto;

import com.ruiji.daomin.SetMeal;
import com.ruiji.daomin.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends SetMeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
