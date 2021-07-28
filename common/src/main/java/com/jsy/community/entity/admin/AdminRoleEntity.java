package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author chq459799974
 * @description 系统角色
 * @since 2020-12-14 15:22
 **/
@Data
@TableName("t_admin_role")
public class AdminRoleEntity extends BaseEntity {
	
	private Long communityId;//社区ID
	
	@NotBlank(message = "角色名不能为空")
	private String name;//角色名
	private String remark;//备注
	
	private Long createBy;//创建人
	private Long updateBy;//修改人
	
	@TableField(exist = false)
	private List<Long> menuIds;//菜单ID列表

}
