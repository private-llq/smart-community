package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: APP活动报名人员
 * @author: Hu
 * @create: 2021-08-13 14:32
 **/
@Data
@TableName("t_activity_user")
public class ActivityUserEntity implements Serializable {

    /**
     * ID
     */
    private Long id;
    /**
     * 用户uid
     */
    private String uid;
    /**
     * 活动id
     */
    private Long activityId;
    /**
     * 报名人员电话
     */
    private String mobile;
    /**
     * 报名人员名称
     */
    private String name;


}
