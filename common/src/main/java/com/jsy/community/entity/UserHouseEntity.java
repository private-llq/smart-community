package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @return
 * @Author lihao
 * @Description 业主房屋认证
 * @Date 2020/11/28 10:06
 * @Param
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_house")
@ApiModel(value="UserHouse对象", description="业主房屋认证")
public class UserHouseEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    @ApiModelProperty(value = "社区id")
    @Range(groups = {UpdateHouse.class}, min = 1, message = "社区id不在有效范围")
    @NotNull(groups = {UpdateHouse.class}, message = "社区id不能为空!")
    private Long communityId;
    
    @ApiModelProperty(value = "房间(门禁)id", hidden = true)
    @TableField(exist = false)
    private Long buildingId;

    @ApiModelProperty(value = "房间ID(业主查自己房屋用)")
    @Range(groups = {UpdateHouse.class, UntieHouse.class}, min = 1, message = "房屋id不在有效范围")
    @NotNull(groups = {UpdateHouse.class, UntieHouse.class}, message = "房屋id不能为空!")
    private Long houseId;
    
    @ApiModelProperty(value = "是否通过审核 0.否 1.是 2.审核中")
    private Integer checkStatus;

    /**
     * 房屋信息 更新/新增  验证
     */
    public interface UpdateHouse {}

    /**
     * 业主解绑房屋
     */
    public interface UntieHouse {}
}
