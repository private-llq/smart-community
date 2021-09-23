package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("ceh")
@TableName("t_car_qr")
public class CarQREntity extends BaseEntity {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @ApiModelProperty(value = "社区二维码地址")
    private  String path;
}
