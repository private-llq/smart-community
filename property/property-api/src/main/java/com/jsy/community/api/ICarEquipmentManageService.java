package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarEquipmentManageEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarEquipMentQO;

import java.util.List;
import java.util.Map;


public interface ICarEquipmentManageService extends IService<CarEquipmentManageEntity> {
    /**
     * @Description: 分页查询设备管理
     * @Param: [baseQO, communityId]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.CarEquipmentManageEntity>
     * @Author: Tian
     * @Date: 2021/8/6-15:29
     **/
    Map<String, Object> equipmentPage(BaseQO<CarEquipmentManageEntity> baseQO, Long communityId);

    /**
     * @Description: 添加设备管理
     * @Param: [carEquipMentQO, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/6-17:52
     **/
    boolean addEquipment(CarEquipMentQO carEquipMentQO, Long adminCommunityId, String uid);

    /**
     * @Description: 删除设备管理
     * @Param: [id, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/9-14:44
     **/
    boolean deleteEquipment(Long id, Long adminCommunityId);



    /**
     * @Description: 查询所有设备信息
     * @Param: [communityId]
     * @Return: java.lang.Object
     * @Author: Tian
     * @Date: 2021/8/10-14:20
     **/
    List<CarEquipmentManageEntity> equipmentList(Long communityId);
}
