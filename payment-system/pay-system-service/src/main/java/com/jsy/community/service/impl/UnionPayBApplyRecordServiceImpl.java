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
import com.jsy.community.qo.unionpay.CredentialNotifyQO;
import com.jsy.community.qo.unionpay.CredentialQO;
import com.jsy.community.qo.unionpay.ResetBtypeAcctPwdQO;
import com.jsy.community.untils.UnionPayUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.unionpay.CredentialResponseVO;
import com.jsy.community.vo.unionpay.CredentialVO;
import com.jsy.community.vo.unionpay.OpenApiResponseVO;
import com.jsy.community.vo.unionpay.ResetBtypeAcctPwdVO;
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
     *@Description: ????????????????????????
     *@Param: credentialsQO:
     *@Return: com.jsy.community.vo.livingpayment.UnionPay.CredentialsVO
     *@Date: 2021/5/7 17:55
     **/
    @Override
    public CredentialVO getCredential(CredentialQO credentialQO, String uid) {
        CredentialVO credentialVO = new CredentialVO();
        OpenApiResponseVO response = unionPayService.getCredential(credentialQO);
        if (response.getResponse() == null || !UnionPayConfig.SUCCESS_CODE.equals(response.getCode())) {
            log.info("??????????????????!");
            return credentialVO;
        }
        CredentialResponseVO credentialResponseVO = JSONObject.parseObject(response.getBody(), CredentialResponseVO.class);
        if (credentialResponseVO != null && UnionPayConfig.SUCCESS_CODE.equals(credentialResponseVO.getCode())
                && ("10".equals(credentialQO.getJumpType()) || "11".equals(credentialQO.getJumpType()))) {
            // ??????????????????????????????,??????????????????,??????????????????????????????
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
     *@Description: B?????????????????????????????????????????????????????????
     *@Param: credentialNotifyQO:
     *@Return: void
     *@Date: 2021/5/10 17:59
     **/
    @Override
    public void updateBApplyRecord(CredentialNotifyQO credentialNotifyQO) {
        // ?????????????????????????????????????????????
        UnionPayBApplyRecordEntity bApplyRecordEntity = bApplyRecordMapper.selectOneByRegisterNo(credentialNotifyQO);
        if (bApplyRecordEntity != null) {
            // ???????????????????????????,??????????????????
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
            // ?????????????????????,??????????????????
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
     * @Description: B???????????????????????????
     * @Param: resetBtypeAcctPwdQO:
     * @Return: java.lang.Boolean
     * @Date: 2021/5/11 17:48
     */
    @Override
    public Boolean resetBtypeAcctPwd(ResetBtypeAcctPwdQO resetBtypeAcctPwdQO) {
        // ????????????json
        String msgBody = UnionPayUtils.buildMsgBody(resetBtypeAcctPwdQO);
        OpenApiResponseVO response = UnionPayUtils.transApi(msgBody, UnionPayConfig.RESET_BTYPE_ACCT_PWD);
        if (response.getResponse() == null || !UnionPayConfig.SUCCESS_CODE.equals(response.getCode())) {
            log.info("B?????????????????????????????????!{}", response.getMsg());
            return false;
        }
        ResetBtypeAcctPwdVO resetBtypeAcctPwdVO = JSONObject.parseObject(response.getResponse().getMsgBody(), ResetBtypeAcctPwdVO.class);
        if (resetBtypeAcctPwdVO == null || !UnionPayConfig.SUCCESS_CODE.equals(resetBtypeAcctPwdVO.getRspCode())) {
            log.info("B?????????????????????????????????!{}", resetBtypeAcctPwdVO.getRspResult());
            throw new PaymentException("B?????????????????????????????????!" + resetBtypeAcctPwdVO.getRspResult());
        }
        return true;
    }
}
