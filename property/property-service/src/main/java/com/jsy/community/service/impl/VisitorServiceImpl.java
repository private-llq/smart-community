package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorHistoryEntity;
import com.jsy.community.mapper.VisitorHistoryMapper;
import com.jsy.community.mapper.VisitorMapper;
import com.jsy.community.mapper.VisitorPersonRecordMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author chq459799974
 * @description 物业端访客实现类
 * @since 2021-04-12 13:35
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class VisitorServiceImpl implements IVisitorService {
	
	@Autowired
	private VisitorMapper visitorMapper;
	
	@Autowired
	private VisitorPersonRecordMapper visitorPersonRecordMapper;
	
	@Autowired
	private VisitorHistoryMapper visitorHistoryMapper;
	
	/**
	* @Description: 访客记录 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.VisitorEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/12
	**/
	@Override
	public PageInfo<VisitorHistoryEntity> queryVisitorPage(BaseQO<VisitorEntity> baseQO){
		VisitorEntity query = baseQO.getQuery();
		Page<VisitorHistoryEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper<VisitorHistoryEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("*");
		queryWrapper.eq("community_id",query.getCommunityId());
		queryWrapper.orderByDesc("start_time");
//		if(!StringUtils.isEmpty(query.getName())){
//			queryWrapper.like("name",query.getName());
//		}
//		if(!StringUtils.isEmpty(query.getContact())){
//			queryWrapper.like("contact",query.getContact());
//		}
//		if(!StringUtils.isEmpty(query.getCarPlate())){
//			queryWrapper.like("car_plate",query.getCarPlate());
//		}
		//1.查主表-进出历史表
		Page<VisitorHistoryEntity> pageData = visitorHistoryMapper.selectPage(page, queryWrapper);
		if(CollectionUtils.isEmpty(pageData.getRecords())){
			return new PageInfo<>();
		}
		Set<Long> visitorIds = new HashSet<>();
		for(VisitorHistoryEntity historyEntity : pageData.getRecords()){
			visitorIds.add(historyEntity.getVisitorId());
		}
		//2.查访客登记表数据
		Map<Long, VisitorEntity> visitorMap = visitorMapper.queryVisitorMapBatch(visitorIds);
		//3.查随行人员表统计随行人员数量
		Map<Long, Map<Long,Long>> countMap = visitorPersonRecordMapper.getFollowPersonBatch(visitorIds);
		//4.复制数据
		for(VisitorHistoryEntity historyEntity : pageData.getRecords()){
//			visitorMap.get(historyEntity.getVisitorId()).setId(null);
//			VisitorEntity visitorEntity = visitorMap.get(historyEntity.getVisitorId());
			BeanUtils.copyProperties(visitorMap.get(historyEntity.getVisitorId()),historyEntity);
			historyEntity.setFollowCount(countMap.get(historyEntity.getId()) == null ? null : countMap.get(historyEntity.getId()).get("count"));
		}
		PageInfo<VisitorHistoryEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}
	
}
