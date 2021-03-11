package com.jsy.community.mapper;

import com.jsy.community.qo.proprietor.UserDataQO;
import com.jsy.community.vo.UserDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-11 14:22
 **/
@Mapper
public interface UserDataMapper {
    UserDataVO selectUserDataOne(String userId);

    void updateUserData(@Param("userDataQO") UserDataQO userDataQO, @Param("userId") String userId);
}
