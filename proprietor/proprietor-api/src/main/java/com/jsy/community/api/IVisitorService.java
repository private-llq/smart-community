package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.VisitingCarEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorPersonEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitorQO;

import java.util.List;

/**
 * <p>
 * 来访人员 服务类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-11
 */
public interface IVisitorService extends IService<VisitorEntity> {
    
    /**
    * @Description: 访客登记 新增
     * @Param: [visitorEntity]
     * @Return: java.lang.Long(自增ID)
     * @Author: chq45799974
     * @Date: 2020/11/12
    **/
    Long addVisitor(VisitorEntity visitorEntity);
    
    /**
     * @Description: 批量添加随行人员
     * @Param: [personList]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
     **/
    boolean addPersonBatch(List<VisitorPersonEntity> personList);
    
    /**
     * @Description: 批量添加随行车辆
     * @Param: [carList]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
     **/
    boolean addCarBatch(List<VisitingCarEntity> carList);
    
    /**
    * @Description: 根据ID 删除访客登记申请
     * @Param: [id]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    boolean deleteVisitorById(Long id);
    
    /**
    * @Description: 逻辑删除 访客关联数据(随行人员、随行车辆)
     * @Param: [visitorId]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
    void deletePersonAndCar(Long visitorId);
    
    /**
    * @Description: 修改访客登记申请
     * @Param: [visitorEntity]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    boolean updateVisitorById(VisitorEntity visitorEntity);
    
    /**
     * @Description: 修改随行人员
     * @Param: [visitorPersonEntity]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
     **/
    boolean updateVisitorPersonById(VisitorPersonEntity visitorPersonEntity);
    
    /**
     * @Description: 修改随行车辆
     * @Param: [visitingCarEntity]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
     **/
    boolean updateVisitingCarById(VisitingCarEntity visitingCarEntity);
    
    /**
    * @Description: 删除随行人员
     * @Param: [id]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    boolean deleteVisitorPersonById(Long id);
    
    /**
    * @Description: 删除随行车辆
     * @Param: [id]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    boolean deleteVisitingCarById(Long id);
    
    /**
    * @Description: 分页查询
     * @Param: [BaseQO<VisitorQO>]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.VisitorEntity>
     * @Author: chq459799974
     * @Date: 2020/11/11
    **/
    Page<VisitorEntity> queryByPage(BaseQO<VisitorQO> baseQO);
    
    /**
    * @Description: 根据ID单查访客
     * @Param: [id]
     * @Return: com.jsy.community.entity.VisitorEntity
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    VisitorEntity selectOneById(Long id);
    
}
