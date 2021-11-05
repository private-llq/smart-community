package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.property.ActivityUserEntity;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.mapper.ActivityMapper;
import com.jsy.community.mapper.ActivityUserMapper;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.service.IActivityService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 活动管理
 * @author: DKS
 * @create: 2021-11-3 10:00
 **/
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, ActivityEntity> implements IActivityService {


    @Resource
    private ActivityMapper propertyActivityMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ActivityUserMapper propertyActivityUserMapper;
    
    @Resource
    private CommunityMapper communityMapper;
    
    /**
     * @Description: 活动管理分页查询
     * @author: DKS
     * @since: 2021/11/3 10:59
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.ActivityEntity>
     */
    @Override
    public PageInfo<ActivityEntity> list(BaseQO<ActivityEntity> baseQO) {
        ActivityEntity query = baseQO.getQuery();
        Page<ActivityEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<ActivityEntity> wrapper = new QueryWrapper<>();
        if (query.getActivityStatus() != null && query.getActivityStatus() != 0){
            wrapper.eq("activity_status",query.getActivityStatus());
        }
        if (StringUtils.isNotBlank(query.getTheme())){
            wrapper.like("theme",query.getTheme());
        }
        Page<ActivityEntity> pageData = propertyActivityMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        for (ActivityEntity record : pageData.getRecords()) {
            record.setApplyCount(propertyActivityUserMapper.selectCount(new QueryWrapper<ActivityUserEntity>().eq("activity_id", record.getId())));
            // 补充状态名称
            record.setActivityStatusName(record.getActivityStatus() == 1 ? "预发布" : record.getActivityStatus() == 2 ? "报名进行中" : record.getActivityStatus() == 3 ? "报名已结束"
                : record.getActivityStatus() == 4 ? "活动进行中" : record.getActivityStatus() == 5 ? "活动已结束" : "");
        }
        PageInfo<ActivityEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }

    
    /**
     * @Description: 活动管理查询详情
     * @author: DKS
     * @since: 2021/11/3 14:17
     * @Param: [id]
     * @return: com.jsy.community.entity.proprietor.ActivityEntity
     */
    @Override
    public ActivityEntity getOne(Long id) {
        return propertyActivityMapper.selectById(id);
    }
    
    /**
     * @Description: 报名详情分页查询
     * @author: DKS
     * @since: 2021/11/3 14:33
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.ActivityUserEntity>
     */
    @Override
    public PageInfo<ActivityUserEntity> detailPage(BaseQO<ActivityUserEntity> baseQO) {
        ActivityUserEntity query = baseQO.getQuery();
        Page<ActivityUserEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<ActivityUserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id",query.getActivityId());
        if (StringUtils.isNotBlank(query.getKey())){
            wrapper.like("mobile", query.getKey()).or().like("name", query.getKey());
        }
        Page<ActivityUserEntity> pageData = propertyActivityUserMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        PageInfo<ActivityUserEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
    
    /**
     * @Description: 修改活动
     * @author: DKS
     * @since: 2021/11/3 14:25
     * @Param: [activityEntity]
     * @return: java.lang.Boolean
     */
    @Override
    @Transactional
    public Boolean update(ActivityEntity activityEntity) {
        return propertyActivityMapper.updateById(activityEntity) > 0;
    }
    
    
    /**
     * @Description: 新增活动
     * @author: DKS
     * @since: 2021/11/3 14:22
     * @Param: com.jsy.community.entity.proprietor.ActivityEntity
     * @return: java.lang.Boolean
     */
    @Override
    @Transactional
    public Boolean saveBy(ActivityEntity entity) {
//        List<Long> idsList = new ArrayList<>();
//        List<ActivityEntity> activityEntities = new ArrayList<>();
//        // 查询所有小区
//        List<CommunityEntity> communityEntities = communityMapper.selectList(new QueryWrapper<CommunityEntity>().eq("deleted", 0));
//        for (CommunityEntity communityEntity : communityEntities) {
//            ActivityEntity activityEntity = new ActivityEntity();
//            activityEntity.setId(SnowFlake.nextId());
//            activityEntity.setCommunityId(communityEntity.getId());
//            activityEntity.setTheme(entity.getTheme());
//            activityEntity.setContent(entity.getContent());
//            activityEntity.setBeginActivityTime(entity.getBeginActivityTime());
//            activityEntity.setOverActivityTime(entity.getOverActivityTime());
//            activityEntity.setBeginApplyTime(entity.getBeginApplyTime());
//            activityEntity.setOverApplyTime(entity.getOverApplyTime());
//            activityEntity.setCount(entity.getCount());
//            activityEntity.setPicture(entity.getPicture());
//            activityEntity.setDeleted(0L);
//            activityEntity.setCreateTime(LocalDateTime.now());
//            activityEntity.setActivityStatus(1);
//            activityEntities.add(activityEntity);
//            idsList.add(activityEntity.getId());
//        }
//        // 批量新增活动（所有小区）
//        int insert = propertyActivityMapper.addActivityEntities(activityEntities);
    
        entity.setId(SnowFlake.nextId());
        entity.setType(2);
        entity.setCommunityId(0L);
        int insert = propertyActivityMapper.insert(entity);
    
        Map<String, Object> map = new HashMap<>();
        // 1表示活动
        map.put("type",1);
        map.put("dataId",entity.getId());

        //活动报名开始
        map.put("status",2);
        rabbitTemplate.convertAndSend("exchange_activity_delay", "queue.activity.delay", map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",LocalDateTime.now().until(entity.getBeginApplyTime(), ChronoUnit.MILLIS));
                return message;
            }
        });

        //活动报名结束
        map.put("status",3);
        rabbitTemplate.convertAndSend("exchange_activity_delay", "queue.activity.delay", map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",LocalDateTime.now().until(entity.getOverApplyTime(), ChronoUnit.MILLIS));
                return message;
            }
        });

        //活动开始
        map.put("status",4);
        rabbitTemplate.convertAndSend("exchange_activity_delay", "queue.activity.delay", map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",LocalDateTime.now().until(entity.getBeginActivityTime(), ChronoUnit.MILLIS));
                return message;
            }
        });

        //活动结束
        map.put("status",5);
        rabbitTemplate.convertAndSend("exchange_activity_delay", "queue.activity.delay", map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",LocalDateTime.now().until(entity.getOverActivityTime(), ChronoUnit.MILLIS));
                return message;
            }
        });

        //活动开始前两个小时提醒用户
        map.put("type",3);
        map.put("status",null);
        long until = LocalDateTime.now().until(entity.getOverActivityTime(), ChronoUnit.MILLIS);
        long pushUntil=7200000L;
        rabbitTemplate.convertAndSend("exchange_activity_delay", "queue.activity.delay", map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",until-pushUntil);
                return message;
            }
        });
        return insert == 1;
    }
}
