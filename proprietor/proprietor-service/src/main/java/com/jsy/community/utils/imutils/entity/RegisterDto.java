package com.jsy.community.utils.imutils.entity;

import com.jsy.community.utils.imutils.open.StringUtils;
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
    private static String[] names = {"靖衡", "维亨", " 城蕾", " 艾韵", " 汛如", " 维朋", " 康博", " 伯胜", " 顺斌", " 壹燕", " 跃吉", " 伟威", " 长莉", " 培烘", " 森远", " 均贺", " 漫绮", " 雨澜", " 一稼", " 笑蔚", " 舒岑", " 显旗", " 享益", " 广炎", " 鹤嘉", " 昀凌", " 稼俊", " 柯雅", " 艺堂", " 启赢", " 极峰", " 陵雅", " 历远", " 凌箫", " 沛俊", " 兴万", " 尉城", " 必先", " 俊薪", " 世银", " 哲翡", " 相中", " 念桐", " 凤路", " 值瑜", " 斯嘉", " 前路", " 瑶泽", " 沁楚", " 铠维", " 睿峻", " 启朋", " 秦彰", " 亮树", " 哲严", " 仲锦", " 远淮", " 静睿", " 芯培", " 士鸿", " 宝瑶", " 惠辰", " 植园", " 庭婕", " 运程", " 钟钦", " 瑾芝", " 增誉", " 千友", " 聆先", " 培胜", " 荣腾", " 炳润", " 钟策", " 云雨", " 忆和", " 良湘", " 学启", " 伟科", " 逸昊", " 渭云", " 立晋", " 天青", " 沐翰", " 鼎峙", " 弋歌", " 州炳", " 奕真", " 懿仁", " 杏泉", " 绍天", " 颖宣", " 自瑞", " 正帅", " 胤彰", " 银希", " 民望", " 自甜", " 玖妮", " 夏闰"};
    private String[] headImgSmallUrls = {"headImgSmallUrls.png"};
    private String[] headImgMaxUrls = {"headImgMaxUrls.png"};

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

    public String getNickName() {
        if (StringUtils.isEmpty(nickName)) {
            this.nickName = names[(int) (Math.random() * names.length)];
        }
        return nickName;
    }

    public String getHeadImgSmallUrl() {
        if (StringUtils.isEmpty(headImgSmallUrl)) {
            this.headImgSmallUrl = headImgSmallUrls[(int) (Math.random() * headImgSmallUrls.length)];
        }
        return headImgSmallUrl;
    }

    public String getHeadImgMaxUrl() {
        if (StringUtils.isEmpty(headImgMaxUrl)) {
            this.headImgMaxUrl = headImgMaxUrls[(int) (Math.random() * headImgMaxUrls.length)];
        }
        return headImgMaxUrl;
    }

    public RegisterDto(String imId, String password, String identifier) {
        this.imId = imId;
        this.password = password;
        this.identifier = identifier;
    }
}
