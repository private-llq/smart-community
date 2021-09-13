package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 物业公司支付配置
 * @author: Hu
 * @create: 2021-09-10 14:15
 **/
@Data
@TableName("t_company_pay_config")
public class CompanyPayConfigEntity extends BaseEntity {
    /**
     * 物业公司id
     */
    private Long companyId;
    /**
     * 应用号
     */
    private String appId;
    /**
     * 应用秘钥
     */
    private String appSecret;
    /**
     * 商户号
     */
    private String mchId;
    /**
     * 商户私钥
     */
    private String privateKey;
    /**
     * 回调apiv3秘钥
     */
    private String apiV3;
    /**
     * 私钥路径
     */
    private String apiclientKeyUrl;

    /**
     * 私钥  0表示未上传，1表示已上传
     */
    @TableField(exist = false)
    private Integer apiclientKeyStatus;
    /**
     * 公钥路径
     */
    private String apiclientCertUrl;

    /**
     * 公钥  0表示未上传，1表示已上传
     */
    @TableField(exist = false)
    private Integer apiclientCertStatus;
    /**
     * 证书编号
     */
    private String mchSerialNo;

    /**
     * 是否允许退款，默认为1 1允许退款，2不允许退款
     */
    private Integer refundStatus;

}
