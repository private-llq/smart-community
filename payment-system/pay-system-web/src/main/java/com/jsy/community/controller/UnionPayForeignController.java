package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.UnionPayService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.*;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.OpenApiResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.time.Period;

/**
 * @Author: Pipi
 * @Description: 银联向其他项目提供接口的控制器
 * @Date: 2021/5/13 16:21
 * @Version: 1.0
 **/
@Api("银联向其他项目提供接口的控制器")
@Slf4j
@RestController
@RequestMapping("/unionPay/api")
@ApiJSYController
public class UnionPayForeignController {

    @DubboReference(version = Const.version, group = Const.group_payment, check = false, timeout = 1200000)
    private UnionPayService unionPayService;

    /**
     *@Author: Pipi
     *@Description: C端用户开户
     *@Param: openAccountForCQO: 银联C端开户请求参数
     *@Return: com.jsy.community.vo.CommonResult<org.apache.poi.ss.formula.functions.T>
     *@Date: 2021/4/8 10:38
     **/
    @PostMapping("/openAccountForC")
    @ApiOperation("C端用户开户")
    public CommonResult openAccountForC(@RequestBody OpenAccountForCQO openAccountForCQO) {
        openAccountForCQO.setIsActive(1);
        ValidatorUtils.validateEntity(openAccountForCQO);
        if (openAccountForCQO.getAuthType() == 1 && StringUtils.isEmpty(openAccountForCQO.getBankAcctNo())) {
            throw new JSYException(400, "当认证类型为1：银行卡3要素时,请填写银行卡号");
        }
        if (openAccountForCQO.getAuthType() != 1 && StringUtils.isEmpty(openAccountForCQO.getSmsAuthCode())) {
            throw new JSYException(400, "当认证类型为0：姓名身份证2要素或2：运营商3要素时,请填写手机短信验证码");
        }
        OpenApiResponseVO responseVO = unionPayService.openAccountForC(openAccountForCQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 获取控件随机因子
     *@Param: :
     *@Return: com.jsy.community.vo.CommonResult<org.apache.poi.ss.formula.functions.T>
     *@Date: 2021/4/10 13:56
     **/
    @GetMapping("/getPlugRandomKey")
    @ApiOperation("获取控件随机因子")
    public CommonResult getPlugRandomKey(@RequestParam Integer num) {
        if (num == null || num <= 0) {
            num = 1;
        }
        OpenApiResponseVO responseVO = unionPayService.getPlugRandomKey(num);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 钱包银行卡操作,账户绑定/解绑/设置默认银行卡
     *@Param: bindBankCardQO: 账户绑定/解绑/设置默认银行卡接参
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/12 15:09
     **/
    @PostMapping("/bindBankCard")
    @ApiOperation("钱包银行卡操作,账户绑定/解绑/设置默认银行卡")
    public CommonResult bindBankCard(@RequestBody BindBankCardQO bindBankCardQO) {
        ValidatorUtils.validateEntity(bindBankCardQO);
        Integer oprtType = bindBankCardQO.getOprtType();
        if (oprtType == 1 || oprtType == 2) {
            if (StringUtils.isEmpty(bindBankCardQO.getMobileNo())) {
                throw new JSYException(400, "绑定银行卡时,手机号不能为空");
            }
            if (StringUtils.isEmpty(bindBankCardQO.getBankAcctName())) {
                throw new JSYException(400, "绑定银行卡时,身份证不能为空");
            }
            if (StringUtils.isEmpty(bindBankCardQO.getBankAcctName())) {
                throw new JSYException(400, "绑定银行卡时,银行账户户名不能为空,必须与用户姓名保持一致");
            }
            if (StringUtils.isEmpty(bindBankCardQO.getBankNo())) {
                throw new JSYException(400, "绑定银行卡时,开户行号不能为空");
            }
            if (StringUtils.isEmpty(bindBankCardQO.getElecBankNo())) {
                throw new JSYException(400, "绑定银行卡时,电子联行号不能为空");
            }
            if (bindBankCardQO.getBankAcctType() == null) {
                throw new JSYException(400, "绑定银行卡时,银行账户类型不能为空");
            }
        }
        OpenApiResponseVO responseVO = unionPayService.bindBankCard(bindBankCardQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 银联发送短信验证码
     *@Param: sendSmsAuthCodeQO: 发送短信验证码接参
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/12 17:34
     **/
    @PostMapping("/sendSmsAuthCode")
    @ApiOperation("银联发送短信验证码")
    public CommonResult sendSmsAuthCode(@RequestBody SendSmsAuthCodeQO sendSmsAuthCodeQO) {
        ValidatorUtils.validateEntity(sendSmsAuthCodeQO);
        OpenApiResponseVO responseVO = unionPayService.sendSmsAuthCode(sendSmsAuthCodeQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 修改用户手机号
     *@Param: modifyUserMobileQO: 修改用户手机号接参
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/14 9:35
     **/
    @PostMapping("/modifyUserMobile")
    @ApiOperation("修改用户手机号")
    public CommonResult modifyUserMobile(@RequestBody ModifyUserMobileQO modifyUserMobileQO) {
        ValidatorUtils.validateEntity(modifyUserMobileQO);
        OpenApiResponseVO responseVO = unionPayService.modifyUserMobile(modifyUserMobileQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 获取钱包账户信息
     *@Param: walletIdQO: 钱包ID接参
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/14 10:58
     **/
    @PostMapping("/queryAcctInfo")
    @ApiOperation("获取钱包账户信息")
    public CommonResult queryAcctInfo(@RequestBody WalletIdQO walletIdQO) {
        ValidatorUtils.validateEntity(walletIdQO);
        OpenApiResponseVO responseVO = unionPayService.queryAcctInfo(walletIdQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 获取钱包账户绑定的银行卡列表
     *@Param: walletIdQO: 钱包ID接参
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/14 17:34
     **/
    @PostMapping("/queryBindBankCardList")
    @ApiOperation("获取钱包账户绑定的银行卡列表")
    public CommonResult queryBindBankCardList(@RequestBody WalletIdQO walletIdQO) {
        ValidatorUtils.validateEntity(walletIdQO);
        OpenApiResponseVO responseVO = unionPayService.queryBindBankCardList(walletIdQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 修改银联支付密码
     *@Param: modifyPwdQO: 修改银联支付密码接参
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/15 11:30
     **/
    @PostMapping("/modifyPwd")
    @ApiOperation("修改银联支付密码")
    public CommonResult modifyPwd(@RequestBody ModifyPwdQO modifyPwdQO) {
        ValidatorUtils.validateEntity(modifyPwdQO);
        OpenApiResponseVO responseVO = unionPayService.modifyPwd(modifyPwdQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 银联消费类下单
     *@Param: unionPayOrderRecordEntity
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/26 16:33
     **/
    @PostMapping("/generateOrder")
    @ApiOperation("银联消费类下单")
    public CommonResult generateOrder(@RequestBody GenerateOrderQO generateOrderQO) {
        ValidatorUtils.validateEntity(generateOrderQO);
        OpenApiResponseVO responseVO = unionPayService.generateConsumeOrder(generateOrderQO);
        return CommonResult.ok(responseVO);
    }

    /**application/
     *@Author: Pipi
     *@Description: 查询钱包余额
     *@Param: balanceQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/28 17:45
     **/
    @PostMapping("/queryBalance")
    @ApiOperation("查询钱包余额")
    public CommonResult queryBalance(@RequestBody BalanceQO balanceQO) {
        ValidatorUtils.validateEntity(balanceQO);
        if (balanceQO.getIsNeedPwd() == 1) {
            if (StringUtils.isBlank(balanceQO.getEncryptPwd())) {
                throw new JSYException(400, "支付密码密文不能为空");
            }
            if (balanceQO.getEncryptType() == null || (balanceQO.getEncryptType() != 1 && balanceQO.getEncryptType() != 2)) {
                throw new JSYException(400, "加密类型不能为空,只能为1或2");
            }
            if (StringUtils.isBlank(balanceQO.getPlugRandomKey())) {
                throw new JSYException(400, "控件随机因子不能为空");
            }
        }
        OpenApiResponseVO responseVO = unionPayService.queryBalance(balanceQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 银联支付获取凭据接口
     *@Param: :
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/5/7 17:38
     **/
    @PostMapping("/getCredential")
    @ApiOperation("银联支付获取凭据接口")
    public CommonResult getCredential(@RequestBody CredentialQO credentialsQO) {
        ValidatorUtils.validateEntity(credentialsQO);
        OpenApiResponseVO responseVO = unionPayService.getCredential(credentialsQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 查询开B端开户情况
     *@Param: bizLicNoQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/5/10 9:08
     **/
    @PostMapping("/queryWalletByBizLicNo")
    @ApiOperation("查询开B端开户情况")
    public CommonResult queryWalletByBizLicNo(@RequestBody BizLicNoQO bizLicNoQO) {
        ValidatorUtils.validateEntity(bizLicNoQO);
        OpenApiResponseVO responseVO = unionPayService.queryWalletByBizLicNo(bizLicNoQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 提现申请接口
     *@Param: withdrawQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/5/10 10:19
     **/
    @PostMapping("/withdrawApply")
    @ApiOperation("提现申请接口")
    public CommonResult withdrawApply(@RequestBody WithdrawQO withdrawQO) {
        ValidatorUtils.validateEntity(withdrawQO);
        OpenApiResponseVO responseVO = unionPayService.withdrawApply(withdrawQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: B端钱包重置支付密码
     *@Param: resetBtypeAcctPwdQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/5/11 17:47
     **/
    @PostMapping("/resetBtypeAcctPwd")
    @ApiOperation("B端钱包重置支付密码")
    public CommonResult resetBtypeAcctPwd(@RequestBody ResetBtypeAcctPwdQO resetBtypeAcctPwdQO) {
        ValidatorUtils.validateEntity(resetBtypeAcctPwdQO);
        OpenApiResponseVO responseVO = unionPayService.resetBtypeAcctPwd(resetBtypeAcctPwdQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 查询交易明细
     *@Param: queryTransListQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/5/12 9:59
     **/
    @PostMapping("/queryTransList")
    @ApiOperation("查询交易明细")
    public CommonResult queryTransList(@RequestBody QueryTransListQO queryTransListQO) {
        ValidatorUtils.validateEntity(queryTransListQO);
        if ((StringUtils.isNotBlank(queryTransListQO.getStartDate()) && StringUtils.isBlank(queryTransListQO.getEndDate())) || (StringUtils.isBlank(queryTransListQO.getStartDate()) && StringUtils.isNotBlank(queryTransListQO.getEndDate()))) {
            throw new JSYException("开始日期与结束日期只能同时为空或同时不为空");
        }
        if ((StringUtils.isNotBlank(queryTransListQO.getStartSettDate()) && StringUtils.isBlank(queryTransListQO.getEndSettDate())) || (StringUtils.isBlank(queryTransListQO.getStartSettDate()) && StringUtils.isNotBlank(queryTransListQO.getEndSettDate()))) {
            throw new JSYException("起始清算日期与起始清算日期只能同时为空或同时不为空");
        }
        if (StringUtils.isBlank(queryTransListQO.getStartDate()) && StringUtils.isBlank(queryTransListQO.getEndDate()) && StringUtils.isBlank(queryTransListQO.getStartSettDate()) && StringUtils.isBlank(queryTransListQO.getEndSettDate())) {
            throw new JSYException("开始结束交易日期段、开始结束清算日期段不能都为空");
        }
        if (queryTransListQO.getIsNeedPwd() == 1) {
            if (StringUtils.isBlank(queryTransListQO.getEncryptPwd())) {
                throw new JSYException("当需要密码时,密码密文不能为空");
            }
            if (StringUtils.isBlank(queryTransListQO.getEncryptType())) {
                throw new JSYException("当需要密码时,加密类型不能为空");
            }
            if (StringUtils.isBlank(queryTransListQO.getPlugRandomKey())) {
                throw new JSYException("当需要密码时,控件随机因子不能为空");
            }
        }
        OpenApiResponseVO responseVO = unionPayService.queryTransList(queryTransListQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 账单查询
     *@Param: queryBillInfoQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/5/12 11:22
     **/
    @PostMapping("/queryBillInfo")
    @ApiOperation("账单查询")
    public CommonResult queryBillInfo(@RequestBody QueryBillInfoQO queryBillInfoQO) {
        ValidatorUtils.validateEntity(queryBillInfoQO);
        Integer walletNullNum = 0;
        if (StringUtils.isNotBlank(queryBillInfoQO.getWalletId())) {
            walletNullNum++;
        }
        if (StringUtils.isNotBlank(queryBillInfoQO.getGrantWalletId())) {
            walletNullNum++;
        }
        if (StringUtils.isNotBlank(queryBillInfoQO.getOnGrantWalletId())) {
            walletNullNum++;
        }
        if (walletNullNum != 1) {
            throw new JSYException("授权钱包ID,被授权钱包ID,共管子账号查询时只能三选一");
        }
        if (!queryBillInfoQO.getLocalDateStartDate().isBefore(queryBillInfoQO.getLocalDateEndDate()) && !queryBillInfoQO.getLocalDateStartDate().isEqual(queryBillInfoQO.getLocalDateEndDate())) {
            throw new JSYException("开始日期不能大于结束日期");
        }
        if (Period.between(queryBillInfoQO.getLocalDateStartDate(), queryBillInfoQO.getLocalDateEndDate()).getMonths() > 3) {
            throw new JSYException("查询区间最长为3个月");
        }
        OpenApiResponseVO responseVO = unionPayService.queryBillInfo(queryBillInfoQO);
        return CommonResult.ok(responseVO);
    }

    /**
     *@Author: Pipi
     *@Description: 激活账户
     *@Param: activeAcctQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/5/12 17:13
     **/
    @PostMapping("/activeAcct")
    @ApiOperation("激活账户")
    public CommonResult activeAcct(@RequestBody ActiveAcctQO activeAcctQO) {
        ValidatorUtils.validateEntity(activeAcctQO);
        OpenApiResponseVO responseVO = unionPayService.activeAcct(activeAcctQO);
        return CommonResult.ok(responseVO);
    }
}
