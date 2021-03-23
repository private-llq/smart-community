package com.jsy.community.api;

import com.jsy.community.qo.proprietor.BindingMobileQO;
import com.jsy.community.vo.UserAuthVo;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-08 10:51
 **/
public interface IWeChatLoginService {
    UserAuthVo login(String openid);

    UserAuthVo bindingMobile(BindingMobileQO bindingMobileQO);
}
