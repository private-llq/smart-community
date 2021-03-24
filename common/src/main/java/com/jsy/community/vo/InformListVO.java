package com.jsy.community.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author YuLF
 * @since 2020-12-14 17:43
 * 社区消息列表返回对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformListVO extends BaseVO {

    //消息名称
    private String name;

    //头像图片名称
    private String avatarUrl;

    //未读消息总量
    private Integer unread;

    //未读消息第一条 创建时间
    private String unreadInformCreateTime;

    //未读消息第一条 标题
    private String unreadInformTitle;

}
