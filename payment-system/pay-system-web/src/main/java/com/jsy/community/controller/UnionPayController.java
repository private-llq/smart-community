package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.gnete.api.GneteSignatureAlgorithm;
import com.gnete.api.internal.crypto.DefaultRsaSigner;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.UnionPayBApplyRecordService;
import com.jsy.community.api.UnionPayOrderRecordService;
import com.jsy.community.api.UnionPayWalletService;
import com.jsy.community.config.UnionPayConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.payment.UnionPayOrderRecordEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.unionpay.*;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.unionpay.*;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Period;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 银联支付控制器
 * @Date: 2021/4/8 10:29
 * @Version: 1.0
 **/
@Api(tags = "银联支付控制器")
@RestController
// @ApiJSYController
@RequestMapping("/unionPay")
@Slf4j
public class UnionPayController {

    @DubboReference(version = Const.version, group = Const.group_payment, check = false, timeout = 1200000)
    private UnionPayWalletService unionPayWalletService;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false, timeout = 1200000)
    private UnionPayOrderRecordService orderRecordService;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false, timeout = 1200000)
    private UnionPayBApplyRecordService bApplyRecordService;

    /**
     *@Author: Pipi
     *@Description: C端用户开户
     *@Param: openAccountForCQO: 银联C端开户请求参数
     *@Return: com.jsy.community.vo.CommonResult<org.apache.poi.ss.formula.functions.T>
     *@Date: 2021/4/8 10:38
     **/
    @PostMapping("/openAccountForC")
    @ApiOperation("C端用户开户")
    // @Permit("community:payment:unionPay:openAccountForC")
    public CommonResult openAccountForC(@RequestBody OpenAccountForCQO openAccountForCQO) {
        if (openAccountForCQO.getAuthType() == 1 && org.springframework.util.StringUtils.isEmpty(openAccountForCQO.getBankAcctNo())) {
            throw new JSYException(400, "当认证类型为1：银行卡3要素时,请填写银行卡号");
        }
        if (openAccountForCQO.getAuthType() != 1 && org.springframework.util.StringUtils.isEmpty(openAccountForCQO.getSmsAuthCode())) {
            throw new JSYException(400, "当认证类型为0：姓名身份证2要素或2：运营商3要素时,请填写手机短信验证码");
        }
        openAccountForCQO.setIsActive(1);
        ValidatorUtils.validateEntity(openAccountForCQO);
        Boolean result = unionPayWalletService.openAccountForC(openAccountForCQO, UserUtils.getUserId());
        return result ? CommonResult.ok("钱包开户成功!") : CommonResult.error("钱包开户失败!");
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
    // @Permit("community:payment:unionPay:getPlugRandomKey")
    public CommonResult getPlugRandomKey() {
        return CommonResult.ok(unionPayWalletService.getPlugRandomKey());
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
    // @Permit("community:payment:unionPay:bindBankCard")
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
        Boolean result = unionPayWalletService.bindBankCard(bindBankCardQO);
        return result ? CommonResult.ok("银行卡操作成功!") : CommonResult.error("银行卡操作失败!");
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
    // @Permit("community:payment:unionPay:sendSmsAuthCode")
    public CommonResult sendSmsAuthCode(@RequestBody SendSmsAuthCodeQO sendSmsAuthCodeQO) {
        ValidatorUtils.validateEntity(sendSmsAuthCodeQO);
        Boolean result = unionPayWalletService.sendSmsAuthCode(sendSmsAuthCodeQO);
        return result ? CommonResult.ok("短信验证码发送成功!") : CommonResult.error("短信验证码发送失败!");
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
    // @Permit("community:payment:unionPay:modifyUserMobile")
    public CommonResult modifyUserMobile(@RequestBody ModifyUserMobileQO modifyUserMobileQO) {
        ValidatorUtils.validateEntity(modifyUserMobileQO);
        Boolean result = unionPayWalletService.modifyUserMobile(modifyUserMobileQO);
        return result ? CommonResult.ok("修改用户手机号成功!") : CommonResult.error("修改用户手机号失败!");
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
    // @Permit("community:payment:unionPay:queryAcctInfo")
    public CommonResult queryAcctInfo(@RequestBody WalletIdQO walletIdQO) {
        ValidatorUtils.validateEntity(walletIdQO);
        AcctInfoVO acctInfoVO = unionPayWalletService.queryAcctInfo(walletIdQO);
        return CommonResult.ok(acctInfoVO);
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
    // @Permit("community:payment:unionPay:queryBindBankCardList")
    public CommonResult queryBindBankCardList(@RequestBody WalletIdQO walletIdQO) {
        ValidatorUtils.validateEntity(walletIdQO);
        List<BindBankCardVO> bindBankCardVOS = unionPayWalletService.queryBindBankCardList(walletIdQO);
        return CommonResult.ok(bindBankCardVOS);
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
    // @Permit("community:payment:unionPay:modifyPwd")
    public CommonResult modifyPwd(@RequestBody ModifyPwdQO modifyPwdQO) {
        ValidatorUtils.validateEntity(modifyPwdQO);
        Boolean result = unionPayWalletService.modifyPwd(modifyPwdQO);
        return result ? CommonResult.ok("修改银联支付密码成功!") : CommonResult.error("修改银联支付密码失败!");
    }

    /**
     *@Author: Pipi
     *@Description: 银联消费类下单
     *@Param: unionPayOrderRecordEntity:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/26 16:33
     **/
    @PostMapping("/generateOrder")
    @ApiOperation("银联消费类下单")
    // @Permit("community:payment:unionPay:generateOrder")
    public CommonResult generateOrder(@RequestBody UnionPayOrderRecordEntity unionPayOrderRecordEntity) {
        ValidatorUtils.validateEntity(unionPayOrderRecordEntity, UnionPayOrderRecordEntity.GenerateOrderValidate.class);
        unionPayOrderRecordEntity.setUid(UserUtils.getUserId());
        UnionPayOrderVO unionPayOrderVO = orderRecordService.generateOrder(unionPayOrderRecordEntity);
        return CommonResult.ok(unionPayOrderVO, "下单成功!");
    }

    /**
     *@Author: Pipi
     *@Description: 银联消费支付回调接口
     *@Param: request:
     *@Return: void
     *@Date: 2021/4/26 18:04
     **/
    @LoginIgnore
    @PostMapping(value = "/unionPayNotifyUrl")
    @ApiOperation("银联消费支付回调接口")
    // @Permit("community:payment:unionPay:unionPayNotifyUrl")
    public void unionPayNotifyUrl(HttpServletRequest request) {
        // 获取回调参数
        StringBuilder data = new StringBuilder();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            while (null != (line = reader.readLine())){
                data.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 转换为回调接参对象
        ConsumeApplyOrderNotifyQO notifyQO = JSON.parseObject(String.valueOf(data), ConsumeApplyOrderNotifyQO.class);
        // 验签
        DefaultRsaSigner defaultRsaSigner = new DefaultRsaSigner(UnionPayConfig.PRIVATE_KEY, UnionPayConfig.VERIFY_PUBLIC_KEY, GneteSignatureAlgorithm.getAlgorithm("1"));
        ConsumeApplyOrderNotifyQO tempNotufyQO = new ConsumeApplyOrderNotifyQO();
        BeanUtils.copyProperties(notifyQO, tempNotufyQO);
        // 等待验签对象不能包含密文和加密类型
        tempNotufyQO.setSign(null);
        tempNotufyQO.setSignAlg(null);
        // 转json字符串时会按字典序排序
        String notifyString = JSON.toJSONString(tempNotufyQO);
        // 得到验签结果,为true时表示验签通过
        boolean verify = defaultRsaSigner.verify(notifyString, notifyQO.getSign(), "UTF-8");
        if (verify) {
            // 验签通过之后,修改订单状态和信息
            log.info("{}验签通过,前往更新逻辑", notifyQO.getOutTradeNo());
            orderRecordService.updateOrderStatus(notifyQO);
        } else {
            log.info("异常的支付回调,回调信息为:{}", notifyQO.toString());
        }
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
    // @Permit("community:payment:unionPay:queryBalance")
    public CommonResult queryBalance(@RequestBody BalanceQO balanceQO) {
        ValidatorUtils.validateEntity(balanceQO);
        if (balanceQO.getIsNeedPwd() == 1) {
            if (StringUtils.isEmpty(balanceQO.getEncryptPwd())) {
                throw new JSYException(400, "支付密码密文不能为空");
            }
            if (balanceQO.getEncryptType() == null || (balanceQO.getEncryptType() != 1 && balanceQO.getEncryptType() != 2)) {
                throw new JSYException(400, "加密类型不能为空,只能为1或2");
            }
        }
        BalanceVO balanceVO = unionPayWalletService.queryBalance(balanceQO);
        return CommonResult.ok(balanceVO, "查询成功!");
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
    // @Permit("community:payment:unionPay:getCredential")
    public CommonResult getCredential(@RequestBody CredentialQO credentialsQO) {
        credentialsQO.setNotifyUrl(UnionPayConfig.CREDENTIAL_NOTIFY_URL);
        ValidatorUtils.validateEntity(credentialsQO);
        CredentialVO credential = bApplyRecordService.getCredential(credentialsQO, UserUtils.getUserId());
        return CommonResult.ok(credential);
    }

    /**
     *@Author: Pipi
     *@Description: 凭据异步回调接口
     *@Param: request:
     *@Return: void
     *@Date: 2021/5/8 11:35
     **/
    @LoginIgnore
    @PostMapping("/credentialNotifyUrl")
    @ApiOperation("凭据异步回调接口")
    // @Permit("community:payment:unionPay:credentialNotifyUrl")
    public void credentialNotifyUrl(HttpServletRequest request) {
        // 获取回调参数
        StringBuilder data = new StringBuilder();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            while (null != (line = reader.readLine())){
                data.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("银联B端开户回调信息:{}", data);
        UnionPayBaseQO baseQO = JSON.parseObject(String.valueOf(data), UnionPayBaseQO.class);
        if (baseQO != null && baseQO.getMsgBody() != null) {
            CredentialNotifyQO notifyQO = JSON.parseObject(baseQO.getMsgBody(), CredentialNotifyQO.class);
            notifyQO.setMsgType(baseQO.getMsgType());
            bApplyRecordService.updateBApplyRecord(notifyQO);
        }
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
    // @Permit("community:payment:unionPay:queryWalletByBizLicNo")
    public CommonResult queryWalletByBizLicNo(@RequestBody BizLicNoQO bizLicNoQO) {
        ValidatorUtils.validateEntity(bizLicNoQO);
        BEndAccountOpeningVO openingVO = unionPayWalletService.queryWalletByBizLicNo(bizLicNoQO);
        return CommonResult.ok(openingVO);
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
    // @Permit("community:payment:unionPay:withdrawApply")
    public CommonResult withdrawApply(@RequestBody WithdrawQO withdrawQO) {
        withdrawQO.setWithdrawType("T0");
        ValidatorUtils.validateEntity(withdrawQO);
        WithdrawVO withdrawVO = unionPayWalletService.withdrawApply(withdrawQO, UserUtils.getUserId());
        return CommonResult.ok(withdrawVO);
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
    // @Permit("community:payment:unionPay:resetBtypeAcctPwd")
    public CommonResult resetBtypeAcctPwd(@RequestBody ResetBtypeAcctPwdQO resetBtypeAcctPwdQO) {
        ValidatorUtils.validateEntity(resetBtypeAcctPwdQO);
        Boolean result = bApplyRecordService.resetBtypeAcctPwd(resetBtypeAcctPwdQO);
        return result ? CommonResult.ok("重置密码成功!") : CommonResult.error("重置密码失败!");
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
    // @Permit("community:payment:unionPay:queryTransList")
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
        UnionPayTransListVO unionPayTransListVO = orderRecordService.queryTransList(queryTransListQO);
        return unionPayTransListVO != null ? CommonResult.ok(unionPayTransListVO, "查询成功") : CommonResult.error("查询失败");
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
    // @Permit("community:payment:unionPay:queryBillInfo")
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
        QueryBillInfoListVO queryBillInfoListVO = orderRecordService.queryBillInfo(queryBillInfoQO);
        return queryBillInfoListVO != null ? CommonResult.ok(queryBillInfoListVO, "查询成功") : CommonResult.error("查询失败");
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
    // @Permit("community:payment:unionPay:activeAcct")
    public CommonResult activeAcct(@RequestBody ActiveAcctQO activeAcctQO) {
        ValidatorUtils.validateEntity(activeAcctQO);
        ActiveAcctVO activeAcctVO = unionPayWalletService.activeAcct(activeAcctQO);
        return CommonResult.ok(activeAcctVO);
    }
}
