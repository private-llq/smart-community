package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.payment.UnionPayWalletEntity;
import com.jsy.community.qo.*;
import com.jsy.community.vo.*;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 银联支付服务
 * @Date: 2021/4/8 10:47
 * @Version: 1.0
 **/
public interface UnionPayWalletService extends IService<UnionPayWalletEntity> {
    /**
     *@Author: Pipi
     *@Description: C端用户开户
     *@Param: openAccountForCQO: 银联C端开户请求参数
     *@Return: java.lang.Boolean
     *@Date: 2021/4/8 10:49
     **/
    Boolean openAccountForC(OpenAccountForCQO openAccountForCQO, String uid);

    /**
     *@Author: Pipi
     *@Description: 获取控件随机因子
     *@Param: :
     *@Return: java.lang.String
     *@Date: 2021/4/10 13:58
     **/
    String getPlugRandomKey();

    /**
     *@Author: Pipi
     *@Description: 账户绑定/解绑/设置默认银行卡
     *@Param: bindBankCardQO: 账户绑定/解绑/设置默认银行卡接参
     *@Return: java.lang.Boolean
     *@Date: 2021/4/12 15:11
     **/
    Boolean bindBankCard(BindBankCardQO bindBankCardQO);

    /**
     *@Author: Pipi
     *@Description: 银联发送短信验证码
     *@Param: sendSmsAuthCodeQO: 发送短信验证码接参
     *@Return: java.lang.Boolean
     *@Date: 2021/4/12 17:59
     **/
    Boolean sendSmsAuthCode(SendSmsAuthCodeQO sendSmsAuthCodeQO);

    /**
     *@Author: Pipi
     *@Description: 修改用户手机号
     *@Param: modifyUserMobileQO: 修改用户手机号接参
     *@Return: java.lang.Boolean
     *@Date: 2021/4/14 9:36
     **/
    Boolean modifyUserMobile(ModifyUserMobileQO modifyUserMobileQO);

    /**
     *@Author: Pipi
     *@Description: 获取钱包账户信息
     *@Param: walletIdQO: 钱包ID接参
     *@Return: com.jsy.community.vo.AcctInfoVO
     *@Date: 2021/4/14 13:44
     **/
    AcctInfoVO queryAcctInfo(WalletIdQO walletIdQO);

    /**
     *@Author: Pipi
     *@Description: 获取钱包账户绑定的银行卡列表
     *@Param: walletIdQO: 钱包ID接参
     *@Return: java.util.List<com.jsy.community.vo.BindBankCardVO>
     *@Date: 2021/4/14 17:35
     **/
    List<BindBankCardVO> queryBindBankCardList(WalletIdQO walletIdQO);

    /**
     *@Author: Pipi
     *@Description: 修改银联支付密码
     *@Param: modifyPwdQO: 修改银联支付密码接参
     *@Return: java.lang.Boolean
     *@Date: 2021/4/15 11:31
     **/
    Boolean modifyPwd(ModifyPwdQO modifyPwdQO);

    /**
     *@Author: Pipi
     *@Description: 查询钱包余额
     *@Param: balanceQO:
     *@Return: com.jsy.community.vo.BalanceVO
     *@Date: 2021/4/28 17:47
     **/
    BalanceVO queryBalance(BalanceQO balanceQO);

    /**
     *@Author: Pipi
     *@Description: 查询开B端开户情况
     *@Param: bizLicNoQO:
     *@Return: com.jsy.community.vo.BEndAccountOpeningVO
     *@Date: 2021/5/10 9:18
     **/
    BEndAccountOpeningVO queryWalletByBizLicNo(BizLicNoQO bizLicNoQO);

    /**
     *@Author: Pipi
     *@Description: 发送提现申请
     *@Param: withdrawQO:
     *@Return: com.jsy.community.vo.WithdrawVO
     *@Date: 2021/5/10 10:38
     **/
    WithdrawVO withdrawApply(WithdrawQO withdrawQO, String uid);

    /**
     *@Author: Pipi
     *@Description: 激活账户
     *@Param: activeAcctQO: 
     *@Return: com.jsy.community.vo.ActiveAcctVO
     *@Date: 2021/5/12 17:24
     **/
    ActiveAcctVO activeAcct(ActiveAcctQO activeAcctQO);
}
