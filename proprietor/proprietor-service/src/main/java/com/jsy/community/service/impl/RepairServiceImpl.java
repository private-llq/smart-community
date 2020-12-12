package com.jsy.community.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IRepairService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.RepairMapper;
import com.jsy.community.mapper.RepairOrderMapper;
import com.jsy.community.mapper.UserHouseMapper;
import com.jsy.community.vo.repair.RepairVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
	private UserHouseMapper userHouseMapper;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Override
	public List<RepairEntity> testList() {
		return repairMapper.selectList(null);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRepair(RepairEntity repairEntity,String uid) {
		QueryWrapper<UserHouseEntity> wrapper = new QueryWrapper<>();
		QueryWrapper<UserHouseEntity> queryWrapper = wrapper.eq("uid", uid);
		UserHouseEntity houseEntity = userHouseMapper.selectOne(queryWrapper);
		
		
		repairEntity.setUserId(uid);
		repairMapper.insert(repairEntity); // 1.png;2.png;3.png;   或 1.png;2.png;3.png 都可以            redis 现在存的是  3个 xx.png
		String repairImg = repairEntity.getRepairImg();
		String[] split = repairImg.split(";");
		for (String filePath : split) {
			redisTemplate.opsForSet().add("repair_img_all", filePath); // 存入redis
		}
		Long id = repairEntity.getId();// 自增得到的报修id
		
		RepairOrderEntity orderEntity = new RepairOrderEntity(); // 关联订单表
		orderEntity.setRepairId(id);
		orderEntity.setNumber(UUID.randomUUID().toString().replace("-", ""));
		orderEntity.setOrderTime(new DateTime());
		orderEntity.setCommunityId(houseEntity.getCommunityId());
		repairOrderMapper.insert(orderEntity);
	}
	
	public static void main(String[] args) {//
		String repairImg = "1.png;2.png;3.png;";
		
		String[] split = repairImg.split(";");
		for (String s : split) {
			System.out.println(s);
		}
		System.out.println(1);
	}
	
	
	@Override
	public List<RepairEntity> getRepair(String id) {
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id", id);
		return repairMapper.selectList(wrapper);
	}
	
	@Override
	@Transactional
	public void cancelRepair(Long id, String userId) {
		QueryWrapper<RepairEntity> condition = new QueryWrapper<>();
		condition.eq("user_id", userId).eq("id", id);
		RepairEntity entity = repairMapper.selectOne(condition);
		if (entity.getStatus() == 1) { //处理中  不能取消了
			throw new ProprietorException("您好,工作人员已经在处理中，不能取消!");
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
	public void appraiseRepair(Long id, String appraise, String uid, Integer status) {
		QueryWrapper<RepairEntity> entityQueryWrapper = new QueryWrapper<>();
		entityQueryWrapper.eq("user_id", uid).eq("id",id);
		RepairEntity repairEntity = repairMapper.selectOne(entityQueryWrapper);
		
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("repair_id", repairEntity.getId());
		RepairOrderEntity orderEntity = repairOrderMapper.selectOne(queryWrapper);
		if (orderEntity != null) {
			orderEntity.setComment(appraise);
			orderEntity.setCommentStatus(status);
			repairOrderMapper.updateById(orderEntity);
		}
	}
	
	@Override
	public RepairVO repairDetails(Long id, String userId) {
		QueryWrapper<RepairEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", id).eq("user_id", userId);
		RepairEntity repairEntity = repairMapper.selectOne(wrapper);
		
		QueryWrapper<RepairOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("repair_id", id);
		RepairOrderEntity repairOrderEntity = repairOrderMapper.selectOne(queryWrapper);
		
		RepairVO repairVO = new RepairVO();
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
	
}