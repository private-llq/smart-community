package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 公共常量表
 * </p>
 *
 * @author lihao
 * @since 2020-12-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_common_const")
@ApiModel(value="CommonConst对象", description="公共常量表")
public class CommonConst extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "常量名称")
    private String constName;

    @ApiModelProperty(value = "常量数值")
    private Long constNumberValue;

    @ApiModelProperty(value = "常量字符数值")
    private String constStringValue;

    @ApiModelProperty(value = "所属功能编号")
    private Integer typeId;

    @ApiModelProperty(value = "所属功能名")
    private String typeName;

}
