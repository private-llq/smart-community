package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IRepairOrderService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.CommonConstMapper;
import com.jsy.community.mapper.RepairMapper;
import com.jsy.community.mapper.RepairOrderMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RepairOrderQO;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Autowired
	private CommonConstMapper constMapper;
	
	// 房屋报修事项(个人)
	private static final int TYPE_PERSON = 1;
	
	// 房屋报修事项(公共)
	private static final int TYPE_COMMON = 4;
	
	@Override
	public PageInfo<RepairOrderEntity> listRepairOrder(BaseQO<RepairOrderQO> repairOrderQO) {
		Page<RepairOrderEntity> info = new Page<>(repairOrderQO.getPage(), repairOrderQO.getSize());
		List<RepairOrderEntity> orderEntities = repairOrderMapper.listRepairOrder(info,repairOrderQO);
		
		PageInfo<RepairOrderEntity> objectPageInfo = new PageInfo<>();
		BeanUtils.copyProperties(info,objectPageInfo);
		
		objectPageInfo.setRecords(orderEntities);
		return objectPageInfo;
	}
	
	
	@Override
	@Transactional
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
		
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("repair_id", id);
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(queryWrapper);
		
		if (orderEntity == null) {
			throw new PropertyException("该报修订单不存在");
		}
		orderEntity.setStatus(1); // 将状态设置为 处理中
		repairOrderMapper.updateById(orderEntity);
	}
	
	@Override
	@Transactional
	public void successOrder(Long id) {
		RepairEntity repairEntity = commOrder(id);
		if (repairEntity==null) {
			throw new PropertyException("该订单不存在");
		}
		Integer status = repairEntity.getStatus();
		if (!status.equals(1)) {
			throw new PropertyException("该报修订单未曾处理，不能直接完成");
		}
		repairEntity.setStatus(2); // 将状态设置为 已处理
		repairMapper.updateById(repairEntity); // 更新报修表
		
		
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("repair_id", id);
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(queryWrapper);
		
		if (orderEntity == null) {
			throw new PropertyException("该报修订单不存在");
		}
		orderEntity.setStatus(2); // 将状态设置为 处理中
		repairOrderMapper.updateById(orderEntity);
		
		
	}
	
	@Override
	public List<CommonConst> listRepairType(Integer typeId) {
		List<CommonConst> commonConstList = null;
		QueryWrapper<CommonConst> wrapper = new QueryWrapper<>();
		if (typeId == null) {
			wrapper.eq("type_id", TYPE_PERSON).or().eq("type_id", TYPE_COMMON);
			commonConstList = constMapper.selectList(wrapper);
		} else if (typeId == 0) {
			wrapper.eq("type_id", TYPE_PERSON);
			commonConstList = constMapper.selectList(wrapper);
		} else {
			wrapper.eq("type_id", TYPE_COMMON);
			commonConstList = constMapper.selectList(wrapper);
		}
		return commonConstList;
	}
	
	@Override
	public RepairOrderEntity getRepairById(Long id) {
		return repairOrderMapper.selectById(id);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rejectOrder(Long id,String reason) {
		RepairOrderEntity entity = repairOrderMapper.selectById(id);
		if (entity.getStatus()!=0) {
			throw new PropertyException("该订单已开始处理，不能驳回");
		}
		entity.setStatus(3);
		entity.setRejectReason(reason);
		repairOrderMapper.updateById(entity);
		
		// 查询报修信息
		RepairEntity repairEntity = repairMapper.selectById(entity.getRepairId());
		// 对报修信息设置成驳回
		repairEntity.setStatus(3);
		repairMapper.updateById(repairEntity);
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
