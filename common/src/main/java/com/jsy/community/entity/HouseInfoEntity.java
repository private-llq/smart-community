package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 房间推送消息
 * @author: Hu
 * @create: 2021-10-13 16:13
 **/
@Data
@TableName("t_house_info")
public class HouseInfoEntity implements Serializable {
    private String id;
    /**
     * 电话
     */
    private String mobile;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String desc;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 过期时间
     */
    private LocalDateTime overdueTime;
    /**
     * 业主uid
     */
    private String yzUid;
    /**
     * 用户uid
     */
    private String yhUid;

}
