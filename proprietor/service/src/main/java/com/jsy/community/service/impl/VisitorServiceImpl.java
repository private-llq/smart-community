package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitingCar;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorPerson;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.VisitingCarMapper;
import com.jsy.community.mapper.VisitorMapper;
import com.jsy.community.mapper.VisitorPersonMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.VisitorQO;
import com.jsy.community.utils.MyPageUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
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
     * @Author: Administrator
     * @Date: 2020/11/12
    **/
    @Override
    public Long addVisitor(VisitorEntity visitorEntity){
        int insert = visitorMapper.insert(visitorEntity);
        if(1 != insert){
            throw new JSYException(JSYError.INTERNAL.getCode(),"访客登记 新增失败");
        }
        return visitorEntity.getId();
    }
    
    /**
     * @Description: 逻辑删除 访客关联数据(随行人员、随行车辆)
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
        VisitorQO visitorQO = baseQO.getQuery();
        QueryWrapper<VisitorEntity> queryWrapper = new QueryWrapper<VisitorEntity>().select("*");
        if(!StringUtils.isEmpty(visitorQO.getName())){
            queryWrapper.eq("name",visitorQO.getName());
        }
        if(!StringUtils.isEmpty(visitorQO.getContact())){
            queryWrapper.eq("contact",visitorQO.getContact());
        }
        queryAddress(queryWrapper,visitorQO);
        return visitorMapper.selectPage(page, queryWrapper);
    }
    
    /** 查询楼栋 */
    private void queryAddress(QueryWrapper queryWrapper, VisitorQO visitorQO){
        if(!StringUtils.isEmpty(visitorQO.getUnit())){
            queryWrapper.eq("unit",visitorQO.getUnit());
            if(!StringUtils.isEmpty(visitorQO.getBuilding())){
                queryWrapper.eq("building",visitorQO.getBuilding());
                if(!StringUtils.isEmpty(visitorQO.getFloor())){
                    queryWrapper.eq("floor",visitorQO.getFloor());
                    if(!StringUtils.isEmpty(visitorQO.getDoor())){
                        queryWrapper.eq("door",visitorQO.getDoor());
                    }
                }
            }
        }
    }
}
