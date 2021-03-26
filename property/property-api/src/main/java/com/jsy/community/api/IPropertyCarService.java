package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CommunityFunQO;
import com.jsy.community.utils.PageInfo;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 15:48
 **/
public interface IPropertyCarService extends IService<CarEntity> {
    /**
     * @Description: 查询所有车辆信息
     * @author: Hu
     * @since: 2021/3/22 15:56
     * @Param:
     * @return:
     */
    PageInfo findList(BaseQO<CommunityFunQO> baseQO);

    void insert();
}
