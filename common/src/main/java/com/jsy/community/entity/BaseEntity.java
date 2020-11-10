package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class BaseEntity implements Serializable {
	@TableId
	private Long id;
	
	@TableLogic
	private Integer deleted;
	
	private Date createTime;
	private Date updateTime;
}
