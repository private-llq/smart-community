package com.jsy.community.vo.property;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("车辆日志返回对象")
public class CarOperationLogVO implements Serializable {
    private Long id;//id

    private String userRole;//角色

    private String operation;//操作

    private String userId;//用户id

    private String userName;//用户名

    private Integer status;//转态0操作失败，1操作成功

    private LocalDateTime operationTime;//操作时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Long deleted;
}
