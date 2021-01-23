package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IProprietorLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.log.ProprietorLog;
import com.jsy.community.mapper.ProprietorLogMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 业主操作日志 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2021-01-22
 */
@DubboService(version = Const.version, group = Const.group_property)
public class ProprietorLogServiceImpl extends ServiceImpl<ProprietorLogMapper, ProprietorLog> implements IProprietorLogService {
	
	@Autowired
	private ProprietorLogMapper proprietorLogMapper;
	
	@Override
	public void saveProprietorLog(ProprietorLog proprietorLog) {
		proprietorLogMapper.insert(proprietorLog);
	}
}
