package com.jsy.community.aspectj;

import lombok.Getter;

/**
 * 数据脱敏类型
 * @author YuLF
 * @since  2021/1/23 10:59
 */
@Getter
public enum DesensitizationType {

    /**
     * 手机号脱敏
     */
    PHONE("11位手机号", "^(\\d{3})\\d{4}(\\d{4})$", "$1****$2"),

    /**
     * 身份证号脱敏
     */
    ID_CARD( "16或者18身份证号", "^(\\d{4})\\d{8,10}(\\w{4})$", "$1**********$2"),
    /**
     * 银行卡号脱敏
     */
    BANK_CARD( "银行卡号", "^(\\d{4})\\d*(\\d{4})$", "$1****$2"),

    /**
     * 姓名脱敏 一律按3位处理，只显示用户姓
     */
    NAME("真实姓名", "(.{1})(.*)(.{0})", "$1**$3"),
    /**
     * 邮箱脱敏
     */
    EMAIL("电子邮箱", "(\\w+)\\w{7}@(\\w+)", "$1*******@$2");

    String describe;
    String[] regex;

    DesensitizationType(String describe, String... regex) {
        this.describe = describe;
        this.regex = regex;
    }
}
