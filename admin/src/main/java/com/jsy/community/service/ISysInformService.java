package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.property.PushInfromVO;

/**
 * @author YuLF
 * @since 2020-12-21 11:39
 */
public interface ISysInformService extends IService<PushInformEntity> {
	/**
	 * @Description: 大后台推送消息分页查询
	 * @author: DKS
	 * @since: 2021/11/17 14:58
	 * @Param: [baseQO]
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.PushInformEntity>
	 */
	PageInfo<PushInformEntity> querySysInform(BaseQO<PushInformQO> baseQO);
	
	/**
	 * @Description: 添加大后台推送消息
	 * @author: DKS
	 * @since: 2021/11/17 15:00
	 * @Param: [qo]
	 * @return: java.lang.Boolean
	 */
    Boolean addPushInform(PushInformQO qo);
	
	/**
	 * @Description: 删除推送通知消息
	 * @author: DKS
	 * @since: 2021/11/17 15:01
	 * @Param: [id, updateAdminId]
	 * @return: java.lang.Boolean
	 */
	Boolean deletePushInform(Long id, String updateAdminId);
	
	/**
	 * @Description: 获取单条消息详情
	 * @author: DKS
	 * @since: 2021/11/17 15:01
	 * @Param: [id]
	 * @return: com.jsy.community.vo.property.PushInfromVO
	 */
	PushInfromVO getDetail(Long id);
}
