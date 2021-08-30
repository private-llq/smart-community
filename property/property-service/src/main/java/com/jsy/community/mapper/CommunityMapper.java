package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
	
	/**
	* @Description: ids批量查小区
	 * @Param: [list]
	 * @Return: java.util.List<com.jsy.community.entity.CommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	List<CommunityEntity> queryCommunityBatch(List<Long> list);
	
	/**
	* @Description: 查询所有小区ID
	 * @Param: []
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/6/26
	**/
	@Select("select id from t_community where deleted = 0")
	List<Long> queryAllCommunityIdList();
	
	
	/**
	 * @Description: 通过物业id查询物业所属小区
	 * @Param: [companyId]
	 * @Return: java.util.List<com.jsy.community.entity.CommunityEntity>
	 * @Author: DKS
	 * @Date: 2021/8/25
	 **/
	List<CommunityEntity> queryCommunityByCompanyId(Long companyId);
}
