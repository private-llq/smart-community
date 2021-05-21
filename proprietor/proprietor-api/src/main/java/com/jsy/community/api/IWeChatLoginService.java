package com.jsy.community.api;

import com.jsy.community.qo.proprietor.BindingMobileQO;
import com.jsy.community.vo.UserAuthVo;

/**
 * @program: com.jsy.community
 * @description: 三方微信登录
 * @author: Hu
 * @create: 2021-03-08 10:51
 **/
public interface IWeChatLoginService {
    /**
     * @Description: 登录
     * @author: Hu
     * @since: 2021/5/21 13:58
     * @Param: openid
     * @return: UserAuthVo
     */
    UserAuthVo login(String openid);

    /**
     * @Description: 绑定手机
     * @author: Hu
     * @since: 2021/5/21 13:58
     * @Param: bindingMobileQO
     * @return: UserAuthVo
     */
    UserAuthVo bindingMobile(BindingMobileQO bindingMobileQO);
}
