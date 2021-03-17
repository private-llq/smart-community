package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 业主实体接口 - 业主信息实体
 * @author YuLF
 * @since  2020/3/11 10:21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_proprietor")
@ApiModel(value="Proprietor实体对象", description="业主信息")
public class ProprietorEntity extends BaseEntity{

    @ApiModelProperty("社区id")
    private Long communityId;


    @ApiModelProperty("房屋id")
    private Long houseId;


    @ApiModelProperty("微信")
    private String wechat;


    @ApiModelProperty("qq")
    private String qq;


    @ApiModelProperty("邮箱")
    private String email;


    @ApiModelProperty("电话号码")
    private String mobile;


    @ApiModelProperty("真实姓名")
    private String realName;


    @ApiModelProperty("证件号码")
    private String idCard;


    @ApiModelProperty("创建人")
    private String createBy;


    @ApiModelProperty("更新人")
    private String updateBy;


    @ApiModelProperty("证件类型：1.身份证 2.护照")
    private Integer identificationType = 1;


    @ApiModelProperty("房屋编号,用于excel导入")
    @TableField( exist = false)
    private String houseNumber;

}
