package com.jsy.community.utils.imutils.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lxjr
 * @date 2021/6/29 10:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {

    /**
     * 账号
     */
//    @NotBlank(message = "不能为空")
    public String imId;
    /**
     * 昵称
     * 可为null，将使用默认昵称
     */
    private String nickName;
    /**
     * 密码
     * 可为null，将使用默认的url
     */
//    @NotBlank(message = "不能为空")
    private String password;
    /**
     * 标识（手机号 邮箱 账号密码或第三方应用的唯一标识）
     */
    private String identifier = "1";
    /**
     * 头像 - 缩略图
     * 可为null，将使用默认的url
     */
    private String headImgSmallUrl;
    /**
     * 头像 - 原图
     * 可为null，将使用默认的url
     */
    private String headImgMaxUrl;

    public RegisterDto(String imId, String password, String identifier) {
        this.imId = imId;
        this.password = password;
        this.identifier = identifier;
    }
}
