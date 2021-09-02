package com.jsy.community.vo.car;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TrigerData implements Serializable {
  @ApiModelProperty("软触发动作，\"on\"为触发")
  private String  action;
}
