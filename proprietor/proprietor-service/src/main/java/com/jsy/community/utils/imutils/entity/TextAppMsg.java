package com.jsy.community.utils.imutils.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 普通文本消息
 * @author lxjr
 * @date 2021/8/18 15:13
 */
@Data
@NoArgsConstructor
public class TextAppMsg {
    /**
     * 消息标题
     */
//    @NotBlank(message = "不能为空")
    private String title;
    /**
     * 消息描述
     */
//    @NotBlank(message = "不能为空")
    private String desc;
    /**
     * 如果有该url，则消息点击可以跳转，否则点击没有反应
     */
    private String url;
    /**
     * 模板（暂为保留字段）
     */
    private String templateId;
    /**
     * 消息的内容
     */
//    @NotNull(message = "不能为空")
    private String content;
    /**
     * 子集详情
     */
    private List<Links> links;
}
