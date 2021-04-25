package com.jsy.community.qo.property;

import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 结算单账单列表接参
 * @Date: 2021/4/24 11:38
 * @Version: 1.0
 **/
@Data
public class StatementNumQO implements Serializable {
    @NotEmpty(message = "结算单号不能为空")
    @ApiModelProperty("结算单号")
    private String statementNum;
}
