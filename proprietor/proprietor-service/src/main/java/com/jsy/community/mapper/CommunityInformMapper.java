package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PushInformEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author YuLF
 * @since 2020-11-16
 */
public interface CommunityInformMapper extends BaseMapper<PushInformEntity> {



    /**
     * 社区主页 当前轮播消息 查询最近的  initialInformCount 条数量
     * @param initialInformCount     初始轮播消息条数
     * @return                       返回消息列表
     */
    List<PushInformEntity> rotationCommunityInform(@Param("initialCount") Integer initialInformCount, @Param("acctId") Long communityId);

    /**
     * 增加一次该消息的浏览量
     * @param acctId            推送号ID
     * @param informId          消息ID
     */
    @Update("update t_acct_push_inform set browse_count = browse_count+1 where acct_id = #{acctId} and id = #{informId} and deleted = 0")
    void updatePushInformBrowseCount(@Param("acctId") Long acctId, @Param("informId") Long informId);

    /**
     * 根据推送帐号id 和 uid 查询用户未读的推送消息id
     * @param acctId    推送号id
     * @param uid       用户id
     */
    List<Long> selectUnreadInformId(@Param("acctId") Long acctId,@Param("uid") String uid);

    /**
     * 标记未读消息id列表 标记为已读
     * @param unreadInformIds   当前推送号id 中当前用户未读的消息id
     * @param acctId            推送号id
     * @param uid               当前用户id
     */
    void insertBatchReadInform(@Param("ids") List<Long> unreadInformIds, @Param("acctId") Long acctId, @Param("uid") String uid);


    /**
     * 用户消息列表 左滑动 删除推送号(屏蔽)
     * @param acctId    推送号ID
     * @param userId    用户id
     */
    void insertClearRecord(@Param("id")Long id, @Param("acctId") Long acctId, @Param("uid") String userId);
}
