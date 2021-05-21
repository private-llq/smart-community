package com.jsy.community.api;

import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorHistoryEntity;
import com.jsy.community.entity.VisitorPersonRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * @author chq459799974
 * @description 物业端访客服务
 * @since 2021-04-12 13:36
 **/
public interface IVisitorService {
	
	/**
	* @Description: 访客记录 分页查询(现在主表数据是t_visitor,连表查询，以后主表可能会改为t_visitor_history)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.VisitorHistoryEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/14
	**/
	PageInfo<VisitorHistoryEntity> queryVisitorPage(BaseQO<VisitorHistoryEntity> baseQO);
	
	/** 
	* @Description: 查询单次访客邀请的随行人员列表
	 * @Param: [visitorId]
	 * @Return: java.util.List<com.jsy.community.entity.VisitorPersonRecordEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	List<VisitorPersonRecordEntity> queryFollowPersonListByVisitorId(Long visitorId);
	
	/**
	* @Description: 新增访客进出记录(数据来自机器)
	 * @Param: [historyEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/5/7
	**/
	boolean addVisitorRecord(VisitorHistoryEntity historyEntity);
	
}
