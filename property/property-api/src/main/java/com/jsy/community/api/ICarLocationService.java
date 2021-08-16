package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarLocationEntity;


import java.util.List;

public interface ICarLocationService extends IService<CarLocationEntity> {
    /**
     * @Description:查询设备位置
     * @Param: [communityId]
     * @Return: java.util.List<com.jsy.community.entity.property.CarLocationEntity>
     * @Author: Tian
     * @Date: 2021/8/9-11:28
     **/
    List<CarLocationEntity> listLocation(Long communityId);

    /**
     * @Description: 添加设备管理--设备位置
     * @Param: [equipmentLocation]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/7-11:50
     **/
    boolean addLocation(String equipmentLocation,Long communityId);

    /**
     * @Description: 修改设备管理--设备位置
     * @Param: [equipmentLocation, patternId, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/7-13:59
     **/

    boolean updateLocation(String equipmentLocation, Long patternId, Long adminCommunityId);

    /**
     * @Description: 删除设备位置
     * @Param: [locationId, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/9-14:00
     **/
    boolean deleteLocation(Long locationId, Long adminCommunityId);

    /**
     * @Description: 查找单条
     * @Param: [locationId]
     * @Return: com.jsy.community.entity.property.CarLocationEntity
     * @Author: Tian
     * @Date: 2021/8/11-15:10
     **/
    CarLocationEntity findOne(Long locationId);
}
