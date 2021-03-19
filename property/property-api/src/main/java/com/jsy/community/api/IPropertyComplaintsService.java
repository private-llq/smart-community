package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PropertyComplaintsEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.utils.PageInfo;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-19 11:19
 **/
public interface IPropertyComplaintsService extends IService<PropertyComplaintsEntity> {
    /**
     * @Description: 分页查询物业投诉接口
     * @author: Hu
     * @since: 2021/3/19 14:05
     * @Param:
     * @return:
     */
    PageInfo findList(BaseQO<PropertyComplaintsQO> baseQO);
}
