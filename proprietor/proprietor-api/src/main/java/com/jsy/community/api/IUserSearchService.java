package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserSearchEntity;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-16 16:49
 **/
public interface IUserSearchService extends IService<UserSearchEntity> {
    /**
     * @Description: 添加个人搜索词汇
     * @author: Hu
     * @since: 2021/4/16 16:53
     * @Param:
     * @return:
     */
    void addSearchHotKey(String userId, String text);

    /**
     * @Description: 查询个人搜索词汇
     * @author: Hu
     * @since: 2021/4/16 17:04
     * @Param:
     * @return:
     */
    String[] searchUserKey(String userId, Integer num);
    /**
     * @Description: 删除个人搜索词汇
     * @author: Hu
     * @since: 2021/4/16 17:04
     * @Param:
     * @return:
     */
    void deleteUserKey(String userId);
}
