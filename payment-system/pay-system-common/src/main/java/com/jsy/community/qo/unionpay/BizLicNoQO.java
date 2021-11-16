package com.jsy.community.qo.unionpay;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 统一信息用代码接参
 * @Date: 2021/5/10 9:05
 * @Version: 1.0
 **/
@Data
public class BizLicNoQO implements Serializable {

    // 统一信息用代码
    @NotBlank(message = "统一信息用代码不能为空")
    private String bizLicNo;
}
