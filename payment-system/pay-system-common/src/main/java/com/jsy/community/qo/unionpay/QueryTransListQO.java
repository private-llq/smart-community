package com.jsy.community.qo.unionpay;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 查询交易明细接参
 * @Date: 2021/5/12 9:09
 * @Version: 1.0
 **/
@Data
public class QueryTransListQO implements Serializable {

    // 页面大小-必填, 不传默认为10（最大限制50）
    @Range(min = 1, max = 50, message = "页面大小最小为1,最大为50")
    @NotBlank(message = "页面大小不能为空")
    private String pageSize ;

    // 页数-必填,页数（从1开始）。不传默认为1。
    @Range(min = 1, message = "页数最小为1")
    @NotBlank(message = "页数不能为空")
    private String pageNo ;

    // 钱包ID-必填
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    // 开始日期,格式yyyyMMdd
    @Pattern(regexp = "^([\\d]{4}((((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][0-9])|30))|(02((0[1-9])|(1[0-9])|(2[0-8])))))|((((([02468][048])|([13579][26]))00)|([0-9]{2}(([02468][048])|([13579][26]))))(((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][0-9])|30))|(02((0[1-9])|(1[0-9])|(2[0-9]))))){4})$", message = "日期格式不正确,正确格式为yyyyMMdd")
    private String startDate;

    // 结束日期,格式yyyyMMdd
    @Pattern(regexp = "^([\\d]{4}((((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][0-9])|30))|(02((0[1-9])|(1[0-9])|(2[0-8])))))|((((([02468][048])|([13579][26]))00)|([0-9]{2}(([02468][048])|([13579][26]))))(((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][0-9])|30))|(02((0[1-9])|(1[0-9])|(2[0-9]))))){4})$", message = "日期格式不正确,正确格式为yyyyMMdd")
    private String endDate;

    // 起始清算日期,格式yyyyMMdd
    @Pattern(regexp = "^([\\d]{4}((((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][0-9])|30))|(02((0[1-9])|(1[0-9])|(2[0-8])))))|((((([02468][048])|([13579][26]))00)|([0-9]{2}(([02468][048])|([13579][26]))))(((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][0-9])|30))|(02((0[1-9])|(1[0-9])|(2[0-9]))))){4})$", message = "日期格式不正确,正确格式为yyyyMMdd")
    private String startSettDate;

    // 起始清算日期,格式yyyyMMdd
    @Pattern(regexp = "^([\\d]{4}((((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][0-9])|30))|(02((0[1-9])|(1[0-9])|(2[0-8])))))|((((([02468][048])|([13579][26]))00)|([0-9]{2}(([02468][048])|([13579][26]))))(((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][0-9])|30))|(02((0[1-9])|(1[0-9])|(2[0-9]))))){4})$", message = "日期格式不正确,正确格式为yyyyMMdd")
    private String endSettDate;

    // 交易类型集合
    private String transTypes;

    // 交易类型
    private String transType;

    // 处理状态,0：已接收，1：成功，2：失败，3：已冲正，4：已撤销。
    @Range(min = 0,max = 4, message = "处理状态区间为0-4")
    private String procStatus;

    // 商户订单号
    private String mctOrderNo;

    // 交易订单号
    private String transOrderNo;

    // 资金计划项目ID
    private String cptlPlnPrjctId;

    // 是否输入密码-必填,0：不需要密码。1：需要密码。
    @Range(min = 0, max = 1, message = "是否输入密码的区间为0或1")
    @NotNull(message = "是否输入密码不能为空")
    private Integer isNeedPwd;

    // 密码密文
    private String encryptPwd;

    // 加密类型
    @Range(min = 1, max = 2, message = "加密类型的取值范围为1或2,1：H5密码键盘加密,2：非H5加密。")
    private String encryptType;

    // 控件随机因子
    private String plugRandomKey;

    // 证书签名密文
    private String certSign;

    // 是否关联入账查询,默认不关联,0：不关联,1：关联,2：仅包含入账查询
    @Range(min = 0, max = 2, message = "是否关联入账查询区间为0-2")
    private String isRelateOtherWalletId;
}
