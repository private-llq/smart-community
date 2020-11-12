package com.jsy.community.qo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 来访人员
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-11
 */
@Data
@ApiModel(value="TVisitor查询、修改入参对象", description="来访人员")
public class VisitorQO implements Serializable{

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "来访人姓名")
    private String name;

    @ApiModelProperty(value = "所属单元")
    private String unit;

    @ApiModelProperty(value = "所属楼栋")
    private String building;

    @ApiModelProperty(value = "所属楼层")
    private String floor;

    @ApiModelProperty(value = "所属门牌号")
    private String door;

    @ApiModelProperty(value = "来访人联系方式")
    private String contact;

}
