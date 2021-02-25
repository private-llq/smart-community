package com.jsy.community.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 行驶证识别 相关属性
 * @author YuLF
 * @since 2021-02-24 17:41
 */
@Getter
@Component
public class DrivingLicense {
    /**
     * 行驶证识别请求API
     */
    @Value("${jsy.drivinglicense.api}")
    private String api;
    /**
     * 行驶证识别请求api路径
     */
    @Value("${jsy.drivinglicense.path}")
    private String path;
    /**
     * 行驶证识别请求方式
     */
    @Value("${jsy.drivinglicense.method}")
    private String method;
    /**
     * 行驶证识别请求  code
     */
    @Value("${jsy.drivinglicense.appCode}")
    private String appCode;
    /**
     * 行驶证识别 错误码 为接口提供方的错误码
     * 错误码枚举
     * @author YuLF
     * @since  2020/12/10 10:02
     */
    public enum ErrorCode {
        /**
         * 阿里云行驶证识别接口说明
         * https://market.aliyun.com/products/57124001/cmapi025148.html?spm=5176.2020520132.101.3.6f377218LcmcqW#sku=yuncode1914800000
         */
        NULL("图片为空", 40001),
        URL_INVALID("无效的图片URL", 40002),
        URL_TIMEOUT("图片URL请求超时", 40003),
        PIC_BIG("图片大小超过4M", 40004),
        FORMAT_ERROR("图片格式错误", 40005),
        IDENTIFY_FAIL("行驶证识别失败", 40006),
        IDENTIFY_ERROR("行驶证识别错误", 20001),
        IDENTIFY_SUCCESS("行驶证识别成功", 200);
        private final String msg;
        private final Integer code;

        ErrorCode(String msg, Integer code) {
            this.msg = msg;
            this.code = code;
        }

        public static ErrorCode valueOf(int code) {
            ErrorCode result = null;
            ErrorCode[] var1 = values();
            for(ErrorCode errorCode : var1){
                if(errorCode.code == code){
                    result  = errorCode;
                }
            }
            return result;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}

