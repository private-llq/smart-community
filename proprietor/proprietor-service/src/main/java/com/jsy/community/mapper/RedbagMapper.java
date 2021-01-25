package com.jsy.community.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.RedbagEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chq459799974
 * @description 红包
 * @since 2021-01-18 13:17
 **/
public interface RedbagMapper extends BaseMapper<RedbagEntity> {
	
	/**
	* @Description: 查询过期红包/转账
	 * @Param: []
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/1/25
	**/
	@Select("select uuid from t_redbag \n" +
		"where status != 2 and status != -1 \n" +
		"and datediff(now(),create_time) > 0 ")
	List<String> queryExpiredRedbag();
	
}
