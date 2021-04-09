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
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RepairOrderQO;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
	
	@Autowired
	private AdminUserMapper adminUserMapper;
	
	// 房屋报修事项(个人)
	private static final int TYPE_PERSON = 1;
	
	// 房屋报修事项(公共)
	private static final int TYPE_COMMON = 4;
	
	@Override
	public PageInfo<RepairOrderEntity> listRepairOrder(BaseQO<RepairOrderQO> repairOrderQO) {
		Page<RepairOrderEntity> info = new Page<>(repairOrderQO.getPage(), repairOrderQO.getSize());
		List<RepairOrderEntity> orderEntities = repairOrderMapper.listRepairOrder(info, repairOrderQO);
		for (RepairOrderEntity orderEntity : orderEntities) {
			if (orderEntity.getDealId() != null) {
				AdminUserEntity adminUserEntity = adminUserMapper.selectById(orderEntity.getDealId());
				if (adminUserEntity != null) {
					// 处理人姓名
					orderEntity.setDealName(adminUserEntity.getRealName());
					// 处理人编号
					orderEntity.setDealNameNumber(adminUserEntity.getNumber());
				}
			}
			if (orderEntity.getAssignId() != null) {
				QueryWrapper<AdminUserEntity> wrapper = new QueryWrapper<>();
				wrapper.eq("uid", orderEntity.getAssignId());
				AdminUserEntity adminUserEntity = adminUserMapper.selectOne(wrapper);
				orderEntity.setAssignName(adminUserEntity.getRealName());
			}
		}
		
		PageInfo<RepairOrderEntity> objectPageInfo = new PageInfo<>();
		BeanUtils.copyProperties(info, objectPageInfo);
		
		objectPageInfo.setRecords(orderEntities);
		return objectPageInfo;
	}
	
	
	@Override
	@Transactional
	public void dealOrder(Long id, Long dealId, BigDecimal money, String uid) {
		RepairOrderEntity orderEntity = repairOrderMapper.selectById(id);
		if (orderEntity == null) {
			throw new PropertyException("该报修订单不存在");
		}
		orderEntity.setStatus(1); // 将状态设置为 处理中
		orderEntity.setDealId(dealId);
		orderEntity.setMoney(money);
		
		orderEntity.setAssignId(uid);
		repairOrderMapper.updateById(orderEntity);
		
		
		Long repairId = orderEntity.getRepairId();
		RepairEntity repairEntity = repairMapper.selectById(repairId);
		if (repairEntity == null) {
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
	@Transactional
	public void successOrder(Long id, String uid) {
		RepairEntity repairEntity = commOrder(id);
		if (repairEntity == null) {
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
		orderEntity.setAssignId(uid);
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
		RepairOrderEntity orderEntity = repairOrderMapper.selectById(id);
		if (orderEntity == null) {
			throw new PropertyException("该报修订单不存在");
		}
		Long dealId = orderEntity.getDealId();
		AdminUserEntity userEntity = adminUserMapper.selectById(dealId);
		if (userEntity != null) {
			orderEntity.setDealName(userEntity.getRealName());
		}
		return orderEntity;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rejectOrder(Long id, String reason, String uid) {
		RepairOrderEntity entity = repairOrderMapper.selectById(id);
		if (entity.getStatus() != 0) {
			throw new PropertyException("该订单已开始处理，不能驳回");
		}
		entity.setStatus(3);
		entity.setRejectReason(reason);
		entity.setAssignId(uid);
		repairOrderMapper.updateById(entity);
		
		// 查询报修信息
		RepairEntity repairEntity = repairMapper.selectById(entity.getRepairId());
		// 对报修信息设置成驳回
		repairEntity.setStatus(3);
		repairMapper.updateById(repairEntity);
	}
	
	@Override
	public List<Map<String, String>> getRepairPerson(String condition, Long communityId) {
		return adminUserMapper.getRepairPerson(condition, communityId);
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
		queryWrapper.eq("id", id);
		RepairEntity repairEntity = repairMapper.selectOne(queryWrapper);
		if (repairEntity == null) {
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
