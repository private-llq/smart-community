package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.CommunityRfService;
import com.jsy.community.config.PropertyTopicNameEntity;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.face.xu.XUFaceEditPersonDTO;
import com.jsy.community.entity.property.CommunityRfEntity;
import com.jsy.community.entity.property.CommunityRfSycRecordEntity;
import com.jsy.community.entity.property.PropertyFaceSyncRecordEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.util.Query;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Set;

/**
 * @Author: Pipi
 * @Description: 门禁卡服务实现
 * @Date: 2021/11/3 16:32
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class CommunityRfServiceImpl extends ServiceImpl<CommunityRfMapper, CommunityRfEntity> implements CommunityRfService {

    @Autowired
    private CommunityRfMapper rfMapper;

    @Autowired
    private CommunityRfSycRecordMapper rfSycRecordMapper;

    @Autowired
    private CommunityHardWareMapper hardWareMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @param rfEntity :
     * @author: Pipi
     * @description: 添加门禁卡
     * @return: java.lang.Integer
     * @date: 2021/11/5 14:25
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addRf(CommunityRfEntity rfEntity) {
        rfEntity.setId(SnowFlake.nextId());
        Integer result = 0;
        if (rfEntity.getEnableStatus() == 1) {
            // 开启
            rfEntity.setSycStatus(1);
            // 新增门卡
            result = rfMapper.insert(rfEntity);
            if (result == 1) {
                String mobile = rfEntity.getMobile();
                Long communityId = rfEntity.getCommunityId();
                // 查询社区设备
                Set<String> hardWareIdList = hardWareMapper.selectListHardWareIdByCommunityId(communityId);
                if (!CollectionUtils.isEmpty(hardWareIdList)) {
                    ArrayList<CommunityRfSycRecordEntity> rfSycRecordEntities = new ArrayList<>();
                    for (String hardWareId : hardWareIdList) {
                        CommunityRfSycRecordEntity rfSycRecordEntity = new CommunityRfSycRecordEntity();
                        rfSycRecordEntity.setId(SnowFlake.nextId());
                        rfSycRecordEntity.setRfNum(rfEntity.getRfNum());
                        rfSycRecordEntity.setHardwareId(hardWareId);
                        rfSycRecordEntity.setCommunityId(communityId);
                        rfSycRecordEntities.add(rfSycRecordEntity);
                    }
                    rfSycRecordMapper.batchInsertRecord(rfSycRecordEntities);
                    XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
                    xuFaceEditPersonDTO.setCustomId(mobile);
                    xuFaceEditPersonDTO.setName(rfEntity.getName());
                    xuFaceEditPersonDTO.setPersonType(0);
                    xuFaceEditPersonDTO.setTempCardType(0);
                    xuFaceEditPersonDTO.setHardwareIds(hardWareIdList);
                    xuFaceEditPersonDTO.setRFIDCard(rfEntity.getRfNum());
                    xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
                    xuFaceEditPersonDTO.setOperator("editPerson");
                    // 向设备发送同步信息
                    rabbitTemplate.convertAndSend(PropertyTopicNameEntity.exFaceXu, PropertyTopicNameEntity.topicFaceXuServer, JSON.toJSONString(xuFaceEditPersonDTO));
                }
            }
        } else {
            rfEntity.setSycStatus(0);
            //新增门卡
            result = rfMapper.insert(rfEntity);
        }
        return result;
    }
}
