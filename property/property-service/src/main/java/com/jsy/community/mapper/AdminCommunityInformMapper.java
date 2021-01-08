package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.proprietor.CommunityInformQO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author YuLF
 * @since 2020-11-16
 */
public interface AdminCommunityInformMapper extends BaseMapper<PushInformEntity> {

    Integer updateCommunityInform(CommunityInformQO communityInformQO);

    /**
     * 删除社区消息时 同事物理删除用户已读信息
     * @param id  社区消息ID
     */
    @Delete("delete from t_user_inform where inform_id = #{id}")
    void delUserReadInform(@Param("id") Long id);


    /**
     * 根据推送帐号id 和 uid 查询用户未读的推送消息id
     * @param acctId    推送号id
     * @param uid       用户id
     */
    List<Long> selectUnreadInformId(@Param("acctId") Long acctId, @Param("uid") String uid);

    /**
     * 标记未读消息id列表 标记为已读
     * @param unreadInformIds   当前推送号id 中当前用户未读的消息id
     * @param acctId            推送号id
     * @param uid               当前用户id
     */
    void insertBatchReadInform(@Param("ids") List<Long> unreadInformIds, @Param("acctId") Long acctId, @Param("uid") String uid);

}
