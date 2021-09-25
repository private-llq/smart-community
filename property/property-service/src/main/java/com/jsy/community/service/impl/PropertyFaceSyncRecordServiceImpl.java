package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.PropertyFaceSyncRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFaceSyncRecordEntity;
import com.jsy.community.mapper.PropertyFaceSyncRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 物业人脸同步记录表服务实现
 * @Date: 2021/9/24 11:41
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyFaceSyncRecordServiceImpl extends ServiceImpl<PropertyFaceSyncRecordMapper, PropertyFaceSyncRecordEntity> implements PropertyFaceSyncRecordService {

}
