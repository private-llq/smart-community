package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVisitingCarService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitingCarEntity;
import com.jsy.community.mapper.VisitingCarMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitingCarQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
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
@DubboService(version = Const.version, group = Const.group_proprietor)
public class VisitingCarServiceImpl extends ServiceImpl<VisitingCarMapper, VisitingCarEntity> implements IVisitingCarService {
	
    @Autowired
    private VisitingCarMapper visitingCarMapper;
	
	/**
	 * @Description: 添加随行车辆
	 * @Param: [visitingCarEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	@Override
	public boolean addVisitingCar(VisitingCarEntity visitingCarEntity){
		int result = visitingCarMapper.insert(visitingCarEntity);
		if(result == 1){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 修改随行车辆
	 * @Param: [visitingCarQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@Override
	public boolean updateVisitingCarById(VisitingCarQO visitingCarQO){
		VisitingCarEntity entity = new VisitingCarEntity();
		BeanUtils.copyProperties(visitingCarQO,entity);
		int result = visitingCarMapper.updateById(entity);
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
	* @Description: 随行车辆 分页查询
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.VisitingCarEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	**/
	@Override
	public PageInfo<VisitingCarEntity> queryVisitingCarPage(BaseQO<String> baseQO){
		Page<VisitingCarEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		Page<VisitingCarEntity> visitingCarpage = visitingCarMapper.selectPage(page, new QueryWrapper<VisitingCarEntity>().select("*").eq("uid", baseQO.getQuery()));
		for(VisitingCarEntity visitingCarEntity : visitingCarpage.getRecords()){
			visitingCarEntity.setCarTypeStr(BusinessEnum.CarTypeEnum.carTypeMap.get(visitingCarEntity.getCarType()));
		}
		PageInfo<VisitingCarEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(visitingCarpage,pageInfo);
		return pageInfo;
	}
}
