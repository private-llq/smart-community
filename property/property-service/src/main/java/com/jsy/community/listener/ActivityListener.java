package com.jsy.community.listener;

import com.jsy.community.api.IUserImService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserIMEntity;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.mapper.PropertyActivityMapper;
import com.jsy.community.mapper.PropertyActivityUserMapper;
import com.jsy.community.mapper.PropertyVoteMapper;
import com.jsy.community.utils.PushInfoUtil;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: com.jsy.community
 * @description:  活动报名 业主投票延时队列
 * @author: Hu
 * @create: 2021-09-28 09:34
 **/
@Component
public class ActivityListener {

    @Resource
    private PropertyActivityMapper propertyActivityMapper;

    @Resource
    private PropertyActivityUserMapper propertyActivityUserMapper;

    @Resource
    private PropertyVoteMapper propertyVoteMapper;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserImService userImService;


    @RabbitListener(queues = {"queue_activity_delay"})
    public void QUEUE_CAR_INSERT(Map<String,Object> map, Message message, Channel channel)throws IOException {
        System.out.println(map);

        //表示活动
        if (map.get("type").equals(1)){
            ActivityEntity activityEntity = propertyActivityMapper.selectById(String.valueOf(map.get("dataId")));
            if (activityEntity!=null){
                activityEntity.setActivityStatus(Integer.parseInt(String.valueOf(map.get("status"))));
                propertyActivityMapper.updateById(activityEntity);
            }
        } else if (map.get("type").equals(2)){
            VoteEntity voteEntity = propertyVoteMapper.selectById(String.valueOf(map.get("dataId")));
            if (voteEntity!=null){
                voteEntity.setVoteStatus(Integer.parseInt(String.valueOf(map.get("status"))));
                propertyVoteMapper.updateById(voteEntity);
            }
        } else if (map.get("type").equals(3)){
            ActivityEntity activityEntity = propertyActivityMapper.selectById(String.valueOf(map.get("dataId")));
            Set<String> set = propertyActivityUserMapper.selectUid(String.valueOf(map.get("dataId")));
            Map<Object, Object> map1 = new HashMap<>();
            map1.put("type",4);
            map1.put("dataId",activityEntity.getId());
            if (set.size()!=0){
                List<UserIMEntity> list = userImService.selectUidAll(set);
                for (UserIMEntity entity : list) {
                    PushInfoUtil.PushPublicTextMsg(entity.getImId(),
                            "活动投票",
                            "您报名的活动即将开始",
                            null,
                            "您报名的"+activityEntity.getTheme()+"还有两个小时就开始了，请即时参加哦，\n"+activityEntity.getBeginActivityTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"——"+activityEntity.getOverActivityTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            map1,
                            BusinessEnum.PushInfromEnum.ACTIVITYVOTING.getName());
                }
            }
        }
        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }
}
