package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RepairOrderQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 报修订单信息 Mapper 接口
 * </p>
 *
 * @author jsy
 * @since 2020-12-08
 */
public interface RepairOrderMapper extends BaseMapper<RepairOrderEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.vo.repair.RepairOrderVO>
	 * @Author lihao
	 * @Description 分页查询报修订单
	 * @Date 2021/3/17 10:31
	 * @Param [repairOrderQO]
	 **/
	List<RepairOrderEntity> listRepairOrder(Page<RepairOrderEntity> pageInfo, @Param("qo") BaseQO<RepairOrderQO> repairOrderQO);
}
