package com.jsy.community.vo.proprietor;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/12/16 16:15
 * @Version: 1.0
 **/
@Data
public class ThirdPlatformInfoVO implements Serializable {
    /**
     * 自增id
     */
    @TableId
    private Long id;

    /**
     * 第三方唯一标识
     */
    private String thirdPlatformId;

    /**
     * 第三方唯账号昵称
     */
    private String nickName;

    /**
     * 第三方唯账号头像
     */
    private String avatarUrl;

    /**
     * 三方平台类型
     * WECHAT
     * ALIPAY
     * IOS
     */
    private String thirdPlatformType;

    private String thirdPlatformTypeString;

    private Boolean thirdPlatformBindStatus;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 东八区 - 最近一次修改时间
     */
    private Date utcUpdate;

    /**
     * 东八区 - 创建时间
     */
    private String utcCreate;
}
