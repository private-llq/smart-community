package com.jsy.community.entity.log;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 业主操作日志
 * </p>
 *
 * @author lihao
 * @since 2021-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_proprietor_log")
@ApiModel(value="ProprietorLog对象", description="业主操作日志")
@ToString
public class ProprietorLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作人姓名")
    private String name;

    @ApiModelProperty(value = "操作人所在城市")
    private String city;

    @ApiModelProperty(value = "操作人所在社区")
    private String community;

    @ApiModelProperty(value = "操作人电话")
    private String phone;

    @ApiModelProperty(value = "访问ip")
    private String ipAddress;

    @ApiModelProperty(value = "功能描述")
    private String introduce;

    @ApiModelProperty(value = "请求地址")
    private String url;

    @ApiModelProperty(value = "请求方式")
    private String requestMethod;

    @ApiModelProperty(value = "请求模块")
    private String module;

    @ApiModelProperty(value = "请求结果 0 成功 1失败")
    private Integer status;

    @ApiModelProperty(value = "请求参数")
    private String parameter;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "总耗时 毫秒(单位)")
    private Long runtime;

    @ApiModelProperty(value = "异常信息")
    private String exceptionInfo;

}
