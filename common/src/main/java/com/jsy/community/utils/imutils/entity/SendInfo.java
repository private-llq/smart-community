package com.jsy.community.utils.imutils.entity;

import lombok.Data;


/**
 * @author lxjr
 * @date 2021/8/18 15:54
 */
@Data
public class SendInfo {
    /**
     * 通知ImId（比如 房屋管理的ImId，就是发送人）
     */
//    @NotBlank(message = "不能为空")
    private String fromImId;
    /**
     * 接收人类型
     * 1 只发给一个用户
     * 2 群发本OpenId下注册的所有用户
     * 3 群发该公众号关注的人
     */
//    @NotBlank(message = "不能为空")
    private Integer receiveType;
    /**
     * 接收账号
     * 当receiveType 为1时：这里就是用户的ImId
     * 当receiveType 为2时：（不填也可以）
     * 当receiveType 为3时：（不填也可以）
     */
    private String to;
}
