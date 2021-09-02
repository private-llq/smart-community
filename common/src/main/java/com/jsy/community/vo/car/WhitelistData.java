package com.jsy.community.vo.car;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WhitelistData implements Serializable {
  @ApiModelProperty("名单动作。\n" +
          "add:表示增加 1 个名单；\n" +
          "update:表示更改 1 个名单；\n" +
          "delete:表示删除 1 个名单；\n" +
          "deleteAll:表示清空所有名单")
  private String  Action;
  @ApiModelProperty("车牌号码，UTF8 编码")
  private String  PlateNumber;
  @ApiModelProperty("名单类型。\n" +
          "W：表示白名单；\n" +
          "B：表示黑名单。")
  private String  Type;
  @ApiModelProperty("起始时间。\n" +
          "格式：\"年/月/日 时:分:秒\"")
  private String  Start;
  @ApiModelProperty("结束时间。\n" +
          "格式：\"年/月/日 时:分:秒")
  private String  End;


}
