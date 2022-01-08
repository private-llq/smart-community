package com.jsy.community.qo.payment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费通用QO
 * @Date: 2021/11/17 17:57
 * @Version: 1.0
 **/
@Data
public class CebBankCommonQO implements Serializable {
    // 用户手机号
    @NotBlank(groups = {QueryCityContributionCategoryValidateGroup.class}, message = "用户手机号不能为空")
    private String userPhone;

    // 查询城市下缴费类别验证组
    interface QueryCityContributionCategoryValidateGroup{}

}
