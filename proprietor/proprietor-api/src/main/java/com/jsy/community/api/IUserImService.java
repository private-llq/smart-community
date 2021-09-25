package com.jsy.community.api;

import com.jsy.community.entity.UserIMEntity;

/**
 * @Description: im
 * @author: Hu
 * @since: 2021/9/25 15:28
 * @Param:
 * @return:
 */
public interface IUserImService {
    /**
     * @Description: 根据uidimid
     * @author: Hu
     * @since: 2021/9/25 15:33
     * @Param:
     * @return:
     */
    UserIMEntity selectUid(String uid);
}
