package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 社区配置
 * @since 2021-05-08 10:43
 **/
@Data
@TableName("t_community_config")
public class CommunityConfigEntity implements Serializable {
	private Long communityId;//社区ID
	private Integer showSysMsg;//是否展示系统消息 1.展示 0.不展示
	private Integer showSysBanner;//是否展示系统广告(轮播图) 1.展示 0.不展示
}
