package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitingCarEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorPersonEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.VisitingCarMapper;
import com.jsy.community.mapper.VisitorMapper;
import com.jsy.community.mapper.VisitorPersonMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitorQO;
import com.jsy.community.utils.MyPageUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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
@DubboService(version = Const.version, group = Const.group)
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, VisitorEntity> implements IVisitorService {

    @Autowired
    private VisitorMapper visitorMapper;
    
    @Autowired
    private VisitorPersonMapper visitorPersonMapper;
    
    @Autowired
    private VisitingCarMapper visitingCarMapper;
    
    /**
    * @Description: 访客登记 新增
     * @Param: [visitorEntity]
     * @Return: java.lang.Long
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
    @Override
    public Long addVisitor(VisitorEntity visitorEntity){
        int insert = visitorMapper.insert(visitorEntity);
        if(1 != insert){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"访客登记 新增失败");
        }
        return visitorEntity.getId();
    }
    
    /**
    * @Description: 批量新增随行人员
     * @Param: [personList]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    @Override
    public boolean addPersonBatch(List<VisitorPersonEntity> personList){
        int result = visitorPersonMapper.addPersonBatch(personList);
        if(result > 0){
            return true;
        }
        return false;
    }
    
    /**
     * @Description: 批量新增随行车辆
     * @Param: [carList]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
     **/
    @Override
    public boolean addCarBatch(List<VisitingCarEntity> carList){
        int result = visitingCarMapper.addCarBatch(carList);
        if(result > 0){
            return true;
        }
        return false;
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
        visitorPersonMapper.deleteByMap(opMap);
        visitingCarMapper.deleteByMap(opMap);
    }
    
    /**
     * @Description: 修改访客登记申请
     * @Param: [visitorEntity]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
     **/
    @Override
    public boolean updateVisitorById(VisitorEntity visitorEntity){
        int result = visitorMapper.updateById(visitorEntity);
        if(result == 1){
            return true;
        }
        return false;
    }
    /**
    * @Description: 修改随行人员
     * @Param: [visitorPersonEntity]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    @Override
    public boolean updateVisitorPersonById(VisitorPersonEntity visitorPersonEntity){
        int result = visitorPersonMapper.updateById(visitorPersonEntity);
        if(result == 1){
            return true;
        }
        return false;
    }
    
    /**
    * @Description: 修改随行车辆
     * @Param: [visitingCarEntity]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    @Override
    public boolean updateVisitingCarById(VisitingCarEntity visitingCarEntity){
        int result = visitingCarMapper.updateById(visitingCarEntity);
        if(result == 1){
            return true;
        }
        return false;
    }
    
    /**
    * @Description: 删除随行人员
     * @Param: [id]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    @Override
    public boolean deleteVisitorPersonById(Long id){
        int result = visitorPersonMapper.deleteById(id);
        if(result == 1){
            return true;
        }
        return false;
    }
    
    /**
    * @Description: 删除随行车辆
     * @Param: [id]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    @Override
    public boolean deleteVisitingCarById(Long id){
        int result = visitingCarMapper.deleteById(id);
        if(result == 1){
            return true;
        }
        return false;
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
            queryAddress(queryWrapper,visitorQO);
        }
        return visitorMapper.selectPage(page, queryWrapper);
    }
    
    /** 查询楼栋 */
    private void queryAddress(QueryWrapper queryWrapper, VisitorQO visitorQO){
        if(!StringUtils.isEmpty(visitorQO.getUnit())){
            queryWrapper.eq("building",visitorQO.getBuilding());
            if(!StringUtils.isEmpty(visitorQO.getBuilding())){
                queryWrapper.eq("unit",visitorQO.getUnit());
                if(!StringUtils.isEmpty(visitorQO.getFloor())){
                    queryWrapper.eq("floor",visitorQO.getFloor());
                    if(!StringUtils.isEmpty(visitorQO.getDoor())){
                        queryWrapper.eq("door",visitorQO.getDoor());
                    }
                }
            }
        }
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
}
