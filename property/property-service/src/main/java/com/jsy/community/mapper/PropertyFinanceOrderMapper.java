package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:  物业缴费账单
 * @author: Hu
 * @create: 2021-04-20 15:56
 **/
public interface PropertyFinanceOrderMapper extends BaseMapper<PropertyFinanceOrderEntity> {
    /**
     * @Description: 查询认证过的所有id集合
     * @author: Hu
     * @since: 2021/4/21 13:34
     * @Param:
     * @return:
     */
    List<Long> communityIdList();

    /**
     * @Description:
     * @author: Hu
     * @since: 2021/4/21 14:04
     * @Param:
     * @return:
     */
    List<HouseEntity> selectHouseAll(Long id);
}
