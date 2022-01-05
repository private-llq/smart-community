package com.jsy.community.vo;

import com.jsy.community.entity.UserLivingExpensesOrderEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2022/1/5 16:42
 * @Version: 1.0
 **/
@Data
public class LivingExpensesOrderListVO implements Serializable {
    private String dateString;
    private List<UserLivingExpensesOrderEntity> orderEntityList;

}
