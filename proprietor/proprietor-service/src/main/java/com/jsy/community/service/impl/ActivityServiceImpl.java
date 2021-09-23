package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IActivityService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.ActivityUserEntity;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.mapper.ActivityMapper;
import com.jsy.community.mapper.ActivityUserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @program: com.jsy.community
 * @description: App活动报名人员
 * @author: Hu
 * @create: 2021-08-13 14:44
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, ActivityEntity> implements IActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityUserMapper activityUserMapper;



    /**
     * @Description: 查询一条活动详情
     * @author: Hu
     * @since: 2021/8/13 15:35
     * @Param: [id, userId]
     * @return: void
     */
    @Override
    public ActivityEntity selectOne(Long id, String userId) {
        ActivityEntity activityEntity = activityMapper.selectById(id);
        ActivityUserEntity entity = activityUserMapper.selectOne(new QueryWrapper<ActivityUserEntity>().eq("activity_id", id).eq("uid", userId));
        if (entity!=null){
            activityEntity.setMobile(entity.getMobile());
            activityEntity.setName(entity.getName());
            activityEntity.setStatus(1);
        }else {
            activityEntity.setStatus(0);
        }
        return activityEntity;

    }

    /**
     * @Description: 撤销
     * @author: Hu
     * @since: 2021/8/13 15:22
     * @Param: [id, userId]
     * @return: void
     */
    @Override
    public void cancel(Long id, String userId) {
        ActivityEntity activityEntity = activityMapper.selectById(id);
        ActivityUserEntity entity = activityUserMapper.selectOne(new QueryWrapper<ActivityUserEntity>().eq("activity_id", id).eq("uid", userId));
        if (activityEntity!=null){
            if (activityEntity.getBeginActivityTime().minusHours(12).isBefore(LocalDateTime.now())){
                throw new ProprietorException("活动开始前十二个小时不能取消活动!");
            }
        }
        if (entity!=null){
            activityUserMapper.delete(new QueryWrapper<ActivityUserEntity>().eq("activity_id", id).eq("uid", userId));
        }
    }


    /**
     * @Description: 活动报名
     * @author: Hu
     * @since: 2021/8/13 14:58
     * @Param: [activityUserEntity]
     * @return: void
     */
    @Override
    public void apply(ActivityUserEntity activityUserEntity) {
        activityUserEntity.setId(SnowFlake.nextId());
        ActivityEntity entity = activityMapper.selectById(activityUserEntity.getActivityId());

        ActivityUserEntity one = activityUserMapper.selectOne(new QueryWrapper<ActivityUserEntity>().eq("uid", activityUserEntity.getUid()).eq("activity_id", activityUserEntity.getActivityId()));
        if (one!=null){
            throw new ProprietorException("当前活动您已报名，请勿重复参加!");
        }
        if (entity!=null){
            Integer count = activityUserMapper.selectCount(new QueryWrapper<ActivityUserEntity>().eq("activity_id", activityUserEntity.getActivityId()));
            if (entity.getCount()<count){
                throw new ProprietorException("当前活动报名人数已满!");
            }
            activityUserMapper.insert(activityUserEntity);
        }
    }


    /**
     * @Description: 查询所有活动
     * @author: Hu
     * @since: 2021/8/13 14:58
     * @Param: [communityId]
     * @return: java.util.List<com.jsy.community.entity.proprietor.ActivityEntity>
     */
    @Override
    public HashMap<String, Object> list(BaseQO<ActivityEntity> baseQO) {
        if (baseQO.getPage()==0){
            baseQO.setPage(1L);
        }
        Page<ActivityEntity> entityPage = activityMapper.selectPage(new Page<ActivityEntity>(baseQO.getPage(), baseQO.getSize()), new QueryWrapper<ActivityEntity>()
                .eq("community_id", baseQO.getQuery().getCommunityId()).in("activity_status",1,2));
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",entityPage.getTotal());
        map.put("list",entityPage.getRecords());

        return map;
    }
}
