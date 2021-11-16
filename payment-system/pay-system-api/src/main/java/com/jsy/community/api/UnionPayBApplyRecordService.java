package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.payment.UnionPayBApplyRecordEntity;
import com.jsy.community.qo.unionpay.CredentialNotifyQO;
import com.jsy.community.qo.unionpay.CredentialQO;
import com.jsy.community.qo.unionpay.ResetBtypeAcctPwdQO;
import com.jsy.community.vo.unionpay.CredentialVO;

/**
 * @Author: Pipi
 * @Description: B端用户开户申请记录服务
 * @Date: 2021/5/11 10:34
 * @Version: 1.0
 **/
public interface UnionPayBApplyRecordService extends IService<UnionPayBApplyRecordEntity> {

    /**
     *@Author: Pipi
     *@Description: 获取银联支付凭据
     *@Param: credentialsQO:
     *@Return: com.jsy.community.vo.livingpayment.UnionPay.CredentialsVO
     *@Date: 2021/5/7 17:55
     **/
    CredentialVO getCredential(CredentialQO credentialQO, String uid);

    /**
     *@Author: Pipi
     *@Description: B端开户凭据回调接口的更新申请记录的操作
     *@Param: credentialNotifyQO:
     *@Return: void
     *@Date: 2021/5/10 17:59
     **/
    void updateBApplyRecord(CredentialNotifyQO credentialNotifyQO);

    /**
     *@Author: Pipi
     *@Description: B端钱包重置支付密码
     *@Param: resetBtypeAcctPwdQO:
     *@Return: java.lang.Boolean
     *@Date: 2021/5/11 17:48
     **/
    Boolean resetBtypeAcctPwd(ResetBtypeAcctPwdQO resetBtypeAcctPwdQO);
}
