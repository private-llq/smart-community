package com.jsy.community.vo.car;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GpioData implements Serializable {
  @ApiModelProperty("输出 IO 口，\"io1\"固定开闸用，\n" +
          "\"io2\"为其它用途")
  private String  ionum;
  @ApiModelProperty("输出动作，\"on\"为开闸")
  private String  action;
}
