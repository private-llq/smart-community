package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PropertyOpinionEntity;
import com.jsy.community.vo.admin.AdminInfoVo;

/**
 * @program: com.jsy.community
 * @description: 意见反馈
 * @author: Hu
 * @create: 2021-04-11 11:15
 **/
public interface IPropertyOpinionService extends IService<PropertyOpinionEntity> {
    /**
     * @Description: 查询当天投诉是否已达三条
     * @author: Hu
     * @since: 2021/4/11 11:20
     * @Param:
     * @return:
     */
    Integer selectCount(AdminInfoVo userInfo);

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/4/11 11:20
     * @Param:
     * @return:
     */
    void insetOne(PropertyOpinionEntity propertyOpinionEntity, AdminInfoVo userInfo);
}
