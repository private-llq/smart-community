package com.jsy.community.service;

import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.zhsj.base.api.vo.PageVO;


/**
 * @author DKS
 * @description 账号管理
 * @since 2021-11-18 16:24
 **/
public interface IAdminUserService{
	
	/**
	 * @Description: 操作员条件查询
	 * @author: DKS
	 * @since: 2021/11/19 14:29
	 * @Param: [baseQO]
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.admin.AdminUserEntity>
	 */
	PageVO<AdminUserEntity> queryOperator(BaseQO<AdminUserQO> baseQO);
	
	/**
	 * @Description: 添加操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [adminUserEntity]
	 * @return: void
	 */
	void addOperator(AdminUserQO adminUserQO);
	
	/**
	 * @Description: 编辑操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [adminUserQO]
	 * @return: void
	 */
	void updateOperator(AdminUserQO adminUserQO);
	
	/**
	 * @Description: 删除操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [id]
	 * @return: void
	 */
	void deleteOperator(Long id);
}
