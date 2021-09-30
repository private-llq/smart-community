package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IRepairOrderService;
import com.jsy.community.api.IUserImService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RepairOrderQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.PushInfoUtil;
import com.jsy.community.vo.repair.RepairPlanVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
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

	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserImService userImService;
	
	@Autowired
	private CommonConstMapper constMapper;
	
	@Autowired
	private AdminUserMapper adminUserMapper;
	
	@Autowired
	private OrganizationMapper organizationMapper;
	
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
				if (adminUserEntity != null) {
					orderEntity.setAssignName(adminUserEntity.getRealName());
					orderEntity.setAssignNameNumber(adminUserEntity.getNumber());
				}
			}
			
			if (orderEntity.getStatus() == 0) {
				orderEntity.setStatusStr("待处理");
			} else if (orderEntity.getStatus() == 1) {
				orderEntity.setStatusStr("修复中");
			} else if (orderEntity.getStatus() == 2) {
				orderEntity.setStatusStr("已完成");
			} else {
				orderEntity.setStatusStr("已驳回");
			}
			
			String repairImg = orderEntity.getRepairImg();
			String[] strings = repairImg.split(";");
			orderEntity.setRepairImgs(strings);
		}
		
		PageInfo<RepairOrderEntity> objectPageInfo = new PageInfo<>();
		BeanUtils.copyProperties(info, objectPageInfo);
		
		objectPageInfo.setRecords(orderEntities);
		return objectPageInfo;
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dealOrder(Long id, String dealId, BigDecimal money, String uid) {
		RepairOrderEntity orderEntity = repairOrderMapper.selectById(id);
		if (orderEntity == null) {
			throw new PropertyException("该报修订单不存在");
		}
		orderEntity.setStatus(1); // 将状态设置为 处理中
		// 判断该dealId的人员是否存在
		QueryWrapper<AdminUserEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("uid", dealId);
		AdminUserEntity adminUserEntity = adminUserMapper.selectOne(wrapper);
		if (adminUserEntity == null) {
			throw new PropertyException("请选择正确的维修人员");
		}
		orderEntity.setDealId(dealId);
		orderEntity.setMoney(money);
		
		orderEntity.setAssignId(uid);
		orderEntity.setServiceTime(new Date());
		repairOrderMapper.updateById(orderEntity);
		
		
		Long repairId = orderEntity.getRepairId();
		RepairEntity repairEntity = repairMapper.selectById(repairId);
		if (repairEntity == null) {
			throw new PropertyException("该订单不存在");
		}
		repairEntity.setStatus(1); // 将状态设置为 处理中
		repairMapper.updateById(repairEntity); // 更新报修表

		UserIMEntity imEntity = userImService.selectUid(repairEntity.getUserId());
		if (imEntity!=null){
			PushInfoUtil.PushPublicTextMsg(imEntity.getImId(),
					"报修通知",
					"您提交的报修事项处理中",
					null,"您提交的报修事项处理中\n请耐心等待处理结果。",null, BusinessEnum.PushInfromEnum.REPAIRNOTICE.getName());
		}

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void successOrder(Long id, String uid) {
		RepairOrderEntity orderEntity = repairOrderMapper.selectById(id);
		
		if (orderEntity == null) {
			throw new PropertyException("该报修订单不存在");
		}
		orderEntity.setStatus(2); // 将状态设置为 已完成
		orderEntity.setAssignId(uid);
		orderEntity.setSuccessTime(new Date());
		repairOrderMapper.updateById(orderEntity);
		
		Long repairId = orderEntity.getRepairId();
		RepairEntity repairEntity = repairMapper.selectById(repairId);
		if (repairEntity == null) {
			throw new PropertyException("该订单不存在");
		}
		Integer status = repairEntity.getStatus();
		if (!status.equals(1)) {
			throw new PropertyException("该报修订单未曾处理，不能直接完成");
		}
		repairEntity.setStatus(2); // 将状态设置为 已完成
		repairMapper.updateById(repairEntity); // 更新报修表

		UserIMEntity imEntity = userImService.selectUid(repairEntity.getUserId());
		if (imEntity!=null){
			PushInfoUtil.PushPublicTextMsg(imEntity.getImId(),
					"报修通知",
					"报修事项处理完成",
					null,"报修事项处理完成\n您提交的报修事项工作人员已近处理完成了。",null, BusinessEnum.PushInfromEnum.REPAIRNOTICE.getName());
		}

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
		
		// 图片处理
		String imgPath = orderEntity.getRepairImg();
		String[] strings = imgPath.split(";");
		orderEntity.setRepairImgs(strings);
		
		// 订单状态处理
		if (orderEntity.getStatus() == 0) {
			orderEntity.setStatusStr("待处理");
		} else if (orderEntity.getStatus() == 1) {
			orderEntity.setStatusStr("修复中");
		} else if (orderEntity.getStatus() == 2) {
			orderEntity.setStatusStr("已完成");
		} else {
			orderEntity.setStatusStr("已驳回");
		}
		
		String dealId = orderEntity.getDealId();
		QueryWrapper<AdminUserEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("uid", dealId);
		AdminUserEntity userEntity = adminUserMapper.selectOne(wrapper);
		if (userEntity != null) {
			// 被派单人
			orderEntity.setDealName(userEntity.getRealName());
			// 被派单人编号
			orderEntity.setDealNameNumber(userEntity.getNumber());
			// 被派单人部门
			Long orgId = userEntity.getOrgId();
			OrganizationEntity organizationEntity = organizationMapper.selectById(orgId);
			orderEntity.setDepartment(organizationEntity.getName());
		}
		return orderEntity;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rejectOrder(Long id, String reason, String uid, String number, String realName) {
		RepairOrderEntity entity = repairOrderMapper.selectById(id);
		if (entity.getStatus() != 0) {
			throw new PropertyException("该订单已开始处理，不能驳回");
		}
		entity.setStatus(3);
		entity.setRejectReason(reason);
		entity.setAssignId(uid);
		entity.setAssignName(realName);
		entity.setAssignNameNumber(number);
		entity.setRejectTime(new Date());
		repairOrderMapper.updateById(entity);
		
		// 查询报修信息
		RepairEntity repairEntity = repairMapper.selectById(entity.getRepairId());
		// 对报修信息设置成驳回
		repairEntity.setStatus(3);
		repairMapper.updateById(repairEntity);
	}
	
	@Override
	public List<Map<String, Object>> getRepairPerson(String condition, Long communityId) {
		List<Map<String, Object>> repairPerson = adminUserMapper.getRepairPerson(condition, communityId);
		for (Map<String, Object> person : repairPerson) {
			BigInteger s = (BigInteger) person.get("id");
			
			String idStr = s.toString();
			person.put("idStr", idStr);
		}
		return repairPerson;
	}
	
	@Override
	public RepairPlanVO checkCase(Long id) {
		RepairPlanVO planVO = new RepairPlanVO();
		
		// 1. 报修信息
		RepairOrderEntity orderEntity = repairOrderMapper.selectById(id);
		if (orderEntity != null) {
			Long repairId = orderEntity.getRepairId();
			RepairEntity repairEntity = repairMapper.selectById(repairId);
			
			planVO.setRepairTime(repairEntity.getCreateTime());
			planVO.setRepairName(orderEntity.getName());
			planVO.setRepairPhone(repairEntity.getPhone());
			planVO.setProblem(orderEntity.getProblem());
			planVO.setDealTime(orderEntity.getServiceTime());
			
			// 2. 派单信息
			// 被派单人id
			String dealId = orderEntity.getDealId();
			QueryWrapper<AdminUserEntity> wrapper = new QueryWrapper<>();
			wrapper.eq("uid", dealId);
			AdminUserEntity adminUserEntity = adminUserMapper.selectOne(wrapper);
			if (adminUserEntity != null) {
				// 组织机构id
				Long orgId = adminUserEntity.getOrgId();
				OrganizationEntity organizationEntity = organizationMapper.selectById(orgId);
				planVO.setDealName(adminUserEntity.getRealName() + "-" + adminUserEntity.getNumber() + "-" + organizationEntity.getName());
				planVO.setDealPhone(adminUserEntity.getMobile());
				planVO.setDealTime(orderEntity.getServiceTime());
			}
			
			// 3. 完成时间
			planVO.setSuccessTime(orderEntity.getSuccessTime());
			
			// 4. 评价信息
			if (orderEntity.getStatus() == 2) {
				planVO.setCommentTime(orderEntity.getCommentTime());
				planVO.setCommentStatus(orderEntity.getCommentStatus());
				planVO.setComment(orderEntity.getComment());
				if (orderEntity.getCommentTime() != null && orderEntity.getCommentStatus() != null) {
					// 返回进度状态
					planVO.setStatus(4);
				} else {
					// 返回进度状态
					planVO.setStatus(3);
				}
			} else {
				// 返回进度状态
				planVO.setStatus(orderEntity.getStatus() + 1);
				planVO.setCommentStatus(null);
			}
			
			
			return planVO;
		}
		
		
		return null;
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
