package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.qo.admin.AdminUserQO;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 系统用户
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUserEntity> {
	
	/**
	 * @Description: 备份用户角色
	 * @Param: [userId]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Select("select role_id from t_admin_user_role where user_id = #{userId}")
	List<Long> getUserRole(Long userId);
	
	/**
	 * @Description: 清空用户角色
	 * @Param: [userId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Delete("delete from t_admin_user_role where user_id = #{userId}")
	void clearUserRole(Long userId);
	
	/**
	 * @Description: 批量添加用户角色
	 * @Param: [list, userId]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	int addUserRoleBatch(@Param("list") List<Long> list, @Param("userId") Long userId);
	
	/**
	 * 查询用户的所有权限
	 *
	 * @param userId 用户ID
	 */
	List<String> queryAllPerms(Long userId);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);
	
//	/**
//	 * 根据用户名，查询系统用户
//	 */
//	AdminUserEntity queryByUserName(String username);

//	/**
//	 * 根据邮箱，查询系统用户
//	 */
//	AdminUserEntity queryByEmail(String email);

//	/**
//	 * 根据手机号，查询系统用户
//	 */
//	AdminUserEntity queryByMobile(String mobile);
	
	//========资料或账户相关start ==========
	/**
	* @Description: uid查用户信息
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.admin.AdminUserEntity
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Select("select * from t_admin_user where uid = #{uid}")
	AdminUserEntity queryByUid(String uid);
	
	/**
	* @Description: uid查手机号
	 * @Param: [uid]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Select("select mobile from t_admin_user where uid = #{uid}")
	String queryMobileByUid(String uid);
	
	/**
	 * @Description: uid批量查姓名
	 * @Param: [list]
	 * @Return: com.jsy.community.entity.admin.AdminUserEntity
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	 **/
	@MapKey("uid")
	Map<String, Map<String,String>> queryNameByUidBatch(Collection<String> uidList);
	
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
	* @Description: 查询登录用户(操作员)已加入小区idList
	 * @Param: [mobile]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Select("select community_id from t_admin_user where mobile = #{mobile} and deleted = 0")
	List<Long> queryCommunityIdListByMobile(String mobile);
	
	/**
	* @Description: 更新用户头像
	 * @Param: [url, uid]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Update("update t_admin_user set avatar_url = #{url} where uid = #{uid}")
	int updateAvatar(@Param("url")String url,@Param("uid")String uid);
	
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
	@Select("select company_id from t_admin_user_company where uid = #{uid} limit 1")
	Long queryCompanyId(String uid);

	//========资料或账户相关start ==========
	
	//========操作员增删改查start ==========
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
	* @Description: 根据手机号查询用户数
	 * @Param: [mobile]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	**/
	@Select("select id from t_admin_user where deleted = 0 and mobile = #{mobile} limit 1")
	Long countUser(String mobile);
	//========操作员增删改查end ==========
	
	//========用户拓展start ==========
	/**
	 * @return java.util.List<com.jsy.community.entity.admin.AdminUserEntity>
	 * @Author lihao
	 * @Description 根据条件[员工姓名,编号,所在组织]查询报修人员
	 * @Date 2021/4/2 13:40
	 * @Param []
	 **/
	List<Map<String, Object>> getRepairPerson(@Param("condition")String condition, @Param("communityId") Long communityId);
	//========用户拓展end ==========
	
	/**
	 * @Description: 获取uid根据姓名模糊查询
	 * @Param: [userName]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: DKS
	 * @Date: 2021/8/23 14:16
	 **/
	List<String> queryUidListByRealName(@Param("userName")String userName);

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
