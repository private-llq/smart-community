package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MarketCategoryMapper extends BaseMapper<ProprietorMarketCategoryEntity> {
	
	/**
	 * @Description: 更新顺序
	 * @author: DKS
	 * @since: 2022/1/5 15:21
	 * @Param: [sort]
	 * @return:
	 */
	void updateSort(@Param("sort") Integer sort);
}
