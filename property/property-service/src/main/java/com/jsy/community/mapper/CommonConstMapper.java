package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommonConst;

import java.util.List;

/**
 * <p>
 * 公共常量表 Mapper 接口
 * </p>
 *
 * @author jsy
 * @since 2020-12-25
 */
public interface CommonConstMapper extends BaseMapper<CommonConst> {
	
	/**  
	 * @return java.util.List<java.lang.Long>
	 * @Author lihao
	 * @Description 根据类型id查询出常量id
	 * @Date 2021/1/12 14:13
	 * @Param [i] 
	 **/
	List<Long> listByType(Long i);
}
