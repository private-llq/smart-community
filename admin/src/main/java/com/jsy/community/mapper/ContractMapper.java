package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ContractQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物业端社区楼栋 Mapper 接口
 * @author DKS
 * @since 2021-10-22
 */
@Mapper
public interface ContractMapper extends BaseMapper<AssetLeaseRecordEntity> {
	
	List<AssetLeaseRecordEntity> selectContractPage(@Param("contractQO") BaseQO<ContractQO> ContractQO);
	
	Long getContractPageCount(@Param("contractQO") BaseQO<ContractQO> ContractQO);
}
