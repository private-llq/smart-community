package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.PaymentException;
import com.jsy.community.api.UnionPayBApplyRecordService;
import com.jsy.community.api.UnionPayService;;
import com.jsy.community.config.UnionPayConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.payment.BUnionPayWalletEntity;
import com.jsy.community.entity.payment.UnionPayBApplyRecordEntity;
import com.jsy.community.mapper.BUnionPayWalletMapper;
import com.jsy.community.mapper.UnionPayBApplyRecordMapper;
import com.jsy.community.qo.CredentialNotifyQO;
import com.jsy.community.qo.CredentialQO;
import com.jsy.community.qo.ResetBtypeAcctPwdQO;
import com.jsy.community.untils.UnionPayUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CredentialResponseVO;
import com.jsy.community.vo.CredentialVO;
import com.jsy.community.vo.OpenApiResponseVO;
import com.jsy.community.vo.ResetBtypeAcctPwdVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/5/11 10:35
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class UnionPayBApplyRecordServiceImpl extends ServiceImpl<UnionPayBApplyRecordMapper, UnionPayBApplyRecordEntity> implements UnionPayBApplyRecordService {

    /*@Autowired
    private UnionPayUtils UnionPayUtils;*/

    @Autowired
    private UnionPayBApplyRecordMapper bApplyRecordMapper;

    @Autowired
    private BUnionPayWalletMapper bUnionPayWalletMapper;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false, timeout = 1200000)
    private UnionPayService unionPayService;

    /**
     *@Author: Pipi
     *@Description: 获取银联支付凭据
     *@Param: credentialsQO:
     *@Return: com.jsy.community.vo.livingpayment.UnionPay.CredentialsVO
     *@Date: 2021/5/7 17:55
     **/
    @Override
    public CredentialVO getCredential(CredentialQO credentialQO, String uid) {
        CredentialVO credentialVO = new CredentialVO();
        OpenApiResponseVO response = unionPayService.getCredential(credentialQO);
        if (response.getResponse() == null || !UnionPayConfig.SUCCESS_CODE.equals(response.getCode())) {
            log.info("获取凭据失败!");
            return credentialVO;
        }
        CredentialResponseVO credentialResponseVO = JSONObject.parseObject(response.getBody(), CredentialResponseVO.class);
        if (credentialResponseVO != null && UnionPayConfig.SUCCESS_CODE.equals(credentialResponseVO.getCode())
                && ("10".equals(credentialQO.getJumpType()) || "11".equals(credentialQO.getJumpType()))) {
            // 当获取凭据用于开户时,且返回成功时,向数据库添加申请记录
            UnionPayBApplyRecordEntity bApplyRecordEntity = new UnionPayBApplyRecordEntity();
            bApplyRecordEntity.setUid(uid);
            bApplyRecordEntity.setJumpUrl(credentialResponseVO.getResponse().getJumpUrl());
            bApplyRecordEntity.setTicket(credentialResponseVO.getResponse().getTicket());
            bApplyRecordEntity.setRegisterNo(credentialResponseVO.getResponse().getRegisterNo());
            bApplyRecordEntity.setOperationType(0);
            bApplyRecordEntity.setRegStatus("00");
            bApplyRecordEntity.setId(SnowFlake.nextId());
            bApplyRecordEntity.setDeleted(0L);
            bApplyRecordMapper.insert(bApplyRecordEntity);
        }
        return credentialResponseVO.getResponse();
    }

    /**
     *@Author: Pipi
     *@Description: B端开户凭据回调接口的更新申请记录的操作
     *@Param: credentialNotifyQO:
     *@Return: void
     *@Date: 2021/5/10 17:59
     **/
    @Override
    public void updateBApplyRecord(CredentialNotifyQO credentialNotifyQO) {
        // 查询这个注册号的最新的一条记录
        UnionPayBApplyRecordEntity bApplyRecordEntity = bApplyRecordMapper.selectOneByRegisterNo(credentialNotifyQO);
        if (bApplyRecordEntity != null) {
            // 以最新的记录为基础,新增一条记录
            bApplyRecordEntity.setMobileNo(credentialNotifyQO.getMobileNo());
            bApplyRecordEntity.setConfirmAmtStatus(credentialNotifyQO.getConfirmAmtStatus());
            bApplyRecordEntity.setOperationType(1);
            bApplyRecordEntity.setId(SnowFlake.nextId());
            bApplyRecordEntity.setRegStatus(credentialNotifyQO.getRegStatus());
            if (StringUtils.isNotEmpty(credentialNotifyQO.getConfirmAmt())) {
                BigDecimal confirmAmt = new BigDecimal(credentialNotifyQO.getConfirmAmt()).divide(new BigDecimal(100));
                bApplyRecordEntity.setConfirmAmt(confirmAmt);
            }
            bApplyRecordEntity.setUnpassReasonContent(credentialNotifyQO.getUnpassReasonContent());
            bApplyRecordMapper.insert(bApplyRecordEntity);
            // 如果是开户成功,新增钱包信息
            if ("REG_OPEN_ACCT_SUCCESS".equals(credentialNotifyQO.getMsgType())) {
                BUnionPayWalletEntity bUnionPayWalletEntity = new BUnionPayWalletEntity();
                bUnionPayWalletEntity.setUid(bApplyRecordEntity.getUid());
                bUnionPayWalletEntity.setWalletId(credentialNotifyQO.getWalletId());
                bUnionPayWalletEntity.setWalletName(credentialNotifyQO.getWalletName());
                bUnionPayWalletEntity.setCompanyName(credentialNotifyQO.getCompanyName());
                bUnionPayWalletEntity.setBizLicNo(credentialNotifyQO.getBizLicNo());
                bUnionPayWalletEntity.setLegalName(credentialNotifyQO.getLegalName());
                bUnionPayWalletEntity.setBankAcctType(Integer.valueOf(credentialNotifyQO.getBankAcctType()));
                bUnionPayWalletEntity.setId(SnowFlake.nextId());
                bUnionPayWalletEntity.setDeleted(0L);
                bUnionPayWalletMapper.insert(bUnionPayWalletEntity);
            }
        }
    }


    /**
     * @Author: Pipi
     * @Description: B端钱包重置支付密码
     * @Param: resetBtypeAcctPwdQO:
     * @Return: java.lang.Boolean
     * @Date: 2021/5/11 17:48
     */
    @Override
    public Boolean resetBtypeAcctPwd(ResetBtypeAcctPwdQO resetBtypeAcctPwdQO) {
        // 构建请求json
        String msgBody = UnionPayUtils.buildMsgBody(resetBtypeAcctPwdQO);
        OpenApiResponseVO response = UnionPayUtils.transApi(msgBody, UnionPayConfig.RESET_BTYPE_ACCT_PWD);
        if (response.getResponse() == null || !UnionPayConfig.SUCCESS_CODE.equals(response.getCode())) {
            log.info("B端钱包重置支付密码失败!{}", response.getMsg());
            return false;
        }
        ResetBtypeAcctPwdVO resetBtypeAcctPwdVO = JSONObject.parseObject(response.getResponse().getMsgBody(), ResetBtypeAcctPwdVO.class);
        if (resetBtypeAcctPwdVO == null || !UnionPayConfig.SUCCESS_CODE.equals(resetBtypeAcctPwdVO.getRspCode())) {
            log.info("B端钱包重置支付密码失败!{}", resetBtypeAcctPwdVO.getRspResult());
            throw new PaymentException("B端钱包重置支付密码失败!" + resetBtypeAcctPwdVO.getRspResult());
        }
        return true;
    }
}
