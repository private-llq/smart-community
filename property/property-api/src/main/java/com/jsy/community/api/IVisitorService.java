package com.jsy.community.api;

import com.jsy.community.entity.VisitorHistoryEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

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
	
}
