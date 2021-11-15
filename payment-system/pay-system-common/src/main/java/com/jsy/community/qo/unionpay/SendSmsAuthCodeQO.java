package com.jsy.community.qo.unionpay;

import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.RegexUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 发送短信验证码接参
 * @Date: 2021/4/12 16:55
 * @Version: 1.0
 **/
@Data
public class SendSmsAuthCodeQO implements Serializable {

    // 手机号
    @Pattern(regexp = RegexUtils.REGEX_MOBILE, message = "手机号格式不正确")
    @NotBlank(message = "手机号不能为空")
    private String mobileNo;

    // 短信模板编码
    @NotBlank(message = "短信模板编码不能为空")

    private String smsTmpltCode;

    // 短信业务类型
    private String smsBizType;

    public void setSmsTmpltCode(String smsTmpltCode) {
        if (PaymentEnum.SmsTmpltCode.getName(smsTmpltCode) == null) {
            throw new JSYException("短信模板编码超出范围,1：一般验证码；2：注册（开户）；3：激活；4:交易");
        }
        this.smsTmpltCode = PaymentEnum.SmsTmpltCode.getName(smsTmpltCode);
    }
}
