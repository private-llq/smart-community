package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarLocationEntity;
import com.jsy.community.qo.BaseQO;

import java.util.List;

public interface ICarLocationService extends IService<CarLocationEntity> {
    /**
     * @Description:分页查询设备位置
     * @Param: [communityId]
     * @Return: java.util.List<com.jsy.community.entity.property.CarLocationEntity>
     * @Author: Tian
     * @Date: 2021/8/9-11:28
     **/
    Page<CarLocationEntity> listLocation(BaseQO<CarLocationEntity> baseQO, Long communityId);

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

    boolean updateLocation(String equipmentLocation, String patternId, Long adminCommunityId);

    /**
     * @Description: 删除设备位置
     * @Param: [locationId, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/9-14:00
     **/
    boolean deleteLocation(String locationId, Long adminCommunityId);

    /**
     * @Description: 查找单条
     * @Param: [locationId]
     * @Return: com.jsy.community.entity.property.CarLocationEntity
     * @Author: Tian
     * @Date: 2021/8/11-15:10
     **/
    CarLocationEntity findOne(String locationId);

    /**
     * @Description: 查询位置信息
     * @Param: [adminCommunityId]
     * @Return: void
     * @Author: Tian
     * @Date: 2021/8/18-17:58
     **/
    List<CarLocationEntity> selectList(Long adminCommunityId);
}
