package com.jsy.community.api;

import com.jsy.community.qo.proprietor.UserInformQO;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 查询通知已读未读
 * @author: Hu
 * @create: 2020-12-07 16:44
 **/
public interface ISelectInformService {
    /**
     * @Description: 查询通知已读未读
     * @author: Hu
     * @since: 2020/12/7 16:44
     * @Param:
     * @return:
     */
    Map<String, Object> findList(UserInformQO userInformQO);

    Map<String, Object> findNotList(UserInformQO userInformQO);
}
