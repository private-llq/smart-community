package com.jsy.community.vo.car;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Rs485Data implements Serializable {
  @ApiModelProperty("待透传数据的编码类型。\n" +
          "hex2string：表示是十六进制数据\n" +
          "直接转成字符串形式\n" +
          "base64:表示是十六进制数据直\n" +
          "接进行 BASE64 编码得到的字符\n" +
          "串")
  private String  encodetype;
  @ApiModelProperty("待透传的数据")
  private String  data;
}
