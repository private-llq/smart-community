package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
public class BaseEntity implements Serializable {
	@TableId
	private Long id;
	
	@TableLogic
	private Integer deleted;
	
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;
	
	@TableField(fill = FieldFill.UPDATE)
	private LocalDateTime updateTime;
}
