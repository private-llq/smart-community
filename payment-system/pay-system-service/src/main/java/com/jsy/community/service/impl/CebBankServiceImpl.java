package com.jsy.community.service.impl;

import com.jsy.community.api.CebBankService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.cebbank.CebBaseQO;
import com.jsy.community.qo.cebbank.CebLoginQO;
import com.jsy.community.untils.cebbank.CebBankContributionUtil;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费服务实现
 * @Date: 2021/11/15 17:20
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_payment)
public class CebBankServiceImpl implements CebBankService {

    /**
     * @author: Pipi
     * @description: 光大银行用户注册
     * @param cebLoginQO:
     * @return: void
     * @date: 2021/11/15 17:25
     **/
    @Override
    public void login(CebLoginQO cebLoginQO) {
        String loginResponse = CebBankContributionUtil.login(cebLoginQO);
    }
}
