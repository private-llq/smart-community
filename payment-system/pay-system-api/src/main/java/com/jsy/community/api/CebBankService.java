package com.jsy.community.api;

import com.jsy.community.qo.cebbank.CebBaseQO;
import com.jsy.community.qo.cebbank.CebLoginQO;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费服务
 * @Date: 2021/11/15 17:20
 * @Version: 1.0
 **/
public interface CebBankService {
    /*
     * @author: Pipi
     * @description: 光大银行用户注册
     * @param cebLoginQO:
     * @return: void
     * @date: 2021/11/15 17:23
     **/
    void login(CebLoginQO cebLoginQO);
}
