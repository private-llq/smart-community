package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyAdvanceDepositRecordEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author DKS
 * @description 物业预存款余额明细记录表
 * @since 2021/8/12  13:53
 **/
public interface PropertyAdvanceDepositRecordMapper extends BaseMapper<PropertyAdvanceDepositRecordEntity> {
	/**
	 * @Description: 根据预存款id查询是否新增
	 * @Param: [id]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: DKS
	 * @Date: 2021/8/12
	 **/
	@Select("select * from t_property_advance_deposit_record where advance_deposit_id = #{id} and community_id = #{communityId} and deleted = 0")
	List<PropertyAdvanceDepositRecordEntity> queryAdvanceDepositRecordList(Long id, Long communityId);
	
	/**
	 * @Description: 查询最新时间的一条记录
	 * @Param: [id]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: DKS
	 * @Date: 2021/8/12
	 **/
	PropertyAdvanceDepositRecordEntity queryMaxCreateTimeRecord(Long advanceDepositId, Long communityId);
	
	/**
	 *@Author: DKS
	 *@Description: 导入充值余额明细记录
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/16 11:51
	 **/
	Integer saveAdvanceDepositRecord(@Param("list") List<PropertyAdvanceDepositRecordEntity> propertyAdvanceDepositRecordEntityList);
}
