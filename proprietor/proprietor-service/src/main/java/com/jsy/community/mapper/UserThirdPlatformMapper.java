package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserThirdPlatformEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * @author chq459799974
 * @description 三方登录表Mapper
 * @since 2021-01-11 16:03
 **/
public interface UserThirdPlatformMapper extends BaseMapper<UserThirdPlatformEntity> {
    /**
     * 直接物理删除支付宝绑定账户，防止产生逻辑删除数据冗余
     * @param uid 用户id
     */
    @Delete("delete from t_user_third_platform where uid = #{uid} and third_platform_type = 5")
    void deleteZhiFuBaoBinDing(@Param("uid") String uid);
}
