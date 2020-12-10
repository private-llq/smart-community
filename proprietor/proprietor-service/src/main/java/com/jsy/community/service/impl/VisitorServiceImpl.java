package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitingCarQO;
import com.jsy.community.qo.proprietor.VisitorPersonQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.qo.proprietor.VisitorQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 来访人员 服务实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-11
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, VisitorEntity> implements IVisitorService {

    @Autowired
    private VisitorMapper visitorMapper;
    
    @Autowired
    private VisitorPersonMapper visitorPersonMapper;
    
    @Autowired
    private VisitorPersonRecordMapper visitorPersonRecordMapper;
    
    @Autowired
    private VisitingCarMapper visitingCarMapper;
    
    @Autowired
    private VisitingCarRecordMapper visitingCarRecordMapper;
    
    /**
    * @Description: 访客登记 新增
     * @Param: [visitorEntity]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
    @Override
    public void addVisitor(VisitorEntity visitorEntity){
        int insert = visitorMapper.insert(visitorEntity);
        if(1 != insert){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"访客登记 新增失败");
        }
    }
    
    /**
     * @Description: 批量添加随行人员
     * @Param: [personRecordList]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/12/10
     **/
    @Override
    public void addPersonBatch(List<VisitorPersonRecordEntity> personRecordList){
        int result = visitorPersonRecordMapper.addPersonBatch(personRecordList);
        if(result != personRecordList.size()){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"添加随行人员失败");
        }
    }
    
    /**
     * @Description: 批量添加随行车辆
     * @Param: [carList]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/12/10
     **/
    @Override
    public void addCarBatch(List<VisitingCarRecordEntity> carRecordList){
        int result = visitingCarRecordMapper.addCarBatch(carRecordList);
        if(result != carRecordList.size()){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"添加随行车辆失败");
        }
    }
    
	/**
	 * @Description: 根据ID 删除访客登记申请
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@Override
    public boolean deleteVisitorById(Long id){
	    int result = visitorMapper.deleteById(id);
	    if(result == 1){
	    	return true;
	    }
	    return false;
    }
    
    /**
     * @Description: 关联删除 访客关联数据(随行人员、随行车辆)
     * @Param: [visitorId]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    @Override
    public void deletePersonAndCar(Long visitorId){
        Map<String, Object> opMap = new HashMap<>();
        opMap.put("visitor_id",visitorId);
        visitorPersonRecordMapper.deleteByMap(opMap);
        visitingCarRecordMapper.deleteByMap(opMap);
    }
    
    /**
     * @Description: 分页查询
     * @Param: [BaseQO<VisitorQO>]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.VisitorEntity>
     * @Author: chq459799974
     * @Date: 2020/11/11
     **/
    @Override
    public Page<VisitorEntity> queryByPage(BaseQO<VisitorQO> baseQO){
        Page<VisitorEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO); //设置分页参数
        QueryWrapper<VisitorEntity> queryWrapper = new QueryWrapper<VisitorEntity>().select("*");
        VisitorQO visitorQO = baseQO.getQuery();
        if(visitorQO != null){
            if(!StringUtils.isEmpty(visitorQO.getName())){
                queryWrapper.eq("name",visitorQO.getName());
            }
            if(!StringUtils.isEmpty(visitorQO.getContact())){
                queryWrapper.eq("contact",visitorQO.getContact());
            }
        }
        return visitorMapper.selectPage(page, queryWrapper);
    }
    
    /**
    * @Description: 根据ID单查访客
     * @Param: [id]
     * @Return: com.jsy.community.entity.VisitorEntity
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    @Override
    public VisitorEntity selectOneById(Long id){
        return visitorMapper.selectOne(new QueryWrapper<VisitorEntity>().select("*").eq("id",id));
    }
    
    /**
     * @Description: 根据ID单查随行人员记录
     * @Param: [visitorid]
     * @Return: java.util.List<com.jsy.community.entity.VisitorPersonRecordEntity>
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    @Override
    public List<VisitorPersonRecordEntity> queryPersonRecordList(Long visitorid){
        return visitorPersonRecordMapper.selectList(new QueryWrapper<VisitorPersonRecordEntity>()
            .select("id,name,mobile")
            .eq("visitor_id",visitorid)
        );
    }
    
    /**
     * @Description: 根据ID单查随行车辆记录
     * @Param: [visitorid]
     * @Return: java.util.List<com.jsy.community.entity.VisitingCarRecordEntity>
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    @Override
    public List<VisitingCarRecordEntity> queryCarRecordList(Long visitorid){
        List<VisitingCarRecordEntity> entityList = visitingCarRecordMapper.selectList(new QueryWrapper<VisitingCarRecordEntity>()
            .select("id,car_plate,car_type")
            .eq("visitor_id", visitorid)
        );
        for (VisitingCarRecordEntity entity : entityList) {
            entity.setCarTypeStr(BusinessEnum.CarTypeEnum.carTypeMap.get(entity.getCarType()));
        }
        return entityList;
    }
}
