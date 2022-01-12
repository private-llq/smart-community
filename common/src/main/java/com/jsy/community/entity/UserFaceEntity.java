package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * @Author: Pipi
 * @Description: 用户人脸数据实体
 * @Date: 2021/12/22 15:20
 * @Version: 1.0
 **/
@TableName("t_user_face")
@Data
public class UserFaceEntity extends BaseEntity {
    /**
     * 人脸照片路径
     */
    private String faceUrl;
    
    /**
     * 手机
     */
    @TableField( exist = false )
    private String mobile;
    
    /**
     * 用户名
     */
    @TableField( exist = false )
    private String realName;

    /**
     * 用户uid
     */
    private String uid;

    /**
     * 人脸启用状态;1:启用;2:禁用
     */
    private Integer faceEnableStatus;

    @TableField( exist = false )
    private String faceEnableStatusStr;

    @ApiModelProperty("家属关系code")
    @TableField( exist = false )
    private Integer relationCode;

    @TableField( exist = false )
    private String relationStr;

    // 与业主关系 1.业主 6.亲属，7租户
    @TableField( exist = false )
    private Set<String> relationSet;

    // 下发状态;1:失败(未完整同步);2;成功
    @TableField( exist = false )
    private String distributionStatusStr;

}
