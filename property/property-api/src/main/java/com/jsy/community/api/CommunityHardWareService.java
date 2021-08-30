package com.jsy.community.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityHardWareEntity;

/**
 * @Author: Pipi
 * @Description: 社区扫描设备(扫脸机)服务
 * @Date: 2021/8/18 10:15
 * @Version: 1.0
 **/
public interface CommunityHardWareService extends IService<CommunityHardWareEntity> {

    /**
     * @author: Pipi
     * @description: 物业端添加扫描设备(扫脸机)
     * @param communityHardWareEntity: 扫描设备(扫脸机)实体
     * @return: java.lang.Integer
     * @date: 2021/8/18 10:33
     **/
    Integer addHardWare(CommunityHardWareEntity communityHardWareEntity);

    /**
     * @author: Pipi
     * @description: 物业端修改扫描设备(扫脸机)信息
     * @param communityHardWareEntity: 扫描设备(扫脸机)实体
     * @return: java.lang.Integer
     * @date: 2021/8/19 10:43
     **/
    Integer updateHardWare(CommunityHardWareEntity communityHardWareEntity);

    /**
     * @author: Pipi
     * @description: 扫脸一体机人脸同步
     * @param id: 扫脸一体机ID
     * @param communityId: 社区ID
     * @return: java.lang.Integer
     * @date: 2021/8/19 14:43
     **/
    Integer syncFaceUrl(Long id, Long communityId);

    /**
     * @author: Pipi
     * @description: 更新设备在线状态
     * @param :
     * @return: void
     * @date: 2021/8/18 17:38
     **/
    void updateOnlineStatus(JSONObject jsonObject);
}
