package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityEntity;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 社区 Mapper 接口
 * </p>
 *
 * @author jsy
 * @since 2020-11-25
 */
public interface CommunityMapper extends BaseMapper<CommunityEntity> {

	/**
	* @Description: 查询社区模式
	 * @Param: [id]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/1/21
	**/
	@Select("select house_level_mode from t_community where id = #{id}")
	Integer getCommunityMode(Long id);
	
}
