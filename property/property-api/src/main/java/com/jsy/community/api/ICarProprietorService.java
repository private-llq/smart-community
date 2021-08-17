package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarPatternEntity;
import com.jsy.community.entity.property.CarProprietorEntity;
import com.jsy.community.qo.BaseQO;

import java.util.List;
import java.util.Map;

public interface ICarProprietorService extends IService<CarProprietorEntity> {
    /**
     * @Description: 查询所有业主车辆信息
     * @Param: [adminCommunityId]
     * @Return: java.util.List<com.jsy.community.entity.property.CarProprietorEntity>
     * @Author: Tian
     * @Date: 2021/8/10-15:44
     **/
    List<CarProprietorEntity> listAll(Long adminCommunityId);

    /**
     * @Description: f分页查询业主车辆
     * @Param: [baseQO, adminCommunityId]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.property.CarProprietorEntity>
     * @Author: Tian
     * @Date: 2021/8/11-10:36
     **/
    Page<CarProprietorEntity> listPage(BaseQO<CarProprietorEntity> baseQO, Long adminCommunityId,Long phone);

    /**
     * @Description: 添加业主车辆
     * @Param: [carProprietorEntity, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/11-14:10
     **/
    boolean addProprietor(CarProprietorEntity carProprietorEntity, Long adminCommunityId);

    /**
     * @Description: x修改业主车辆
     * @Param: [carProprietorEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/11-14:15
     **/
    boolean updateProprietor(CarProprietorEntity carProprietorEntity);

    /**
     * @Description: 删除业主车辆
     * @Param: [id]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/11-14:18
     **/
    boolean deleteProprietor(Long id);
}
