package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("返回家属详细信息")
public class RelationVO extends BaseVO {
    @ApiModelProperty(value = "家属姓名",required = true)
    private String name;
    @ApiModelProperty("性别")
    private Integer sex;
    @ApiModelProperty("电话")
    private String mobile;
    @ApiModelProperty(value = "证件类型1.身份证 2.护照",required = true)
    private Integer identificationType;
    @ApiModelProperty(value = "证件类型1.身份证 2.护照",required = true)
    private String identificationTypeText;
    @ApiModelProperty(value = "身份证号码",required = true)
    private String idCard;
    @ApiModelProperty(hidden = true)
    private String userId;
    @ApiModelProperty(value = "与业主关系 1.夫妻 2.父子 3.母子 4.父女 5.母女 6.亲属")
    private Integer relation;
    @ApiModelProperty(value = "与业主关系 1.夫妻 2.父子 3.母子 4.父女 5.母女 6.亲属")
    private String relationText;
    @ApiModelProperty(value = "所属社区",required = true)
    private Long communityId;
    @ApiModelProperty(value = "所属单元",required = true)
    private Long houseId;
    @ApiModelProperty(value = "0，其他，1亲属，2租客")
    private Integer personType;
    @ApiModelProperty("车辆信息集合")
    private List<RelationCarsVO> cars = new ArrayList<>();
}
