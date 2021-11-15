package com.jsy.community.qo.cebbank;

import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 查询缴费账单信息QO
 * @Date: 2021/11/12 11:11
 * @Version: 1.0
 **/
@Data
public class CebQueryBillInfoQO extends CebBaseQO {
    // 用户标识-必填
    private String sessionId;

    // 缴费项目id-必填
    private String itemCode;

    // 缴款码-必填
    // 用户输入账单号：电费号，水费号，用户号，手机号等
    private String billKey;

    // 备用字段1-非必填
    // 用户输入或选择，由查询缴费项目接口返回，比如水费的账期
    private String filed1;

    // 北京智能电表查询次数标志(1,2)-非必填
    // 只有二次查缴项目必传第一次查flag=1
    // 第二次查flag=2
    private String flag;

    // 账单查询跟踪码-非必填
    // 第一次查询账单时返回
    private String qryAcqSsn;

    // 轮询次数-非必填
    // 第一次请求pollingTimes=1;
    // 间隔1秒第二次请求pollingTimes=2；
    // 间隔2秒第三次请求pollingTimes=3；
    // 间隔2秒第四次请求pollingTimes=4；
    // 间隔2秒第五次请求pollingTimes=5，此时不再查询账单返回参数有误停止请求；
    // 原因是有的缴费项目返回账单慢需要轮询。
    private String pollingTimes;
}
