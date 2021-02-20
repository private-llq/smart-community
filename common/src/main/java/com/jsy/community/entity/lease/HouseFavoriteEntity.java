package com.jsy.community.entity.lease;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 *  房屋租售收藏实体对象
 *  数据访问对象：这个类主要用于对应数据库表t_house_favorite的数据字段的映射关系，
 * @author YuLF
 * @since  2021/2/20 15:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋收藏对象", description="房屋租售收藏数据字段实体")
@TableName("t_house_favorite")
public class HouseFavoriteEntity extends BaseEntity {

    @ApiModelProperty(value = "所属人ID")
    private String uid;

    @ApiModelProperty(value = "收藏房屋id")
    private Long favoriteId;

    @ApiModelProperty(value = "收藏类型：1.商铺 2.租房")
    private Short favoriteType;

    public static HouseFavoriteEntity getInstance(){
        return new HouseFavoriteEntity();
    }

}
