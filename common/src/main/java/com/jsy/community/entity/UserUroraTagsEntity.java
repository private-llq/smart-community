package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chq459799974
 * @description 用户极光推送tags (注：若后续增加了除社区以外的其他类型tags,查询全部tag时需要拼在一起返回)
 * @since 2021-01-14 11:25
 **/
@Data
@TableName("t_user_urora_tags")
public class UserUroraTagsEntity extends BaseEntity{
	
	@TableField(exist = false)
	private Long id;
	
	@TableField(exist = false)
	private Integer deleted;
	
	@ApiModelProperty(value = "用户id")
	private String uid;
	
	@ApiModelProperty(value = "社区tag")
	private String communityTags;
	
	@ApiModelProperty(value = "极光推送全部类型标签总和")
	private String uroraTags;
	
}
