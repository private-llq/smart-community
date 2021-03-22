package com.jsy.community.vo.lease;

import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author YuLF
 * @since 2021-03-19 09:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋图片返回对象", description="返回后端查询参数")
public class HouseImageVo extends BaseVO {

    @ApiModelProperty("图片url")
    private String imgUrl;

    @ApiModelProperty("图片id")
    private Long fieldId;

    @ApiModelProperty("租赁房屋id")
    private Long hid;

}
