package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyActivityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.ActivityUserEntity;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.mapper.PropertyActivityMapper;
import com.jsy.community.mapper.PropertyActivityUserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private RabbitTemplate rabbitTemplate;

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
    public Map<String, Object> list(BaseQO<ActivityEntity> baseQO, Long adminCommunityId) {
        Map<String, Object> map = new HashMap<>();
        ActivityEntity query = baseQO.getQuery();
        QueryWrapper<ActivityEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("community_id",adminCommunityId);
        if (query.getActivityStatus()!=null&&query.getActivityStatus()!=0){
            wrapper.eq("activity_status",query.getActivityStatus());
        }
        if (!"".equals(query.getTheme())&&query.getTheme()!=null){
            wrapper.like("theme",query.getTheme());
        }
        Page<ActivityEntity> page = propertyActivityMapper.selectPage(new Page<ActivityEntity>(baseQO.getPage(), baseQO.getSize()), wrapper);
        List<ActivityEntity> records = page.getRecords();
        for (ActivityEntity record : records) {
            record.setApplyCount(propertyActivityUserMapper.selectCount(new QueryWrapper<ActivityUserEntity>().eq("activity_id", record.getId())));
        }
        map.put("total",page.getTotal());
        map.put("list",records);
        return map;
    }


    /**
     * @Description: 查询详情
     * @author: Hu
     * @since: 2021/9/23 10:36
     * @Param: [id]
     * @return: void
     */
    @Override
    public ActivityEntity getOne(Long id) {
        ActivityEntity entity = propertyActivityMapper.selectById(id);
        return entity;
    }



    /**
     * @Description: 查询报名详情
     * @author: Hu
     * @since: 2021/9/23 14:52
     * @Param: [baseQO, adminCommunityId]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String, Object> detailPage(BaseQO<ActivityUserEntity> baseQO, Long adminCommunityId) {
        Map<String, Object> map = new HashMap<>();
        ActivityUserEntity query = baseQO.getQuery();
        QueryWrapper<ActivityUserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id",query.getActivityId());
        if (!"".equals(query.getKey())&&query.getKey()!=null){
            wrapper.like("mobile",query.getKey()).or().like("name",query.getKey());
        }
        Page<ActivityUserEntity> page = propertyActivityUserMapper.selectPage(new Page<ActivityUserEntity>(baseQO.getPage(), baseQO.getSize()), wrapper);

        map.put("total",page.getTotal());
        map.put("list",page.getRecords());
        return map;
    }

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/9/23 10:36
     * @Param: [activityEntity]
     * @return: void
     */
    @Override
    @Transactional
    public void update(ActivityEntity activityEntity) {
        propertyActivityMapper.updateById(activityEntity);
    }


    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/9/23 10:36
     * @Param: [entity]
     * @return: void
     */
    @Override
    @Transactional
    public void saveBy(ActivityEntity entity) {
        entity.setId(SnowFlake.nextId());
        propertyActivityMapper.insert(entity);

        Map<String, Object> map = new HashMap<>();
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



    }
}
