package com.jsy.community.api;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.PeopleHistoryEntity;
import com.jsy.community.entity.VisitorPersonRecordEntity;
import com.jsy.community.entity.VisitorStrangerEntity;
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
	* @Description: 访客记录 分页查询(现在主表数据是t_visitor,连表查询，以后主表可能会改为t_people_history)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.VisitorHistoryEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/14
	**/
	PageInfo<PeopleHistoryEntity> queryVisitorPage(BaseQO<PeopleHistoryEntity> baseQO);

	/**
	 * @author: Pipi
	 * @description: 访客管理分页查询
	 * @param baseQO: 分页参数
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.VisitorEntity>
	 * @date: 2021/8/13 17:26
	 **/
	PageInfo<VisitorEntity> visitorPage(BaseQO<VisitorEntity> baseQO);
	
	/** 
	* @Description: 查询单次访客邀请的随行人员列表
	 * @Param: [visitorId]
	 * @Return: java.util.List<com.jsy.community.entity.VisitorPersonRecordEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	List<VisitorPersonRecordEntity> queryFollowPersonListByVisitorId(Long visitorId);
	
	/**
	* @Description: 批量新增访客进出记录
	 * @Param: [jsonObject]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/2
	**/
	void addVisitorRecordBatch(JSONObject jsonObject);
	
	/**
	* @Description: 陌生人脸上传
	 * @Param: [jsonObject]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-08-02
	**/
	void saveStranger(JSONObject jsonObject);
	
	/**
	* @Description: 陌生人记录 分页查询
	 * @Param: [qo]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.VisitorStrangerEntity>
	 * @Author: chq459799974
	 * @Date: 2021-08-02
	**/
	PageInfo<VisitorStrangerEntity> queryStrangerPage(BaseQO<VisitorStrangerEntity> qo);

	/**
	 * @author: Pipi
	 * @description: 添加访客邀请
	 * @param visitorEntity: 访客表实体
	 * @return: java.lang.Integer
	 * @date: 2021/8/13 14:17
	 **/
	Integer addVisitor(VisitorEntity visitorEntity);

	/**
	 * @author: Pipi
	 * @description: 查询社区未同步的人脸信息
	 * @param communityId: 社区ID
	 * @param facilityId: 设备序列号
	 * @return: java.util.List<com.jsy.community.entity.VisitorStrangerEntity>
	 * @date: 2021/8/19 15:08
	 **/
	List<VisitorEntity> queryUnsyncFaceUrlList(Long communityId, String facilityId);

	/**
	 * @author: Pipi
	 * @description: 批量更新访客人脸同步状态
	 * @param ids: 访客ID列表
	 * @return: void
	 * @date: 2021/8/20 15:55
	 **/
	void updateFaceUrlSyncStatus(List<Long> ids);

	/**
	 * @author: arli
	 * @description: 根据车牌查询车辆是否被邀请
	 **/

	boolean selectCarNumberIsNoInvite(String carNumber,Long communityId,Integer status);

	/**
	 * @author: arli
	 * @description: 改变入院状态
	 *
	 **/

	boolean updateCarStatus(String carNumber,Long communityId,Integer status);
}
