package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IRepairService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.proprietor.RepairCommentQO;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.PushInfoUtil;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.repair.RepairVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.UserImVo;
import com.zhsj.im.chat.api.rpc.IImChatPublicPushRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 房屋报修 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-12-08
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
@Slf4j
public class RepairServiceImpl extends ServiceImpl<RepairMapper, RepairEntity> implements IRepairService {
	
	@Autowired
	private RepairMapper repairMapper;
	
	@Autowired
	private RepairOrderMapper repairOrderMapper;
	
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Autowired
	private CommonConstMapper commonConstMapper;
	
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserIMMapper userIMMapper;

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER)
	private IBaseUserInfoRpcService userInfoRpcService;

	@DubboReference(version = com.zhsj.im.chat.api.constant.RpcConst.Rpc.VERSION, group = com.zhsj.im.chat.api.constant.RpcConst.Rpc.Group.GROUP_IM_CHAT)
	private IImChatPublicPushRpcService iImChatPublicPushRpcService;
	
	// 个人报修事项
	private static final int TYPEPERSON = 1;
	
	// 公共报修事项
	private static final int TYPECOMMON = 4;
	
	private String repairNumber="repair_number:";
	
	@Override
	public List<RepairEntity> testList() {
		return repairMapper.selectList(null);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRepair(RepairEntity repairEntity) {
		// 报修类别
		Integer repairType = repairEntity.getRepairType();
		// 报修事项
		List<CommonConst> commonConstList = null;
		if (repairType == 0) {
			QueryWrapper<CommonConst> wrapper = new QueryWrapper<>();
			wrapper.eq("type_id", TYPEPERSON);
			commonConstList = commonConstMapper.selectList(wrapper);
			
			
		} else {
			QueryWrapper<CommonConst> wrapper = new QueryWrapper<>();
			wrapper.eq("type_id", TYPECOMMON);
			commonConstList = commonConstMapper.selectList(wrapper);
			
		}
		
		if (!CollectionUtils.isEmpty(commonConstList)) {
			// 判断该报修类别是否有这样一个报修事项
			QueryWrapper<CommonConst> constQueryWrapper = new QueryWrapper<>();
			constQueryWrapper.eq("id", repairEntity.getType()).eq("const_name", repairEntity.getTypeName());
			CommonConst commonConst = commonConstMapper.selectOne(constQueryWrapper);
			if (!commonConstList.contains(commonConst)) {
				throw new ProprietorException("您选择的报修事项不正确或不存在");
			}
		}
		
		String str = null;
		String s = null;
		Object number = redisTemplate.opsForValue().get(repairNumber + repairEntity.getCommunityId());
		if (number!=null){
			s = String.valueOf(number);
		}else {
			s = String.valueOf(1);
		}
		if (s.length()==1) {
			str = "000" + s;
		} else if (s.length()==2) {
			str = "00" + s;
		} else if (s.length()==3) {
			str = "0" + s;
		} else if (s.length()==4) {
			str = s;
		} else {
			str = s;
		}
		int anInt = Integer.parseInt(s);
		++anInt;
		redisTemplate.opsForValue().set(repairNumber + repairEntity.getCommunityId(), String.valueOf(anInt), getMinute(), TimeUnit.MINUTES);
		String format = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String sb = "BX" + format + str;
		repairEntity.setRepairNum(sb);
		repairEntity.setId(SnowFlake.nextId());
		repairMapper.insert(repairEntity); // 1.png;2.png;3.png;   或 1.png;2.png;3.png 都可以            redis 现在存的是  3个 xx.png
		Long repairId = repairEntity.getId();// 得到新添加报修的报修id
		
		RepairOrderEntity orderEntity = new RepairOrderEntity(); // 用得到的新增报修id 关联订单表
		BeanUtils.copyProperties(repairEntity, orderEntity);
		orderEntity.setId(SnowFlake.nextId());
		orderEntity.setStatus(0);
		orderEntity.setNumber(MyMathUtils.randomCode(17));
		orderEntity.setRepairId(repairId);
		orderEntity.setOrderTime(repairEntity.getCreateTime());
		repairOrderMapper.insert(orderEntity);

		UserImVo userIm = userInfoRpcService.getEHomeUserIm(repairEntity.getUserId());
		if (userIm!=null){
			PushInfoUtil.PushPublicTextMsg(
					iImChatPublicPushRpcService,
					userIm.getImId(),
					"报修通知",
					"报修事项提交成功",
					null,"报修事项提交成功\n请耐心等待工作人员处理。",null, BusinessEnum.PushInfromEnum.REPAIRNOTICE.getName());
		}
	}
	
	/**
	 * @Description: 获取当前时间到0点钟的分钟数
	 * @author: Hu
	 * @since: 2021/5/19 9:48
	 * @Param:
	 * @return:
	 */
	public int getMinute() {
		int hour = LocalDateTime.now().getHour();
		int minute = LocalDateTime.now().getMinute();
		int remainHour=24-hour-1;
		int remainMinute=60-minute;
		return remainHour*60+remainMinute;
	}
	
	@Override
	public List<RepairEntity> getRepair(String id, Integer status) {
		List<RepairEntity> list = null;
		// 如果不传status就是查询全部报修
		if (status == null) {
			QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
			wrapper.eq("user_id", id).orderByDesc("create_time").orderByAsc("status");
			list = repairMapper.selectList(wrapper);
		} else {
			// 根据报修状态查询响应数据 0 待处理 1 修复中 2 已完成  3 驳回
			QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
			wrapper.eq("user_id", id).orderByDesc("create_time").eq("status", status);
			list = repairMapper.selectList(wrapper);
		}
		for (RepairEntity repairEntity : list) {
			Long type = repairEntity.getType();
			CommonConst commonConst = commonConstMapper.selectById(type);
			if (commonConst != null) {
				repairEntity.setTypeName(commonConst.getConstName());
			}
			
			Integer repairType = repairEntity.getRepairType();
			if (repairType == 0) {
				repairEntity.setRepairTypeString("个人报修");
			}
			if (repairType == 1) {
				repairEntity.setRepairTypeString("公共报修");
			}
			
			if (repairEntity.getStatus() == 0) {
				repairEntity.setStatusString("待处理");
			}
			if (repairEntity.getStatus() == 1) {
				repairEntity.setStatusString("修复中");
			}
			if (repairEntity.getStatus() == 2) {
				repairEntity.setStatusString("已完成");
				// 对已评价的 获取其评价信息，前端通过其判断是否该订单评价过[评价过的不能再次评价]
				QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("repair_id", repairEntity.getId());
				RepairOrderEntity order = repairOrderMapper.selectOne(queryWrapper);
				if (order != null) {
					if (!StringUtils.isEmpty(order.getComment())) {
						repairEntity.setComment(order.getComment());
						System.out.println(repairEntity.getComment());
					}
				}
			}
			if (repairEntity.getStatus() == 3) {
				repairEntity.setStatusString("驳回");
			}
		}
		return list;
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void cancelRepair(Long id, String userId) {
		QueryWrapper<RepairEntity> condition = new QueryWrapper<>();
		condition.eq("user_id", userId).eq("id", id);
		RepairEntity entity = repairMapper.selectOne(condition);
		if (entity == null) {
			throw new ProprietorException("该订单不存在");
		}
		if (entity.getStatus() != 0) { //处理中  不能取消了
			throw new ProprietorException("您好,工作人员已经在处理中或已处理完成，不能取消!");
		}
		
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id", userId).eq("id", id);
		repairMapper.delete(wrapper);// 删除房屋报修表
		
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("repair_id", id);
		repairOrderMapper.delete(queryWrapper);// 删除订单表
	}
	
	@Override
	public void completeRepair(Long id, Long userId) {
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id", userId).eq("id", id);
		RepairEntity repairEntity = repairMapper.selectOne(wrapper); // 根据id查询房屋报修信息
		
		repairEntity.setStatus(2);// 设置为已处理
		repairMapper.updateById(repairEntity);
	}
	
	@Override
	public void appraiseRepair(RepairCommentQO repairCommentQO) {
		QueryWrapper<RepairEntity> entityQueryWrapper = new QueryWrapper<>();
		entityQueryWrapper.eq("user_id", repairCommentQO.getUid()).eq("id", repairCommentQO.getId());
		RepairEntity repairEntity = repairMapper.selectOne(entityQueryWrapper);
		if (repairEntity == null) {
			throw new ProprietorException("您好,您选择的订单并不存在,请联系管理员");
		}
		Integer flag = repairEntity.getStatus();
		if (flag != null && flag != 2) {
			log.debug("预评价的订单状态类型为：" + flag);
			throw new ProprietorException("您好,该订单尚未处理完成");
		}
		
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("repair_id", repairEntity.getId());
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(queryWrapper);
		if (orderEntity != null) {
			orderEntity.setComment(repairCommentQO.getAppraise());
			orderEntity.setCommentStatus(repairCommentQO.getStatus());
			orderEntity.setImgPath(repairCommentQO.getFilePath());
			orderEntity.setCommentTime(new Date());
			repairOrderMapper.updateById(orderEntity);
		}
	}
	
	@Override
	public RepairVO repairDetails(Long id, String userId) { // id 房屋id
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", id).eq("user_id", userId);
		RepairEntity repairEntity = repairMapper.selectOne(wrapper);
		if (repairEntity == null) {
			throw new ProprietorException("该报修订单不存在");
		}
		
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("repair_id", id);
		RepairOrderEntity repairOrderEntity = repairOrderMapper.selectOne(queryWrapper);
		if (repairOrderEntity == null) {
			throw new ProprietorException("您好，你查询的订单有误，请联系管理员");
		}
		
		RepairVO repairVO = new RepairVO();
		
		QueryWrapper<CommonConst> constQueryWrapper = new QueryWrapper<>();
		constQueryWrapper.eq("id", repairEntity.getType());
		CommonConst commonConst = commonConstMapper.selectOne(constQueryWrapper);
		repairEntity.setTypeName(commonConst.getConstName());
		BeanUtils.copyProperties(repairEntity, repairVO); // 封装报修信息
		BeanUtils.copyProperties(repairOrderEntity, repairVO); // 封装订单信息
		
		return repairVO;
	}
	
	@Override
	public void deleteAppraise(Long id) {
		QueryWrapper<RepairOrderEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("repair_id", id);
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(wrapper);
		orderEntity.setComment("");  // TODO 因为mybatis-plus动态sql 所以删除评论是对其评论设置的 ""   app前台判断未评价 已评价的时候 要注意赛选条件
		repairOrderMapper.updateById(orderEntity);
	}
	
	@Override
	public List<CommonConst> getRepairType(int repairType) {
		// 如果前台传0 表示个人报修 1 公共报修
		if (repairType == 0) {
			repairType = TYPEPERSON;
		} else if (repairType == 1) {
			repairType = TYPECOMMON;
		} else {
			throw new PropertyException("请选择正确的报修类别");
		}
		
		QueryWrapper<CommonConst> wrapper = new QueryWrapper<>();
		wrapper.eq("type_id", repairType).select("const_name", "id");
		return commonConstMapper.selectList(wrapper);
	}
	
	@Override
	public String getRejectReason(Long id) {
		QueryWrapper<RepairOrderEntity> repairOrderEntityQueryWrapper = new QueryWrapper<>();
		repairOrderEntityQueryWrapper.eq("repair_id", id);
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(repairOrderEntityQueryWrapper);
		if (orderEntity == null) {
			throw new ProprietorException("该报修订单不存在");
		}
		return orderEntity.getRejectReason();
	}
	
}
