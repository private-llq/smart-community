package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description im用户实体类
 * @since 2021-01-15 09:22
 **/
@Data
@TableName("t_user_im")
public class UserIMEntity implements Serializable {
	
	@ApiModelProperty(value = "用户id")
	private String uid;
	
	@ApiModelProperty(value = "idID")
	private String imId;

	@ApiModelProperty(value = "id密码")
	private String imPassword;
	
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;
	
}
