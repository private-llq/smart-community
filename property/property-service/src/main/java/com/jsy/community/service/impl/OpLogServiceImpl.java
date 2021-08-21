package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IOpLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.OpLogEntity;
import com.jsy.community.mapper.OpLogMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author DKS
 * @description 用户操作日志
 * @since 2021/8/21  14:44
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class OpLogServiceImpl extends ServiceImpl<OpLogMapper, OpLogEntity> implements IOpLogService {
	
	@Autowired
	private OpLogMapper opLogMapper;
	
	/**
	 * @author DKS
	 * @description 用户操作日志AOP
	 * @since 2021/8/21  14:45
	 **/
	@Override
	public boolean saveOpLog(OpLogEntity opLogEntity) {
		int row;
		opLogEntity.setId(SnowFlake.nextId());
		row = opLogMapper.insert(opLogEntity);
		return row == 1;
	}
}
