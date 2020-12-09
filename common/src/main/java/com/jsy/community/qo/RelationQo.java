package com.jsy.community.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("添加家属")
public class RelationQo implements Serializable {
    @ApiModelProperty(value = "家属姓名",required = true)
    private String name;
    @ApiModelProperty("性别")
    private Integer sex;
    @ApiModelProperty("电话")
    private String phoneTel;

    @ApiModelProperty(value = "身份证号码",required = true)
    private String idNumber;


    @ApiModelProperty(value = "与业主关系 1.夫妻 2.父子 3.母子 4.父女 5.母女 6.亲属")
    private Integer concern;
    @ApiModelProperty(value = "所属社区",required = true)
    private Long communityId;
    @ApiModelProperty(value = "所属单元",required = true)
    private Long houseId;
    @ApiModelProperty("车辆信息集合")
    private List<RelationCarsQo> cars = new ArrayList<>();
}
