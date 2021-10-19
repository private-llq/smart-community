//package com.jsy.community.service;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.jsy.community.entity.admin.AdminUserEntity;
//import com.jsy.community.qo.BaseQO;
//import com.jsy.community.qo.admin.AdminUserQO;
//import com.jsy.community.utils.PageInfo;
//
//
///**
// * 系统用户
// */
//public interface IAccountService extends IService<AdminUserEntity> {
//	/**
//	* @Description: 操作员条件查询
//	 * @Param: [baseQO]
//	 * @Return: com.jsy.community.utils.PageInfo
//	 * @Author: chq459799974
//	 * @Date: 2021/3/16
//	**/
//	PageInfo queryOperator(BaseQO<AdminUserQO> baseQO);
//
//	/**
//	* @Description: 添加操作员
//	 * @Param: [adminUserEntity]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2021/3/17
//	**/
//	void addOperator(AdminUserEntity adminUserEntity);
//
//	/**
//	* @Description: 编辑操作员
//	 * @Param: [adminUserEntity]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2021/3/18
//	**/
//	void updateOperator(AdminUserEntity adminUserEntity);
//
//	/**
//	 * @Description: 删除操作员
//	 * @author: DKS
//	 * @since: 2021/10/13 15:38
//	 * @Author: DKS
//	 * @Date: 2021/10/13
//	 */
//	void deleteOperator(Long id);
//}
