package com.jsy.community.vo.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jsy.community.entity.admin.AdminMenuEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 物业端用户VO
 * @Author qq459799974
 * @Date 2020/11/16 14:16
 **/
@Data
@ApiModel("物业端用户VO")
public class AdminInfoVo implements Serializable {
	/**
	 * 社区id
	 */
	private Long communityId;
	
	/**
	 * uid
	 */
	private String uid;
	
	/**
	 * 姓名
	 */
	private String realName;
	
	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;
	
	/**
	 * 角色ID列表
	 */
	@TableField(exist = false)
	private List<Integer> roleIdList;
	
	/**
	 * 用户菜单列表
	 */
	@TableField(exist = false)
	private List<AdminMenuEntity> menuList;
	
	/**
	 * token
	 */
	private String token;

}
