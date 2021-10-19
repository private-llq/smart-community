package com.jsy.community.vo.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * 房租租赁合同存证信息
 */
public class LeaseContractInfoVO {
    @ApiModelProperty
    private Long id;

    @ApiModelProperty("合同内容")
    private String contractContent;

    @ApiModelProperty("甲方签名")
    private String companySignature;

    @ApiModelProperty("甲方联系方式")
    private String companyMobile;

    @ApiModelProperty("甲方签约日期")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime companySignTime;

    @ApiModelProperty("乙方签名")
    private String supplierSignature;

    @ApiModelProperty("乙方联系方式")
    private String supplierMobile;

    @ApiModelProperty("乙方签约日期")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime supplierSignTime;

    @ApiModelProperty("甲方身份证/营业执照 正面")
    private String companyLicensePositive;

    @ApiModelProperty("甲方身份证/营业执照 反面")
    private String companyLicenseReverse;

    @ApiModelProperty("乙方身份证/营业执照 正面")
    private String supplierLicensePositive;

    @ApiModelProperty("乙方身份证/营业执照 反面")
    private String supplierLicenseReverse;
}
