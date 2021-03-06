package com.jsy.community.controller;

import com.jsy.community.api.IRedbagService;
import com.jsy.community.api.IUserAccountRecordService;
import com.jsy.community.api.IUserAccountService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.UserAccountRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RedbagQO;
import com.jsy.community.qo.UserAccountRecordQO;
import com.jsy.community.qo.UserTicketQO;
import com.jsy.community.qo.proprietor.UserAccountTradeQO;
import com.jsy.community.qo.proprietor.UserWithdrawalQ0;
import com.jsy.community.qo.proprietor.ZhiFuBaoAccountBindingQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserAccountVO;
import com.jsy.community.vo.WithdrawalResulrVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.BaseWallet;
import com.zhsj.base.api.domain.WalletBalanceChange;
import com.zhsj.base.api.rpc.IBaseAuthRpcService;
import com.zhsj.base.api.rpc.IBaseWalletRpcService;
import com.zhsj.base.api.rpc.IBaseWithdrawalRpcService;
import com.zhsj.base.api.vo.PageVO;
import com.zhsj.basecommon.exception.BaseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 用户金钱账户控制器
 * @since 2021-01-08 11:41
 **/
@Api(tags = "用户金钱账户控制器")
@RestController
@RequestMapping("user/account")
// @ApiJSYController
public class UserAccountController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserAccountService userAccountService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserAccountRecordService userAccountRecordService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IRedbagService redbagService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseWalletRpcService baseWalletRpcService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseAuthRpcService baseAuthRpcService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseWithdrawalRpcService withdrawalRpcService;

    //========================== 用户账户 ==============================

    /**
     * @Description: 查询余额
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    @ApiOperation("【用户账户】查询余额")
    @GetMapping("balance")
    public CommonResult queryBalance() {
        BaseWallet baseWallet = baseWalletRpcService.getWalletByCon(UserUtils.getEHomeUserId(), "RMB");
        Map<String, Object> returnMap = new HashMap<>();
        if (baseWallet != null) {
            returnMap.put("balance", new BigDecimal(0E-8).compareTo(baseWallet.getBalance()) == 0 ? new BigDecimal(0.00) : baseWallet.getBalance().setScale(2, RoundingMode.DOWN));
        } else {
            returnMap.put("balance", new BigDecimal(0.00));
        }
        return CommonResult.ok(returnMap, "查询成功");
    }
    // @Permit("community:proprietor:user:account:balance")
    /*public CommonResult queryBalance() {
        UserAccountVO userAccountVO = userAccountService.queryBalance(UserUtils.getUserId());
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("balance", userAccountVO.getBalance().setScale(2, RoundingMode.HALF_UP).toPlainString());
        return CommonResult.ok(returnMap, "查询成功");
    }*/

    /**
     * @Description: 账户交易
     * @Param: [userAccountTradeQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    @ApiOperation("【用户账户】账户交易")
    @PostMapping("trade")
    // @Permit("community:proprietor:user:account:trade")
    public CommonResult trade(@RequestBody UserAccountTradeQO userAccountTradeQO) {
        ValidatorUtils.validateEntity(userAccountTradeQO);
        userAccountTradeQO.setUid(UserUtils.getUserId());
        userAccountService.trade(userAccountTradeQO);
        return CommonResult.ok("交易完成");
    }

    /**
     * @Description: 查询账户流水
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/2/7
     **/
    @ApiOperation("【用户账户】流水查询")
    @PostMapping("record")
    // @Permit("community:proprietor:user:account:record")
    public CommonResult<PageInfo<UserAccountRecordEntity>> queryAccountRecord(@RequestBody BaseQO<UserAccountRecordQO> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new UserAccountRecordQO());
        }
        PageVO<WalletBalanceChange> walletBalanceChange = baseWalletRpcService.getWalletBalanceChange(UserUtils.getEHomeUserId(), baseQO.getPage().intValue(), baseQO.getSize().intValue(), baseQO.getQuery().getTradeType());
        List<UserAccountRecordEntity> recordEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(walletBalanceChange.getData())) {
            for (WalletBalanceChange datum : walletBalanceChange.getData()) {
                UserAccountRecordEntity userAccountRecordEntity = new UserAccountRecordEntity();
                userAccountRecordEntity.setUid(UserUtils.getUserId());
                userAccountRecordEntity.setTradeFromStr(datum.getTitle());
                userAccountRecordEntity.setTradeType(datum.getChangeType());
                userAccountRecordEntity.setTradeTypeStr(PaymentEnum.TradeTypeEnum.tradeTypeMap.get(datum.getChangeType()));
                userAccountRecordEntity.setTradeAmount(datum.getAmount().setScale(2, RoundingMode.DOWN));
                userAccountRecordEntity.setTradeAmountStr(String.valueOf(datum.getAmount().setScale(2, RoundingMode.DOWN)));
                userAccountRecordEntity.setBalance(datum.getBalance().setScale(2, RoundingMode.DOWN));
                userAccountRecordEntity.setBalanceStr(String.valueOf(datum.getBalance().setScale(2, RoundingMode.DOWN)));
                userAccountRecordEntity.setComment(datum.getRemark());
                userAccountRecordEntity.setId(datum.getId());
                userAccountRecordEntity.setIdStr(String.valueOf(datum.getId()));
                userAccountRecordEntity.setCreateTime(datum.getCreateTime());
                userAccountRecordEntity.setUpdateTime(datum.getUpdateTime());
                recordEntities.add(userAccountRecordEntity);
            }
        }
        PageInfo<UserAccountRecordEntity> pageInfo = new PageInfo<>();
        pageInfo.setRecords(recordEntities);
        pageInfo.setTotal(walletBalanceChange.getTotal());
        pageInfo.setSize(walletBalanceChange.getPageSize());
        pageInfo.setCurrent(walletBalanceChange.getPageNum());
        return CommonResult.ok(pageInfo);
    }
    /*public CommonResult<PageInfo<UserAccountRecordEntity>> queryAccountRecord(@RequestBody BaseQO<UserAccountRecordQO> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new UserAccountRecordQO());
        }
        baseQO.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(userAccountRecordService.queryAccountRecord(baseQO));
    }*/

    @ApiOperation(value = "验证支付密码", notes = "需要登录")
    @GetMapping("/password/pay/check")
    // @Permit("community:proprietor:user:account:password:pay:check")
    public CommonResult<Boolean> checkPayPassword(@RequestParam String payPassword) {
        try {
            return CommonResult.ok(baseAuthRpcService.checkPayPassword(UserUtils.getEHomeUserId(), payPassword));
        } catch (BaseException baseException) {
            /*if (baseException.getErrorEnum().getCode().equals(ErrorEnum.PAY_PASSWORD_ERROR.getCode())) {
                return CommonResult.ok(false);
            }*/
        }
        return CommonResult.ok(false);
    }
    /*public CommonResult<Boolean> checkPayPassword(@RequestParam String payPassword) {
        String uid = UserUtils.getUserId();
        return CommonResult.ok(userAccountService.checkPayPassword(uid, payPassword));
    }*/

    @ApiOperation(value = "用户余额提现至微信", notes = "需要登录")
    @PostMapping("/wechat/withdrawal")
    // @Permit("community:proprietor:user:account:wechat:withdrawal")
    public CommonResult<WithdrawalResulrVO> wechatWithdrawal(@RequestBody UserWithdrawalQ0 userWithdrawalQ0) {
        ValidatorUtils.validateEntity(userWithdrawalQ0);
        withdrawalRpcService.walletWithdrawalToWeChat(UserUtils.getEHomeUserId(), userWithdrawalQ0.getPayPassword(), userWithdrawalQ0.getAmount(), "余额提现到微信");
        WithdrawalResulrVO withdrawalResulrVO = new WithdrawalResulrVO();
        withdrawalResulrVO.setCode("0");
        withdrawalResulrVO.setMsg("");
        withdrawalResulrVO.setSuccess(true);
        return CommonResult.ok(withdrawalResulrVO, "提现成功");
    }

    /*public CommonResult<WithdrawalResulrVO> wechatWithdrawal(@RequestBody UserWithdrawalQ0 userWithdrawalQ0) {
        String uid = UserUtils.getUserId();
        return CommonResult.ok(userAccountService.wechatWithdrawal(userWithdrawalQ0, uid));
    }*/

    @ApiOperation(value = "用户余额提现至支付宝", notes = "需要登录")
    @PostMapping("/zhifubao/withdrawal")
    // @Permit("community:proprietor:user:account:zhifubao:withdrawal")
    public CommonResult<WithdrawalResulrVO> zhifubaoWithdrawal(@RequestBody UserWithdrawalQ0 userWithdrawalQ0) {
        ValidatorUtils.validateEntity(userWithdrawalQ0);
        withdrawalRpcService.walletWithdrawalToAliPay(UserUtils.getEHomeUserId(), userWithdrawalQ0.getPayPassword(), userWithdrawalQ0.getAmount(), "余额提现到支付宝");
        WithdrawalResulrVO withdrawalResulrVO = new WithdrawalResulrVO();
        withdrawalResulrVO.setCode("0");
        withdrawalResulrVO.setMsg("");
        withdrawalResulrVO.setSuccess(true);
        return CommonResult.ok(withdrawalResulrVO, "提现成功");
    }
    /*public CommonResult<WithdrawalResulrVO> zhifubaoWithdrawal(@RequestBody UserWithdrawalQ0 userWithdrawalQ0) {
        String uid = UserUtils.getUserId();
        return CommonResult.ok(userAccountService.zhiFuBaoWithdrawal(userWithdrawalQ0, uid));
    }*/

    @ApiOperation(value = "查询已绑定支付宝账户", notes = "需要登录")
    @GetMapping("/zhifubao/account/query")
    // @Permit("community:proprietor:user:account:zhifubao:account:query")
    @Deprecated
    public CommonResult<ZhiFuBaoAccountBindingQO> queryZhiFuBaoAccount() {
        return CommonResult.ok(userAccountService.queryZhiFuBaoAccount(UserUtils.getUserId()));
    }

    @ApiOperation(value = "绑定(修改)支付宝账户", notes = "需要登录")
    @PostMapping("/zhifubao/account/binding")
    // @Permit("community:proprietor:user:account:zhifubao:account:binding")
    @Deprecated
    public CommonResult<Boolean> bindingZhiFuBaoAccount(@RequestBody ZhiFuBaoAccountBindingQO accountBindingQO) {
        String uid = UserUtils.getUserId();
        userAccountService.bindingZhiFuBaoAccount(uid, accountBindingQO);
        return CommonResult.ok();
    }

    @ApiOperation(value = "解绑支付宝账户", notes = "需要登录")
    @GetMapping("/zhifubao/account/unbundling")
    // @Permit("community:proprietor:user:account:zhifubao:account:unbundling")
    @Deprecated
    public CommonResult<Boolean> unbundlingZhiFuBaoAccount() {
        String uid = UserUtils.getUserId();
        userAccountService.unbundlingZhiFuBaoAccount(uid);
        return CommonResult.ok();
    }

    //========================== 红包 ==============================

    /**
     * @Description: 单发红包、转账
     * @Param: [redBagQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/18
     **/
    @ApiOperation("【红包/转账】单发")
    @PostMapping("redbag/send/single")
    // @Permit("community:proprietor:user:account:redbag:send:single")
    public CommonResult sendSingleRedbag(@RequestBody RedbagQO redbagQO) {
        ValidatorUtils.validateEntity(redbagQO, RedbagQO.singleRedbagValidated.class);
        if (BusinessConst.BUSINESS_TYPE_GROUP_REDBAG.equals(redbagQO.getBusinessType())) {
            return CommonResult.error("请使用群红包服务");
        }
        if (new BigDecimal("0.01").compareTo(redbagQO.getMoney()) == 1) {
            return CommonResult.error("金额过小");
        }
        if (BusinessConst.BUSINESS_TYPE_PRIVATE_REDBAG.equals(redbagQO.getBusinessType())
                && new BigDecimal("200").compareTo(redbagQO.getMoney()) == -1) {
            return CommonResult.error("红包金额超限");
        }
        redbagQO.setFromType(BusinessConst.REDBAG_FROM_TYPE_PERSON);//目前写死个人红包，调用方不用传
        redbagQO.setType(PaymentEnum.CurrencyEnum.CURRENCY_CNY.getIndex());
        redbagQO.setGroupUuid(null);
        redbagQO.setNumber(1);
        redbagQO.setUserUuid(UserUtils.getUserId());
        redbagQO.setBehavior(BusinessConst.BEHAVIOR_SEND);
        redbagService.sendRedbag(redbagQO);
        return CommonResult.ok("发送成功");
    }

    /**
     * @Description: 群发红包
     * @Param: [redBagQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/18
     **/
    @ApiOperation("【红包】群发红包")
    @PostMapping("redbag/send/group")
    // @Permit("community:proprietor:user:account:redbag:send:group")
    public CommonResult sendGroupRedbag(@RequestBody RedbagQO redbagQO) {
        ValidatorUtils.validateEntity(redbagQO, RedbagQO.groupRedbagValidated.class);
        if (redbagQO.getMoney().doubleValue() / redbagQO.getNumber() < 0.01) {
            return CommonResult.error("人数太多或金额太小");
        }
        redbagQO.setFromType(BusinessConst.REDBAG_FROM_TYPE_PERSON);//目前写死群发红包，调用方不用传
        redbagQO.setType(PaymentEnum.CurrencyEnum.CURRENCY_CNY.getIndex());
        redbagQO.setReceiveUserUuid(null);
        redbagQO.setUserUuid(UserUtils.getUserId());
        redbagQO.setBusinessType(BusinessConst.BUSINESS_TYPE_GROUP_REDBAG);
        redbagQO.setBehavior(BusinessConst.BEHAVIOR_SEND);
        redbagService.sendRedbag(redbagQO);
        return CommonResult.ok("发送成功");
    }

    /**
     * @Description: 领取红包/转账
     * @Param: [redbagQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/25
     **/
    @ApiOperation("【红包/转账】领取")
    @PostMapping("redbag/receive/single")
    // @Permit("community:proprietor:user:account:redbag:receive:single")
    public CommonResult receiveSingleRedbag(@RequestBody RedbagQO redbagQO) {
        ValidatorUtils.validateEntity(redbagQO, RedbagQO.receiveSingleValidated.class);
        if (!BusinessConst.BUSINESS_TYPE_PRIVATE_REDBAG.equals(redbagQO.getBusinessType())
                && !BusinessConst.BUSINESS_TYPE_TRANSFER.equals(redbagQO.getBusinessType())) {
            return CommonResult.error("请明确领取红包还是转账");
        }
        redbagQO.setBehavior(BusinessConst.BEHAVIOR_RECEIVE);
        return CommonResult.ok(redbagService.receiveRedbag(redbagQO), "领取成功");
    }

    /**
     * @Description: 领取群红包
     * @Param: [redbagQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/25
     **/
    @ApiOperation("【群红包】领取")
    @PostMapping("redbag/receive/group")
    // @Permit("community:proprietor:user:account:redbag:receive:group")
    public CommonResult receiveGroupRedbag(@RequestBody RedbagQO redbagQO) {
        ValidatorUtils.validateEntity(redbagQO, RedbagQO.receiveSingleValidated.class);
        redbagQO.setBusinessType(BusinessConst.BUSINESS_TYPE_GROUP_REDBAG);
        redbagQO.setBehavior(BusinessConst.BEHAVIOR_RECEIVE);
        return CommonResult.ok(redbagService.receiveRedbag(redbagQO), "领取成功");
    }

    //========================== 现金券 ==============================

    /**
     * @Description: 统计用户可用券张数
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @ApiOperation("【全平台抵用券】统计可用张数")
    @GetMapping("tickets/count")
    // @Permit("community:proprietor:user:account:tickets:count")
    public CommonResult countAvailableTickets() {
        return CommonResult.ok(userAccountService.countTicketByUid(UserUtils.getUserId()));
    }

    /**
     * @Description: 查用户拥有的所有券
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @ApiOperation("【全平台抵用券】查询")
    @PostMapping("tickets")
    // @Permit("community:proprietor:user:account:tickets")
    public CommonResult queryTickets(@RequestBody BaseQO<UserTicketQO> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new UserTicketQO());
        }
        baseQO.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(userAccountService.queryTickets(baseQO), "查询成功");
    }

    /**
     * @Description: id单查
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @ApiOperation("【全平台抵用券】id单查")
    @GetMapping("ticket")
    // @Permit("community:proprietor:user:account:ticket")
    public CommonResult queryTicketById(@RequestParam Long id) {
        return CommonResult.ok(userAccountService.queryTicketById(id, UserUtils.getUserId()), "查询成功");
    }

    /**
     * @Description: 使用券
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @ApiOperation("【全平台抵用券】使用")
    @PutMapping("ticket/use")
    // @Permit("community:proprietor:user:account:ticket:use")
    public CommonResult useTicket(@RequestParam Long id) {
        boolean b = userAccountService.useTicket(id, UserUtils.getUserId());
        return b ? CommonResult.ok("使用成功") : CommonResult.error("使用失败");
    }

    /**
     * @Description: 退回券
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @ApiOperation("【全平台抵用券】退回")
    @PutMapping("ticket/rollback")
    // @Permit("community:proprietor:user:account:ticket:rollback")
    public CommonResult rollbackTicket(@RequestParam Long id) {
        boolean b = userAccountService.rollbackTicket(id, UserUtils.getUserId());
        return b ? CommonResult.ok("退回成功") : CommonResult.error("退回失败");
    }

    //============================= 查询整合接口 ==================================
    @ApiOperation("【整合查询】(个人中心)")
    @GetMapping("all")
    // @Permit("community:proprietor:user:account:all")
    public CommonResult queryAll() {
        Map<String, Object> returnMap = new HashMap<>();
        String uid = UserUtils.getUserId();
        UserAccountVO balance = userAccountService.queryBalance(uid);
        Integer tickets = userAccountService.countTicketByUid(uid);
        returnMap.put("balance", balance.getBalance().setScale(2, RoundingMode.HALF_UP).toPlainString());
        returnMap.put("tickets", tickets);
        returnMap.put("bankCard", 0);
        return CommonResult.ok(returnMap, "查询成功");
    }
}
