package com.jsy.community.api;

import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorHistoryEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

/**
 * @author chq459799974
 * @description 物业端访客服务
 * @since 2021-04-12 13:36
 **/
public interface IVisitorService {
	
	
	PageInfo<VisitorHistoryEntity> queryVisitorPage(BaseQO<VisitorEntity> baseQO);
	
}
