package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 来访车辆
 * </p>
 *
 * @author jsy
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="VisitingCar对象", description="来访车辆")
@TableName("t_visiting_car")
public class VisitingCar extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "社区ID")
    private Long communityId;

    @ApiModelProperty(value = "业主ID")
    private Long uid;

    @ApiModelProperty(value = "访客登记ID")
    private Long visitorId;

    @ApiModelProperty(value = "来访车辆车牌")
    @Pattern(groups = {addCarValidatedGroup.class, updateCarValidatedGroup.class}, regexp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$", message = "请输入一个正确的车牌号!")
    private String carPlate;

    @ApiModelProperty(value = "来访车辆图片地址")
    private String carImageUrl;

    @ApiModelProperty(value = "来访车辆类型")
    private Integer carType;

    @ApiModelProperty(value = "是否授予来访人楼栋门禁权限，0无，1临时密码，2可视对讲")
    private Integer isBuildingAccess;

    @ApiModelProperty(value = "实际离开时间(改为权限时效时间？再加次数方式？)")
    private Date leavingTime;
    
    /**
     * 添加随行车辆前端参数验证接口
     */
    public interface addCarValidatedGroup{}
    
    /**
     * 修改随行车辆前端参数验证接口
     */
    public interface updateCarValidatedGroup{}
}
