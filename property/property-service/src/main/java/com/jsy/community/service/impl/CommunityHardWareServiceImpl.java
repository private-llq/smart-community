package com.jsy.community.service.impl;
import java.time.LocalDateTime;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.config.TopicExConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.CommunityHardWareMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.CollUtils;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author: Pipi
 * @Description: 社区扫描设备(扫脸机)服务实现
 * @Date: 2021/8/18 10:18
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CommunityHardWareServiceImpl extends ServiceImpl<CommunityHardWareMapper, CommunityHardWareEntity> implements CommunityHardWareService {

    @Autowired
    private CommunityHardWareMapper communityHardWareMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IVisitorService visitorService;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserFaceSyncRecordService userFaceSyncRecordService;

    @Autowired
    private VisitorFaceSyncRecordService visitorFaceSyncRecordService;

    /**
     * @param communityHardWareEntity : 扫描设备(扫脸机)实体
     * @author: Pipi
     * @description: 物业端添加扫描设备(扫脸机)
     * @return: java.lang.Integer
     * @date: 2021/8/18 10:33
     **/
    @Override
    public Integer addHardWare(CommunityHardWareEntity communityHardWareEntity) {
        communityHardWareEntity.setId(SnowFlake.nextId());
        return communityHardWareMapper.insert(communityHardWareEntity);
    }

    /**
     * @author: Pipi
     * @description: 物业端修改扫描设备(扫脸机)信息
     * @param communityHardWareEntity: 扫描设备(扫脸机)实体
     * @return: java.lang.Integer
     * @date: 2021/8/19 10:43
     **/
    @Override
    public Integer updateHardWare(CommunityHardWareEntity communityHardWareEntity) {
        UpdateWrapper<CommunityHardWareEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("community_id", communityHardWareEntity.getCommunityId());
        updateWrapper.eq("id", communityHardWareEntity.getId());
        updateWrapper.set("name", communityHardWareEntity.getName());
        if (communityHardWareEntity.getBuildingId() != null) {
            updateWrapper.set("building_id", communityHardWareEntity.getBuildingId());
        }
        return communityHardWareMapper.update(communityHardWareEntity, updateWrapper);
    }

    /**
     * @param id          : 扫脸一体机ID
     * @param communityId : 社区ID
     * @author: Pipi
     * @description: 扫脸一体机人脸同步
     * @return: java.lang.Integer
     * @date: 2021/8/19 14:43
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer syncFaceUrl(Long id, Long communityId) {
        // 先查一体机信息
        QueryWrapper<CommunityHardWareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("community_id", communityId);
        CommunityHardWareEntity hardWareEntity = communityHardWareMapper.selectOne(queryWrapper);
        if (hardWareEntity == null) {
            throw new PropertyException("未找到需要同步的设备信息!");
        }
        String facilityId = hardWareEntity.getHardwareId();
        // 查询需要同步的数据;包含访客数据和业主等数据
        // 查询访客数据
        List<VisitorEntity> visitorEntities = visitorService.queryUnsyncFaceUrlList(communityId, facilityId);
        // 查询业主数据
        List<UserEntity> userEntityList = userService.queryUnsyncFaceUrlList(communityId, facilityId);
        // 初始化要下发的数据对象
        ArrayList<Map> mapArrayList = new ArrayList<>();
        // 初始化需要更新的访客数据
        ArrayList<VisitorFaceSyncRecordEntity> visitorFaceSyncRecordEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(visitorEntities)) {
            for (VisitorEntity visitorEntity : visitorEntities) {
                // 同步后需要新增的访客人脸同步数据
                VisitorFaceSyncRecordEntity visitorFaceSyncRecordEntity = new VisitorFaceSyncRecordEntity();
                visitorFaceSyncRecordEntity.setVisitorId(visitorEntity.getId());
                visitorFaceSyncRecordEntity.setCommunityId(communityId);
                visitorFaceSyncRecordEntity.setFaceUrl(visitorEntity.getFaceUrl());
                visitorFaceSyncRecordEntity.setFacilityId(facilityId);
                visitorFaceSyncRecordEntity.setId(SnowFlake.nextId());
                visitorFaceSyncRecordEntity.setDeleted(0);
                visitorFaceSyncRecordEntity.setCreateTime(LocalDateTime.now());
                visitorFaceSyncRecordEntities.add(visitorFaceSyncRecordEntity);

                // 组装需要同步的数据
                HashMap<String, Object> hashMap = new HashMap<>();
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                hashMap.put("customId", visitorEntity.getContact());
                hashMap.put("name", visitorEntity.getName());
                hashMap.put("tel", visitorEntity.getContact());
                hashMap.put("personType", 0);
                hashMap.put("tempCardType", 1);
                hashMap.put("cardValidBegin", df.format(visitorEntity.getStartTime()));
                hashMap.put("cardValidEnd", df.format(visitorEntity.getEndTime()));
                hashMap.put("EffectNumber", 0);
                hashMap.put("picURI", visitorEntity.getFaceUrl());
                mapArrayList.add(hashMap);
            }
        }
        // 初始化需要新增的用户同步记录
        ArrayList<UserFaceSyncRecordEntity> userFaceSyncRecordEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userEntityList)) {
            for (UserEntity userEntity : userEntityList) {
                // 同步后需要新增的用户同步记录
                UserFaceSyncRecordEntity userFaceSyncRecordEntity = new UserFaceSyncRecordEntity();
                userFaceSyncRecordEntity.setUid(userEntity.getUid());
                userFaceSyncRecordEntity.setCommunityId(communityId);
                userFaceSyncRecordEntity.setFaceUrl(userEntity.getFaceUrl());
                userFaceSyncRecordEntity.setFacilityId(facilityId);
                userFaceSyncRecordEntity.setId(SnowFlake.nextId());
                userFaceSyncRecordEntities.add(userFaceSyncRecordEntity);

                // 组装需要同步的数据
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("customId", userEntity.getMobile());
                hashMap.put("name", userEntity.getRealName());
                hashMap.put("tel", userEntity.getMobile());
                hashMap.put("personType", 0);
                hashMap.put("tempCardType", 0);
                hashMap.put("picURI", userEntity.getFaceUrl());
                mapArrayList.add(hashMap);
            }
        }
        // 更新访客人脸同步状态
        visitorFaceSyncRecordService.batchAddRecord(visitorFaceSyncRecordEntities);
        // 新增用户人脸同步记录
        userFaceSyncRecordService.batchInsertSyncRecord(userFaceSyncRecordEntities);
        // 推送消息,单次不能超过1000条
        if (mapArrayList.size() > 1000) {
            List<List<Map>> lists = CollUtils.spilList(mapArrayList, 1000);
            // 推送消息
            for (int i = 0; i < lists.size(); i++) {
                assemblingAndPushingData(lists.get(i), facilityId, communityId, i);
            }
        } else if (mapArrayList.size() > 0) {
            // 推送消息
            assemblingAndPushingData(mapArrayList, facilityId, communityId, 0);
        }
        // 更新设备同步时间
        UpdateWrapper<CommunityHardWareEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("data_connect_time", LocalDateTime.now());
        updateWrapper.set("is_connect_data", 1);
        communityHardWareMapper.update(new CommunityHardWareEntity(), updateWrapper);
        return mapArrayList.size();
    }

    /**
     * @param id          : 设备ID
     * @param communityId : 社区ID
     * @author: Pipi
     * @description: 删除设备
     * @return: java.lang.Integer
     * @date: 2021/9/3 16:41
     **/
    @Override
    public Integer deleteHardWare(Long id, Long communityId) {
        QueryWrapper<CommunityHardWareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("community_id", communityId);
        return communityHardWareMapper.delete(queryWrapper);
    }

    /**
     * @author: Pipi
     * @description: 更新设备在线状态
     * @param :
     * @return: void
     * @date: 2021/8/18 17:25
     **/
    @Override
    public void updateOnlineStatus(JSONObject jsonObject) {
        String hardwareId = jsonObject.getString("hardwareId");
        String communityId = jsonObject.getString("communityId");
        Integer onlineStatus = Integer.parseInt(jsonObject.getString("onlineStatus"));
        UpdateWrapper<CommunityHardWareEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("online_status", onlineStatus);
        updateWrapper.eq("hardware_id", hardwareId);
        updateWrapper.eq("community_id", communityId);
        communityHardWareMapper.update(new CommunityHardWareEntity(), updateWrapper);
    }

    /**
     * @param baseQO : 分页查询条件
     * @author: Pipi
     * @description: 分页查询设备列表
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.CommunityHardWareEntity>
     * @date: 2021/9/3 15:00
     **/
    @Override
    public PageInfo<CommunityHardWareEntity> hardWarePageList(BaseQO<CommunityHardWareEntity> baseQO) {
        Page<CommunityHardWareEntity> page = new Page<>();
        PageInfo<CommunityHardWareEntity> pageInfo = new PageInfo<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        CommunityHardWareEntity query = baseQO.getQuery();
        QueryWrapper<CommunityHardWareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id", query.getCommunityId());
        if (StringUtils.isNotBlank(query.getSearchText())) {
            queryWrapper.and(
                    wrapper -> wrapper.like("name", query.getSearchText()).or().like("hardware_id", query.getSearchText())
            );
        }
        if (query.getOnlineStatus() != null) {
            queryWrapper.eq("online_status", query.getOnlineStatus());
        }
        page = communityHardWareMapper.selectPage(page, queryWrapper);
        BeanUtils.copyProperties(page, pageInfo);
        if (!CollectionUtils.isEmpty(pageInfo.getRecords())) {
            for (CommunityHardWareEntity record : page.getRecords()) {
                if (record.getOnlineStatus() == 1) {
                    record.setOnlineStatusStr("在线");
                } else {
                    record.setOnlineStatusStr("离线");
                }
                if (record.getBuildingId() != null) {
                    record.setBuildingIdStr(String.valueOf(record.getBuildingId()));
                }
            }
        }
        return pageInfo;
    }

    /**
     * @author: Pipi
     * @description: 推送延时消息
     * @param personDTOS: 需要推送的数据
     * @param hardwareId: 设备序列号
     * @param communityId: 社区ID
     * @param times: 同一批次推送的分批数
     * @return: void
     * @date: 2021/8/20 10:15
     **/
    private void assemblingAndPushingData(List<Map> personDTOS, String hardwareId, Long communityId, Integer times) {
        JSONObject pushMap = new JSONObject();
        pushMap.put("op", "EditPersonsNew");
        pushMap.put("facesluiceId", hardwareId);
        pushMap.put("personNum", personDTOS.size());
        pushMap.put("info", personDTOS);
        // 用于小区检查监听队列是否正确
        pushMap.put("communityId", communityId);
        log.info("发送消息到队列{}", TopicExConfig.DELAY_FACE_XU_SERVER + "." + communityId);
        rabbitTemplate.convertAndSend(TopicExConfig.DELAY_EX_FACE_XU, TopicExConfig.DELAY_FACE_XU_SERVER, pushMap.toString(), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",60000 * 3 * (times + 1));
                return message;
            }
        });
    }
}
