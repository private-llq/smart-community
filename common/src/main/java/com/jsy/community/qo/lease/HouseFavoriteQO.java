package com.jsy.community.qo.lease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 房屋租售收藏数据传输对象
 * 用于业务层之间的数据传递
 * YuLF
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋租售收藏接收参数对象", description="接收前端参数")
public class HouseFavoriteQO implements Serializable {

    @Range(groups = {addFavorite.class}, min = 1, message = "收藏Id范围错误!")
    @NotNull(groups = {addFavorite.class}, message = "收藏Id不能为空!")
    private Long favoriteId;

    @ApiModelProperty(value = "所属人ID")
    private String uid;

    @Range(groups = {addFavorite.class}, min = 1 ,max = 2, message = "社区id不正确")
    @NotNull(groups = {addFavorite.class}, message = "收藏类型不能为空!")
    @ApiModelProperty(value = "收藏类型：1商铺，2租房")
    private Short favoriteType;

    /**
     * 新增房屋租售收藏验证接口参数验证
     */
    public interface addFavorite{};


}
