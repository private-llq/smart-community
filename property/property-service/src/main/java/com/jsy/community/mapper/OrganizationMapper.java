package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.OrganizationEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2021-03-15
 */
public interface OrganizationMapper extends BaseMapper<OrganizationEntity> {
	
	/**
	* @Description: 查询组织机构名称
	 * @Param: [id]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Select("select name from t_organization where id = #{id}")
	String queryOrganizationNameById(Long id);
	
	/**
	* @Description: 根据idList批量获取对应组织机构名称
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.Long,java.util.Map<java.lang.Long,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2021/3/16
	**/
	@MapKey("id")
	Map<Long,Map<Long,Object>> queryOrganizationNameByIdBatch(Collection<Long> ids);
}
