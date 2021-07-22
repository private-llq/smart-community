package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 物业端管理员社区权限实体
 * @since 2021-07-22 09:38
 **/
@Data
@TableName("t_admin_community")
public class AdminCommunityEntity implements Serializable {
	
	//id
	private Long id;
	//管理员uid
	private String uid;
	//社区id
	private Long communityId;
	
}
