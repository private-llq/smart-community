package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IRepairOrderService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.RepairMapper;
import com.jsy.community.mapper.RepairOrderMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 报修订单信息 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-08
 */
@DubboService(version = Const.version, group = Const.group_property)
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrderEntity> implements IRepairOrderService {
	
	@Autowired
	private RepairOrderMapper repairOrderMapper;
	
	@Autowired
	private RepairMapper repairMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	
	@Override
	public PageInfo<RepairOrderEntity> listRepairOrder(Long communityId, BaseQO<RepairOrderEntity> baseQO) {
		RepairOrderEntity query = baseQO.getQuery();
		Long page = baseQO.getPage();
		Long size = baseQO.getSize();
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("community_id", communityId);
		
		Page<RepairOrderEntity> repairOrderEntityPage = new Page<>(page, size);
		Page<RepairOrderEntity> selectPage = repairOrderMapper.selectPage(repairOrderEntityPage, queryWrapper);
		
		PageInfo<RepairOrderEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(selectPage, pageInfo);
		return pageInfo;
	}
	
	
	@Override
	public void dealOrder(Long id) {
		RepairEntity repairEntity = commOrder(id);
		if (repairEntity==null) {
			throw new PropertyException("该订单不存在");
		}
		Integer status = repairEntity.getStatus();
		if (!status.equals(0)) {
			throw new PropertyException("该报修订单已处理");
		}
		repairEntity.setStatus(1); // 将状态设置为 处理中
		repairMapper.updateById(repairEntity); // 更新报修表
	}
	
	@Override
	public void successOrder(Long id) {
		RepairEntity repairEntity = commOrder(id);
		Integer status = repairEntity.getStatus();
		if (!status.equals(1)) {
			throw new PropertyException("该报修订单未曾处理，不能直接完成");
		}
		repairEntity.setStatus(2); // 将状态设置为 已处理
		repairMapper.updateById(repairEntity); // 更新报修表
	}
	
	@Override
	public UserEntity getUser(Long id) {
		RepairEntity repairEntity = commOrder(id);
		String userId = repairEntity.getUserId();
		QueryWrapper<UserEntity> entityQueryWrapper = new QueryWrapper<>();
		entityQueryWrapper.eq("uid", userId);
		return userMapper.selectOne(entityQueryWrapper);
	}
	
	@Override
	public String getOrderImg(Long id) {
		QueryWrapper<RepairEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id",id);
		RepairEntity repairEntity = repairMapper.selectOne(queryWrapper);
		if (repairEntity==null) {
			throw new PropertyException("该订单不存在");
		}
		return repairEntity.getRepairImg();
	}
	
	
	private RepairEntity commOrder(Long id) {
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("repair_id", id);
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(queryWrapper);
		
		if (orderEntity == null) {
			throw new PropertyException("该报修订单不存在");
		}
		
		Long repairId = orderEntity.getRepairId();
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", repairId);
		return repairMapper.selectOne(wrapper);
	}
}
