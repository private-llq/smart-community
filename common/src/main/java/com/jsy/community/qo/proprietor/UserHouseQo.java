package com.jsy.community.qo.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 业主房屋信息请求对象
 * @author YuLF
 * @since  2021/2/24 14:41
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserHouse请求对象", description="房屋认证信息")
public class UserHouseQo implements Serializable {

    @ApiModelProperty(value = "数据ID")
    private Long id;

    @ApiModelProperty(value = "社区id")
    @Range(groups = {UpdateHouse.class}, min = 1, message = "社区id不在有效范围")
    @NotNull(groups = {UpdateHouse.class}, message = "社区id不能为空!")
    private Long communityId;
    
    @ApiModelProperty(value = "房间ID")
    @Range(groups = {UpdateHouse.class}, min = 1, message = "房屋id不在有效范围")
    @NotNull(groups = {UpdateHouse.class}, message = "房屋id不能为空!")
    private Long houseId;
    

    /**
     * 房屋信息 更新/新增  验证
     */
    public interface UpdateHouse {}
}
