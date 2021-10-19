package com.jsy.community.api;

import com.jsy.community.entity.UserTicketEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RedbagQO;
import com.jsy.community.qo.UserTicketQO;
import com.jsy.community.qo.proprietor.UserAccountTradeQO;
import com.jsy.community.qo.proprietor.UserWithdrawalQ0;
import com.jsy.community.qo.proprietor.ZhiFuBaoAccountBindingQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.UserAccountVO;
import com.jsy.community.vo.WithdrawalResulrVO;

import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 用户账户Service
 * @since 2021-01-08 11:13
 **/
public interface IUserAccountService {

    /**
     * @Description: 创建用户账户
     * @Param: [uid]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    boolean createUserAccount(String uid);

    /**
     * @Description: 查询余额
     * @Param: [uid]
     * @Return: com.jsy.community.vo.UserAccountVO
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    UserAccountVO queryBalance(String uid);

    /**
     * @Description: 账户交易
     * @Param: [userid, uAccountRecordQO]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    void trade(UserAccountTradeQO uAccountRecordQO);

    /**
     * @Description: 统计用户可用券张数
     * @Param: [uid]
     * @Return: java.lang.Integer
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    Integer countTicketByUid(String uid);

    /**
     * @Description: 查用户拥有的所有券
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.UserTicketEntity>
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    PageInfo<UserTicketEntity> queryTickets(BaseQO<UserTicketQO> baseQO);

    /**
     * @Description: 单查
     * @Param: [id, uid]
     * @Return: com.jsy.community.entity.UserTicketEntity
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    UserTicketEntity queryTicketById(Long id, String uid);

    /**
     * @Description: 使用
     * @Param: [id, uid]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    boolean useTicket(Long id, String uid);

    /**
     * @Description: 退回
     * @Param: [id, uid]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    boolean rollbackTicket(Long id, String uid);

    /**
     * 校验支付密码
     *
     * @param uid         用户ID
     * @param payPassword 用户输入的支付密码
     * @return
     */
    boolean checkPayPassword(String uid, String payPassword);

    /**
     * 钱包余额提现至微信
     *
     * @param userWithdrawalQ0 提现金额相关
     * @param uid              用户
     */
    WithdrawalResulrVO wechatWithdrawal(UserWithdrawalQ0 userWithdrawalQ0, String uid);

    /**
     * 钱包余额提现至支付宝
     *
     * @param userWithdrawalQ0 提现金额相关
     * @param uid              用户
     */
    WithdrawalResulrVO zhiFuBaoWithdrawal(UserWithdrawalQ0 userWithdrawalQ0, String uid);

    /**
     * 查询绑定的支付宝账户
     *
     * @param uid 用户id
     * @return
     */
    ZhiFuBaoAccountBindingQO queryZhiFuBaoAccount(String uid);

    /**
     * 解绑支付宝提现账户
     *
     * @param uid
     */
    void unbundlingZhiFuBaoAccount(String uid);

    /**
     * @param uid              用户ID
     * @param accountBindingQO 绑定的支付宝账户信息
     */
    void bindingZhiFuBaoAccount(String uid, ZhiFuBaoAccountBindingQO accountBindingQO);

    /**
     * @author: Pipi
     * @description: 签约支付后租金入账房东账户 
     * @param conId: 合同ID,传合同ID的原因是防止重复加金额
     * @param amount: 入账金额
     * @param uid: 房东ID
     * @return: java.lang.Integer
     * @date: 2021/10/15 11:43
     **/
    Integer rentalIncome(String conId, BigDecimal amount, String uid);
}
