package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PushInformEntity;
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

    /**
     * 通过推送号ID 清理在t_acct_push_del 表中 用户手动在消息列表 左滑动删除的推送号记录
     * 主要是用于 在新增推送消息时，重新给用户推送未读信息，在t_acct_push_del存在的推送号账号，拉取用户消息列表时，不会被拉取
     */
    @Delete("delete from t_acct_push_del where acct_id = #{acctId}")
    void clearPushDel(@Param("acctId") Long acctId);
}
