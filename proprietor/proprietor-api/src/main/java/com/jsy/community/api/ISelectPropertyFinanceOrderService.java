package com.jsy.community.api;

import com.jsy.community.entity.property.PropertyFinanceOrderEntity;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-07-05 10:54
 **/
public interface ISelectPropertyFinanceOrderService {
    /**
     * @Description: 查询物业账单
     * @author: Hu
     * @since: 2021/7/5 11:08
     * @Param: [userId]
     * @return: void
     */
    List<Map<String,Object>> list(PropertyFinanceOrderEntity qo);

}
