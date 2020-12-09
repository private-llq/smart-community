package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IRepairOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.RepairMapper;
import com.jsy.community.mapper.RepairOrderMapper;
import com.jsy.community.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
	public List<RepairOrderEntity> listRepairOrder(Long communityId) {
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("community_id",communityId);
		return repairOrderMapper.selectList(queryWrapper);
	}
	
	@Override
	public void dealOrder(Long id) {
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id",id);
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(queryWrapper);
		
		Long repairId = orderEntity.getRepairId();
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id",repairId);
		RepairEntity repairEntity = repairMapper.selectOne(wrapper);
		repairEntity.setStatus(1); // 将状态设置为 处理中
	}
	
	@Override
	public UserEntity getUser(Long id) {
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id",id);
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(queryWrapper);
		
		Long repairId = orderEntity.getRepairId();
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id",repairId);
		RepairEntity repairEntity = repairMapper.selectOne(wrapper);
		
		String userId = repairEntity.getUserId();
		QueryWrapper<UserEntity> entityQueryWrapper = new QueryWrapper<>();
		entityQueryWrapper.eq("uid",userId);
		return userMapper.selectOne(entityQueryWrapper);  // TODO 上述查询 一条sql就能写出来  缺点：要去写sql
	}
	
	@Override
	public String listOrderImg(Long id) {
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id",id);
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(queryWrapper);
		
		Long repairId = orderEntity.getRepairId();
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id",repairId);
		RepairEntity repairEntity = repairMapper.selectOne(wrapper);
		
		return repairEntity.getRepairImg();
	}
	
	@Override
	public void successOrder(Long id) {
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id",id);
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(queryWrapper);
		
		Long repairId = orderEntity.getRepairId();
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id",repairId);
		RepairEntity repairEntity = repairMapper.selectOne(wrapper);
		repairEntity.setStatus(2); // 将状态设置为 处理中
	}
}
