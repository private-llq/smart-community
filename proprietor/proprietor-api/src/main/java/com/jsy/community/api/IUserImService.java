package com.jsy.community.api;

import com.jsy.community.entity.UserIMEntity;

import java.util.List;
import java.util.Set;

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

    /**
     * @Description: uid批量查询
     * @author: Hu
     * @since: 2021/9/25 16:34
     * @Param:
     * @return:
     */
    List<UserIMEntity> selectUidAll(Set<String> uidAll);
}
