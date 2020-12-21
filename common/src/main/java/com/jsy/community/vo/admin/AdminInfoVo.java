package com.jsy.community.vo.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.vo.HouseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
	 * uid
	 */
	private String uid;
	
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
