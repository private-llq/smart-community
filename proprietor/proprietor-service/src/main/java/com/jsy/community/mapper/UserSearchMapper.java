package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserSearchEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-16 16:49
 **/
public interface UserSearchMapper extends BaseMapper<UserSearchEntity> {
    /**
     * @Description: 删除个人搜索历史
     * @author: Hu
     * @since: 2021/4/19 14:23
     * @Param:
     * @return:
     */
    void deleteUserKey(@Param("uid") String uid);
}
