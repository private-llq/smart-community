package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyActivityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.mapper.PropertyActivityMapper;
import com.jsy.community.mapper.PropertyActivityUserMapper;
import com.jsy.community.qo.BaseQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 社区活动
 * @author: Hu
 * @create: 2021-09-23 10:08
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyActivityServiceImpl extends ServiceImpl<PropertyActivityMapper, ActivityEntity> implements IPropertyActivityService {


    @Autowired
    private PropertyActivityMapper propertyActivityMapper;

    @Autowired
    private PropertyActivityUserMapper propertyActivityUserMapper;


    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/9/23 10:36
     * @Param: [baseQO, adminCommunityId]
     * @return: java.util.List<com.jsy.community.entity.proprietor.ActivityEntity>
     */
    @Override
    public List<ActivityEntity> list(BaseQO<ActivityEntity> baseQO, Long adminCommunityId) {
        return null;
    }


    /**
     * @Description: 查询详情
     * @author: Hu
     * @since: 2021/9/23 10:36
     * @Param: [id]
     * @return: void
     */
    @Override
    public void getOne(Long id) {

    }


    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/9/23 10:36
     * @Param: [activityEntity]
     * @return: void
     */
    @Override
    public void update(ActivityEntity activityEntity) {

    }


    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/9/23 10:36
     * @Param: [entity]
     * @return: void
     */
    @Override
    public void saveBy(ActivityEntity entity) {

    }
}
