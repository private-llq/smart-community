package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarPatternEntity;

import java.util.List;

public interface ICarPatternService extends IService<CarPatternEntity> {
    /**
     * @Description:查询临时车模式
     * @Param: [communityId]
     * @Return: java.util.List<com.jsy.community.entity.property.CarPatternEntity>
     * @Author: Tian
     * @Date: 2021/8/9-11:28
     **/
    List<CarPatternEntity> listPattern(Long communityId);

    CarPatternEntity findOne(String patternId);
    /**
     * @Description: 添加设备管理--临时车模式
     * @Param: [locationPattern]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/7-11:50
     **/
    boolean addPattern(String locationPattern,Long communityId);

    /**
     * @Description: 修改设备管理--临时车模式
     * @Param: [locationPattern, patternId, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/7-13:59
     **/

    boolean updatePattern(String locationPattern, String locationId, Long communityId);

    /**
     * @Description: 删除临时车模式
     * @Param: [patternId, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/9-14:03
     **/
    boolean deletePattern(String patternId, Long adminCommunityId);
}
