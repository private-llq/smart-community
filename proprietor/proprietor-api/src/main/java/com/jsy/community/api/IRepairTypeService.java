package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.RepairTypeEntity;

import java.util.List;

/**
 * <p>
 * 报修类别 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-12-25
 */
public interface IRepairTypeService extends IService<RepairTypeEntity> {
	
	List<RepairTypeEntity> getType();
}
