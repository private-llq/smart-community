package com.jsy.community.qo.proprietor;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("添加家属")
public class RelationQO implements Serializable {
    @ApiModelProperty(value = "家属ID",required = true)
    private Long id;
    @ApiModelProperty(value = "家属姓名",required = true)
    @NotNull(groups = {RelationValidated.class},message = "姓名不能为空")
    @NotBlank(groups = {RelationValidated.class},message = "姓名不能为空")
    private String name;
    @ApiModelProperty("性别")
    @NotNull(groups = {RelationValidated.class},message = "姓别不能为空")
    private Integer sex;
    @ApiModelProperty("电话")
    @Pattern(groups = {RelationValidated.class},regexp = RegexUtils.REGEX_MOBILE,message = "请输入正确手机号！")
    private String mobile;
    @ApiModelProperty(value = "证件类型1.身份证 2.护照",required = true)
    @NotNull(groups = {RelationValidated.class},message = "证件类型不能为空")
    @Range(min = 1,max = 2,message = "证件类型错误")
    private Integer identificationType;
    @ApiModelProperty(value = "身份证号码/护照号码",required = true)
    @NotNull(groups = {RelationValidated.class},message = "身份证或者护照不能为空！")
    @NotBlank(groups = {RelationValidated.class},message = "身份证或者护照不能为空")
    private String idCard;
    @ApiModelProperty(hidden = true)
    private String userId;
    @ApiModelProperty(value = "与业主关系 1.夫妻 2.父子 3.母子 4.父女 5.母女 6.亲属")
    @NotNull(groups = {RelationValidated.class},message = "身份证不能为空！")
    private Integer relation;
    @ApiModelProperty(value = "所属社区",required = true)
    private Long communityId;
    @ApiModelProperty(value = "所属单元",required = true)
    private Long houseId;
    @ApiModelProperty(value = "0，其他，1亲属，2租客")
    private Integer personType;
    @ApiModelProperty("车辆信息集合")
    private List<RelationCarsQO> cars = new ArrayList<>();

    public interface RelationValidated{}
}
