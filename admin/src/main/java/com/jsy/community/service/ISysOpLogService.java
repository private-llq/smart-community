package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SysOpLogEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.OpLogQO;
import com.jsy.community.utils.PageInfo;

/**
 * @author DKS
 * @description 用户操作日志
 * @since 2021/10/20  10:41
 **/
public interface ISysOpLogService extends IService<SysOpLogEntity> {
	/**
	 * @Description: 保存用户操作日志
	 * @Param: [SysOpLogEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @since 2021/10/20  10:57
	 **/
	void saveOpLog(SysOpLogEntity sysOpLogEntity);
	
	/**
	 * @Description: 操作日志分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.SysOpLogEntity>
	 * @Author: DKS
	 * @since 2021/10/20  10:57
	 **/
	PageInfo<SysOpLogEntity> queryOpLogPage(BaseQO<OpLogQO> baseQO);
}
