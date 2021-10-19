package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.qo.admin.AdminUserQO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统用户
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUserEntity> {
	/**
	* @Description: 根据id查uid
	 * @Param: [id]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Select("select uid,mobile from t_admin_user where id = #{id} and deleted = 0")
	UserEntity queryUidById(Long id);
	/**
	* @Description: 添加操作员
	 * @Param: [entity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@Insert("insert into t_admin_user(id,uid,number,real_name,status,mobile,id_card,job,org_id,create_by,create_time,role_type) " +
		"values(#{entity.id},#{entity.uid},#{entity.number},#{entity.realName},#{entity.status},#{entity.mobile},#{entity.idCard},#{entity.job},#{entity.orgId},#{entity.createBy},now(),2)")
	int addOperator(@Param("entity") AdminUserEntity entity);
	
	/**
	* @Description: 编辑操作员
	 * @Param: [entity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	int updateOperator(@Param("entity") AdminUserEntity entity);

	/**
	 * @author: Pipi
	 * @description: 操作员条件查询
	 * @param adminUserQO: 查询条件
     * @param startSize: 分页起点
     * @param size: 每页数量
	 * @return: java.util.List<com.jsy.community.entity.admin.AdminUserEntity>
	 * @date: 2021/9/28 10:21
	 **/
	List<AdminUserEntity> queryPageUserEntity(@Param("qo") AdminUserQO adminUserQO,
	                                          @Param("startSize") Long startSize,
	                                          @Param("size") Long size);

	/**
	 * @author: Pipi
	 * @description: 操作员条件查询数量
	 * @param adminUserQO:
	 * @return: java.lang.Integer
	 * @date: 2021/9/28 11:41
	 **/
	Integer countPageUserEntity(@Param("qo") AdminUserQO adminUserQO);
}
