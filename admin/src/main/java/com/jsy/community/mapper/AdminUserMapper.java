package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.qo.admin.AdminUserQO;
import org.apache.ibatis.annotations.*;

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
	
	/**
	 * @Description: 根据手机号查询用户数
	 * @Param: [mobile]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	 **/
	@Select("select id from t_admin_user where deleted = 0 and mobile = #{mobile} limit 1")
	Long countUser(String mobile);
	
	/**
	 * @Description: 更换手机号
	 * @Param: [newMobile, uid]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	 **/
	@Update("update t_admin_user set mobile = #{newMobile} where mobile = #{oldMobile}")
	int changeMobile(@Param("newMobile")String newMobile, @Param("oldMobile")String oldMobile);
	
	/**
	 * @author: Pipi
	 * @description: 根据用户uid查询单条物业公司ID
	 * @param uid: 用户uid
	 * @return: java.lang.Long
	 * @date: 2021/7/29 11:13
	 **/
	@Select("select ar.company_id from t_admin_user_role ur left join t_admin_role ar on ar.id = ur.role_id where ur.uid = #{uid} limit 1")
	Long queryCompanyId(String uid);
	
	/**
	 * @Description: 根据物业公司id查出属于物业公司的Uid
	 * @author: DKS
	 * @since: 2021/11/19 14:56
	 * @Param: [companyIds]
	 * @return: list
	 */
	List<String> queryUidByCompanyIds(@Param("list") List<Long> companyIds);
	
	/**
	 * @Description: 根据uid查询角色id
	 * @author: DKS
	 * @since: 2021/11/19 15:50
	 * @Param:  uid
	 * @return: Long
	 */
	@Select("select role_id from t_admin_user_role where uid = #{uid}")
	Long queryRoleIdByUid(String uid);
	
	/**
	 * @Description: 根据角色id查找所有菜单名称
	 * @author: DKS
	 * @since: 2021/11/19 15:45
	 * @Param: roleId
	 * @return: list
	 */
	@Select("select name from t_admin_menu where id in (select menu_id from t_admin_role_menu where role_id = #{roleId})")
	List<String> queryMenuNameByRoleId(Long roleId);
}
