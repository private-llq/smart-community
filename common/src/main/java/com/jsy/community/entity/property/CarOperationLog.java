package com.jsy.community.entity.property;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("车辆模块日志")
@TableName("t_car_operation_log")
public class CarOperationLog implements Serializable {
    private Long communityId;//社区id

    private String userId;//用户id

    private Long id;//id

    private Long userRole;//角色

    private String userName;//用户名

    private String operation;//操作

    private Integer status;//转态0操作失败，1操作成功

    private LocalDateTime operationTime;//操作时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Long deleted;
}
