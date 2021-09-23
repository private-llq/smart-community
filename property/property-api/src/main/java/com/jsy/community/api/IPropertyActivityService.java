package com.jsy.community.api;

import com.jsy.community.entity.property.ActivityUserEntity;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.qo.BaseQO;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 社区活动
 * @author: Hu
 * @create: 2021-09-23 10:04
 **/
public interface IPropertyActivityService {
    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/9/23 10:31
     * @Param:
     * @return:
     */
    Map<String, Object> list(BaseQO<ActivityEntity> baseQO, Long adminCommunityId);

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/9/23 10:33
     * @Param:
     * @return:
     */
    void saveBy(ActivityEntity activityEntity);

    /**
     * @Description: 查询详情
     * @author: Hu
     * @since: 2021/9/23 10:34
     * @Param:
     * @return:
     */
    ActivityEntity getOne(Long id);

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/9/23 10:34
     * @Param:
     * @return:
     */
    void update(ActivityEntity activityEntity);

    /**
     * @Description: 查询报名详情
     * @author: Hu
     * @since: 2021/9/23 14:52
     * @Param:
     * @return:
     */
    Map<String, Object> detailPage(BaseQO<ActivityUserEntity> baseQO, Long adminCommunityId);
}
