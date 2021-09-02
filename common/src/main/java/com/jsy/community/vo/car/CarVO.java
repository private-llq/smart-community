package com.jsy.community.vo.car;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CarVO implements Serializable {
  @ApiModelProperty("错误码，0 表示无错误，其它表示\n" + "有错误")
  private Integer error_num;
  @ApiModelProperty("错误码说明")
  private String error_str;
  @ApiModelProperty("密码")
  private String passwd;
  @ApiModelProperty("开闸 GPIO 数据")
  private List<GpioData> gpio_data;
  @ApiModelProperty("RS485 透传数据")
  private List<Rs485Data> rs485_data;
  @ApiModelProperty("软件触发数据")
  private List<TrigerData> triger_data;
  @ApiModelProperty("名单数据")
  private List<WhitelistData> whitelist_data;
}
