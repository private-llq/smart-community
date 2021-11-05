package com.jsy.community.service;

import com.jsy.community.entity.property.ActivityUserEntity;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

/**
 * @program: com.jsy.community
 * @description: 活动管理
 * @author: DKS
 * @create: 2021-11-3 10:00
 **/
public interface IActivityService {
    /**
     * @Description: 活动管理分页查询
     * @author: DKS
     * @since: 2021/11/3 11:00
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.ActivityEntity>
     */
    PageInfo<ActivityEntity> list(BaseQO<ActivityEntity> baseQO);

    /**
     * @Description: 新增活动
     * @author: DKS
     * @since: 2021/11/3 14:22
     * @Param: com.jsy.community.entity.proprietor.ActivityEntity
     * @return: java.lang.Boolean
     */
    Boolean saveBy(ActivityEntity activityEntity);

    /**
     * @Description: 活动管理查询详情
     * @author: DKS
     * @since: 2021/11/3 14:21
     * @Param: [id]
     * @return: com.jsy.community.entity.proprietor.ActivityEntity
     */
    ActivityEntity getOne(Long id);
    
    /**
     * @Description: 修改活动
     * @author: DKS
     * @since: 2021/11/3 14:25
     * @Param: [activityEntity]
     * @return: java.lang.Boolean
     */
    Boolean update(ActivityEntity activityEntity);
    
    /**
     * @Description: 报名详情分页查询
     * @author: DKS
     * @since: 2021/11/3 14:33
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.ActivityUserEntity>
     */
    PageInfo<ActivityUserEntity> detailPage(BaseQO<ActivityUserEntity> baseQO);
}
