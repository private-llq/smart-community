package com.jsy.community.utils.imutils.entity;

import lombok.Data;


/**
 * 推送app通知消息
 *
 * @author lxjr
 * @date 2021/8/18 14:37
 */
@Data
public class PushAppMsg {
    /**
     * 消息的类型
     * 1 文本 + 详情 + 子详情 + 内容
     */
//    @NotNull(message = "不能为空")
    private Integer type;
    /**
     * 消息
     */
//    @NotBlank(message = "不能为空")
    private String appMsg;

    /**
     * 发送者的信息，和接受者的信息
     */
//    @NotNull(message = "不能为空")
    private SendInfo sendInfo;
}
