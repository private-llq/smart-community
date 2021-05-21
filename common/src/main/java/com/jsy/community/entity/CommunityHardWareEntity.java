package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 社区硬件实体
 * @since 2021-04-25 13:41
 **/
@TableName("t_community_hardware")
public class CommunityHardWareEntity implements Serializable {
	private Long communityId;//社区ID
	private Integer hardwareType;//硬件类型 1.炫优人脸识别一体机
	private String hardwareId;//硬件id
}
