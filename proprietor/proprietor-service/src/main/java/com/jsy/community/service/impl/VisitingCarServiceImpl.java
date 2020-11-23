package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVisitingCarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitingCarEntity;
import com.jsy.community.mapper.VisitingCarMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 来访车辆 服务实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-12
 */
@DubboService(version = Const.version, group = Const.group)
public class VisitingCarServiceImpl extends ServiceImpl<VisitingCarMapper, VisitingCarEntity> implements IVisitingCarService {
	
    @Autowired
    private VisitingCarMapper visitingCarMapper;
	
    /**
    * @Description: 根据关联的访客表ID 列表查询
     * @Param: [visitorid]
     * @Return: java.util.List<com.jsy.community.entity.VisitingCarEntity>
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
	@Override
	public List<VisitingCarEntity> queryCarList(Long visitorid){
		return visitingCarMapper.selectList(new QueryWrapper<VisitingCarEntity>()
			.select("id,car_plate,car_type")
			.eq("visitor_id",visitorid)
		);
	}
}
