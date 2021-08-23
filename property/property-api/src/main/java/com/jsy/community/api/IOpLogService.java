package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.OpLogEntity;

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
}
