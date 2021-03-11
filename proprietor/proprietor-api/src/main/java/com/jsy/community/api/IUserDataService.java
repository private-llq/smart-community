package com.jsy.community.api;

import com.jsy.community.qo.proprietor.UserDataQO;
import com.jsy.community.vo.UserDataVO;

/**
 * @program: com.jsy.community
 * @description: 用户个人信息
 * @author: Hu
 * @create: 2021-03-11 13:38
 **/
public interface IUserDataService {
    /**
     * @Description: 查询一条信息
     * @author: Hu
     * @since: 2021/3/11 14:20
     * @Param:
     * @return:
     */
    UserDataVO selectUserDataOne(String userId);

    /**
     * @Description: 修改个人资料
     * @author: Hu
     * @since: 2021/3/11 14:44
     * @Param:
     * @return:
     */
    void updateUserData(UserDataQO userDataQO,String userId);
}
