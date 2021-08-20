package com.jsy.community.api;

import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.entity.property.ActivityUserEntity;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: app活动
 * @author: Hu
 * @create: 2021-08-13 14:46
 **/
public interface IActivityService {
    /**
     * @Description: 查询当前小区所有活动
     * @author: Hu
     * @since: 2021/8/13 14:50
     * @Param:
     * @return:
     */
    List<ActivityEntity> list(Long communityId);

    /**
     * @Description: 活动报名
     * @author: Hu
     * @since: 2021/8/13 14:57
     * @Param:
     * @return:
     */
    void apply(ActivityUserEntity activityUserEntity);

    /**
     * @Description: 撤销
     * @author: Hu
     * @since: 2021/8/13 15:22
     * @Param:
     * @return:
     */
    void cancel(Long id, String userId);

    /**
     * @Description: 查询一条活动详情
     * @author: Hu
     * @since: 2021/8/13 15:35
     * @Param:
     * @return:
     */
    ActivityEntity selectOne(Long id, String userId);
}
