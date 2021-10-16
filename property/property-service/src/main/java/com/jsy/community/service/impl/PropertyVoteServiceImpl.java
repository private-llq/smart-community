package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyVoteService;
import com.jsy.community.api.IVoteService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.entity.proprietor.VoteOptionEntity;
import com.jsy.community.entity.proprietor.VoteTopicEntity;
import com.jsy.community.entity.proprietor.VoteUserEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @program: com.jsy.community
 * @description: 业主投票
 * @author: Hu
 * @create: 2021-09-23 10:06
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyVoteServiceImpl extends ServiceImpl<PropertyVoteMapper,VoteEntity> implements IPropertyVoteService {

    @Autowired
    private PropertyVoteMapper propertyVoteMapper;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IVoteService voteService;

    @Autowired
    private PropertyVoteOptionMapper propertyVoteOptionMapper;

    @Autowired
    private PropertyVoteTopicMapper propertyVoteTopicMapper;

    @Autowired
    private PropertyVoteUserMapper propertyVoteUserMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/9/23 10:46
     * @Param: [baseQO, adminCommunityId]
     * @return: java.util.List<com.jsy.community.entity.proprietor.VoteEntity>
     */
    @Override
    public Map<String, Object> list(BaseQO<VoteEntity> baseQO, Long adminCommunityId) {
        Map<String, Object> map = new HashMap<>();
        VoteEntity query = baseQO.getQuery();
        QueryWrapper<VoteEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("community_id",adminCommunityId);
        if (!"".equals(query.getTheme())&&query.getTheme()!=null){
            wrapper.like("theme",query.getTheme());
        }
        if (query.getVoteStatus()!=null&&query.getVoteStatus()!=0){
            wrapper.like("vote_status",query.getVoteStatus());
        }
        Page<VoteEntity> page = propertyVoteMapper.selectPage(new Page<VoteEntity>(baseQO.getPage(), baseQO.getSize()), wrapper);
        List<VoteEntity> records = page.getRecords();
        for (VoteEntity record : records) {
            record.setVoteTotal(propertyVoteUserMapper.selectCount(new QueryWrapper<VoteUserEntity>().eq("vote_id", record.getId())));
            if (record.getBuildingId().equals("0")){
                record.setScope("全小区");
            } else {
                String[] split = record.getBuildingId().split(",");
                List<HouseEntity> list = houseMapper.selectBatchIds(Arrays.asList(split));
                for (HouseEntity houseEntity : list) {
                    record.setScope(houseEntity.getBuilding()+",");
                }
                record.setScope(record.getScope().substring(0,record.getScope().length()-1));
            }
        }
        map.put("total",page.getTotal());
        map.put("list",records);
        return map;
    }

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/9/23 10:46
     * @Param: [voteEntity]
     * @return: void
     */
    @Override
    @Transactional
    public void saveBy(VoteEntity voteEntity) {
        voteEntity.setId(SnowFlake.nextId());
        propertyVoteMapper.insert(voteEntity);

        VoteTopicEntity topicEntity = voteEntity.getVoteTopicEntity();
        topicEntity.setId(SnowFlake.nextId());
        topicEntity.setVoteId(voteEntity.getId());
        propertyVoteTopicMapper.insert(topicEntity);

        List<VoteOptionEntity> list = new LinkedList<>();
        List<VoteOptionEntity> options = topicEntity.getOptions();
        for (int i= 1;i<=options.size();i++) {
            VoteOptionEntity option = options.get(i-1);
            option.setId(SnowFlake.nextId());
            option.setVoteId(voteEntity.getId());
            option.setTopicId(topicEntity.getId().toString());
            option.setCode(i);
            list.add(option);
        }
        if (list.size()!=0){
            propertyVoteOptionMapper.saveAll(list);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("type",2);
        map.put("dataId",voteEntity.getId());

        //投票进行中
        map.put("status",2);
        rabbitTemplate.convertAndSend("exchange_activity_delay", "queue.activity.delay", map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay", LocalDateTime.now().until(voteEntity.getBeginTime(), ChronoUnit.MILLIS));
                return message;
            }
        });

        //投票已结束
        map.put("status",3);
        rabbitTemplate.convertAndSend("exchange_activity_delay", "queue.activity.delay", map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay", LocalDateTime.now().until(voteEntity.getOverTime(), ChronoUnit.MILLIS));
                return message;
            }
        });
    }

    /**
     * @Description: 查详情
     * @author: Hu
     * @since: 2021/9/23 10:46
     * @Param: [id]
     * @return: void
     */
    @Override
    public List<VoteUserEntity> getOne(Long id) {
        Set<String> ids = new HashSet<>();
        HashMap<String, String> map = new HashMap<>();
        List<VoteUserEntity> entityList = propertyVoteUserMapper.selectList(new QueryWrapper<VoteUserEntity>().eq("vote_id", id));
        for (VoteUserEntity voteUserEntity : entityList) {
            ids.add(voteUserEntity.getUid());
        }
        if (ids.size()!=0){
            List<UserEntity> list = userMapper.listAuthUserInfo(ids);
            for (UserEntity userEntity : list) {
                map.put(userEntity.getUid(),userEntity.getRealName());
            }
            for (VoteUserEntity entity : entityList) {
                entity.setRealName(map.get(entity.getUid()));
            }
        }
        return entityList;
    }


    /**
     * @Description: 删除或撤销
     * @author: Hu
     * @since: 2021/9/23 14:29
     * @Param: [id]
     * @return: void
     */
    @Override
    public void delete(Long id) {
        Integer integer = propertyVoteUserMapper.selectCount(new QueryWrapper<VoteUserEntity>().eq("vote_id", id));
        if (integer==0){
            VoteEntity entity = propertyVoteMapper.selectById(id);
            if (entity.getIssueStatus()==1){
                entity.setVoteStatus(1);
                entity.setIssueStatus(2);
                propertyVoteMapper.updateById(entity);
            }else {
                propertyVoteMapper.deleteById(id);
            }
        } else {
            throw new PropertyException("当前问卷不能被撤销！");
        }
    }

    /**
     * @Description: 查图表
     * @author: Hu
     * @since: 2021/9/23 10:47
     * @Param:
     * @return:
     */
    @Override
    public Map<String, Object> getChart(Long id) {
        Map<String, Object> plan = voteService.getPlan(id);
        return plan;
    }
}
