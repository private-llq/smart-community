package com.jsy.community.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: Pipi
 * @Description: 银联请求日志
 * @Date: 2021/4/10 13:38
 * @Version: 1.0
 **/
@ApiModel("银联请求日志")
@Data
@TableName("t_user_union_pay_log")
public class UnionPayLogEntity {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户uid")
    private String uid;

    @ApiModelProperty("请求参数")
    private String requestParam;

    @ApiModelProperty("请求接口")
    private String interfaceName;

    @ApiModelProperty("请求描述")
    private String description;

    @ApiModelProperty("请求体")
    private String requestBody;

    @ApiModelProperty("响应体")
    private String responseBody;

    @ApiModelProperty("响应码")
    private String code;

    @ApiModelProperty("响应信息")
    private String msg;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
