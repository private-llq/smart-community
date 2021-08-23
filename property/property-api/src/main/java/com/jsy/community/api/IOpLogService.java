package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.OpLogEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.OpLogQO;
import com.jsy.community.utils.PageInfo;

/**
 * @author DKS
 * @description 用户操作日志
 * @since 2021/8/21  14:34
 **/
public interface IOpLogService extends IService<OpLogEntity> {
	/**
	 * @Description: 保存用户操作日志
	 * @Param: [OpLogEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/21
	 **/
	void saveOpLog(OpLogEntity opLogEntity);
	
	/**
	 * @Description: 操作日志分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.OpLogEntity>
	 * @Author: DKS
	 * @Date: 2021/08/23 11:56
	 **/
	PageInfo<OpLogEntity> queryOpLogPage(BaseQO<OpLogQO> baseQO);
}
