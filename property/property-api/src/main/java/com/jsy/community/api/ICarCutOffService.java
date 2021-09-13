package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarCutOffQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.property.CarAccessVO;
import com.jsy.community.vo.property.CarSceneVO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface ICarCutOffService extends IService<CarCutOffEntity> {
    /**
     * @Description: 临时车在场数量
     * @Param: [carCutOffQO, adminCommunityId]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.property.CarCutOffEntity>
     * @Author: Tian
     * @Date: 2021/8/16-11:58
     *
     * @return*/
    Long selectPage(CarCutOffQO carCutOffQO);

     /**
      * @Description: 新增开闸记录
      * @Param: [carCutOffEntity]
      * @Return: boolean
      * @Author: Tian
      * @Date: 2021/9/2-15:56
      **/
    boolean addCutOff(CarCutOffEntity carCutOffEntity);

    /**
     * @Description: 通过车牌和未完成状态查询
     * @Param: [carNumber, state]
     * @Return: java.util.List<com.jsy.community.entity.property.CarCutOffEntity>
     * @Author: Tian
     * @Date: 2021/9/2-17:25
     **/
    List<CarCutOffEntity> selectAccess(String carNumber, Integer state);
    /**
     * @Description: 出闸记录
     * @Param: [carCutOffEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/9/2-17:37
     **/
    boolean updateCutOff(CarCutOffEntity carCutOffEntity);

    /**
     * @Description: 查询所有在场车辆  和进出记录
     * @Param: [carCutOffQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.CarCutOffEntity>
     * @Author: Tian
     * @Date: 2021/9/6-11:45
     **/
    Page<CarCutOffEntity> selectCarPage(BaseQO<CarCutOffQO> baseQO,Long communityId);
    /**
     * @Description: 导出在场车辆
     * @Param: [carCutOffQO, communityId]
     * @Return: java.util.List<com.jsy.community.vo.property.CarSceneVO>
     * @Author: Tian
     * @Date: 2021/9/10-10:17
     **/
    List<CarSceneVO> selectCarSceneList(CarCutOffQO carCutOffQO, Long communityId) throws IOException;

    /**
     * @Description: 导出进出记录
     * @Param: [carCutOffQO, communityId]
     * @Return: java.util.List<com.jsy.community.vo.property.CarSceneVO>
     * @Author: Tian
     * @Date: 2021/9/10-10:17
     **/
    List<CarAccessVO> selectAccessList(CarCutOffQO carCutOffQO, Long communityId);
}
