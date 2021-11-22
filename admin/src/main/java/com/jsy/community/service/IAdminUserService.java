package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.utils.PageInfo;


/**
 * @author DKS
 * @description 账号管理
 * @since 2021-11-18 16:24
 **/
public interface IAdminUserService extends IService<AdminUserEntity> {
	
	/**
	 * @Description: 操作员条件查询
	 * @author: DKS
	 * @since: 2021/11/19 14:29
	 * @Param: [baseQO]
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.admin.AdminUserEntity>
	 */
	PageInfo<AdminUserEntity> queryOperator(BaseQO<AdminUserQO> baseQO);
	
	/**
	 * @Description: 添加操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [adminUserEntity]
	 * @return: void
	 */
	void addOperator(AdminUserEntity adminUserEntity);
	
	/**
	 * @Description: 编辑操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [adminUserEntity]
	 * @return: void
	 */
	void updateOperator(AdminUserEntity adminUserEntity);
	
	/**
	 * @Description: 删除操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [id]
	 * @return: void
	 */
	void deleteOperator(Long id);
	
	/**
	 * @Description: 根据手机号检查小区用户是否已存在(t_admin_user)
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [mobile]
	 * @return: boolean
	 */
	boolean checkUserExists(String mobile);
	
	/**
	 * @Description: 修改手机号
	 * @author: DKS
	 * @since: 2021/11/19 16:56
	 * @Param: [newMobile, oldMobile]
	 * @return: boolean
	 */
	boolean changeMobile(String newMobile,String oldMobile);
}
