package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @author DKS
 * @description 用户与物业公司关联
 * @since 2021-12-16 15:09
 **/
@Data
@TableName("t_admin_user_company")
public class AdminUserCompanyEntity extends BaseEntity {
	/**
	 * 用户uid
	 */
	private String uid;
	/**
	 * 物业公司id
	 */
	private Long companyId;
}
