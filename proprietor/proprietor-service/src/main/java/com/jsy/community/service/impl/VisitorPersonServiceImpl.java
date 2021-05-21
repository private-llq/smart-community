package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVisitorPersonService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitorPersonEntity;
import com.jsy.community.mapper.VisitorPersonMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitorPersonQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 访客随行人员 服务实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-12
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class VisitorPersonServiceImpl extends ServiceImpl<VisitorPersonMapper, VisitorPersonEntity> implements IVisitorPersonService {
	
    @Autowired
    private VisitorPersonMapper visitorPersonMapper;
    
	/**
	 * @Description: 添加随行人员
	 * @Param: [visitorPersonEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	@Override
	public boolean addVisitorPerson(VisitorPersonEntity visitorPersonEntity){
		return visitorPersonMapper.addPerson(visitorPersonEntity) > 0;
	}
	
	/**
	 * @Description: 修改随行人员
	 * @Param: [visitorPersonQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@Override
	public boolean updateVisitorPersonById(VisitorPersonQO visitorPersonQO){
		VisitorPersonEntity entity = new VisitorPersonEntity();
		BeanUtils.copyProperties(visitorPersonQO,entity);
		int result = visitorPersonMapper.updateById(entity);
		if(result == 1){
			return true;
		}
		return false;
	}

	/**
	 * @Description: 批量删除随行人员
	 * @Param: [ids]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@Override
	public boolean deleteVisitorPersonById(List<Long> ids){
		int result = visitorPersonMapper.deleteBatchIds(ids);
		if(result > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 随行人员 分页查询
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.VisitorPersonEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	@Override
	public PageInfo<VisitorPersonEntity> queryVisitorPersonPage(BaseQO<String> baseQO){
		Page<VisitorPersonEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		Page<VisitorPersonEntity> visitorPersonEntityPage = visitorPersonMapper.selectPage(page, new QueryWrapper<VisitorPersonEntity>().select("*").eq("uid", baseQO.getQuery()));
		PageInfo<VisitorPersonEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(visitorPersonEntityPage,pageInfo);
		return pageInfo;
	}
}
