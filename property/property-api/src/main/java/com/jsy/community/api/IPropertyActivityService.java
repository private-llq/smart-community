package com.jsy.community.api;

import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.qo.BaseQO;

import java.util.List;

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
    List<ActivityEntity> list(BaseQO<ActivityEntity> baseQO, Long adminCommunityId);

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
    void getOne(Long id);

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/9/23 10:34
     * @Param:
     * @return:
     */
    void update(ActivityEntity activityEntity);
}
