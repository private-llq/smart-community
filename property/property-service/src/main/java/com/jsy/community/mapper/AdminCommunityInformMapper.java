package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.qo.proprietor.CommunityInformQO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author YuLF
 * @since 2020-11-16
 */
public interface AdminCommunityInformMapper extends BaseMapper<CommunityInformEntity> {

    Integer updateCommunityInform(CommunityInformQO communityInformQO);



    /**
     * 删除社区消息时 同事物理删除用户已读信息
     * @param id  社区消息ID
     */
    @Delete("delete from t_user_inform where inform_id = #{id}")
    void delUserReadInform(@Param("id") Long id);



}
