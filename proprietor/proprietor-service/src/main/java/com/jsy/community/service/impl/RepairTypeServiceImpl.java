package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IRepairTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RepairTypeEntity;
import com.jsy.community.mapper.RepairTypeMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 报修类别 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-25
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class RepairTypeServiceImpl extends ServiceImpl<RepairTypeMapper, RepairTypeEntity> implements IRepairTypeService {
	
	@Autowired
	private RepairTypeMapper repairTypeMapper;
	
	@Override
	public List<RepairTypeEntity> getType() {
		return repairTypeMapper.selectList(null);
	}
}
