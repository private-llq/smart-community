package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.vo.lease.HouseLeaseVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
     * 社区主页 当前轮播消息 查询最近的  initialInformCount 条数量 (只获取当前小区)
     * @param initialInformCount     初始轮播消息条数
     * @param communityId            社区id、
     * @return                       返回消息列表
     */
    List<PushInformEntity> rotationCommunityInformSelf(@Param("initialCount") Integer initialInformCount, @Param("acctId") Long communityId);

    /**
     * 社区主页 当前轮播消息 查询最近的  initialInformCount 条数量 (获取当前小区和系统消息)
     * @param initialInformCount     初始轮播消息条数
     * @param communityId            社区id、
     * @return                       返回消息列表
     */
    List<PushInformEntity> rotationCommunityInform(@Param("initialCount") Integer initialInformCount, @Param("acctId") Long communityId);

    /**
     * 增加一次该消息的浏览量
     * @param acctId            推送号ID
     * @param informId          消息ID
     */
    @Update("update t_push_inform set browse_count = browse_count+1,update_time = now() where acct_id = #{acctId} and id = #{informId} and deleted = 0")
    void updatePushInformBrowseCount(@Param("acctId") Long acctId, @Param("informId") Long informId);

    /**
     * 根据推送帐号id 和 uid 查询用户未读的推送消息id
     * @param acctId    推送号id
     * @param uid       用户id
     * @return          返回未读信息id列表
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
     * @param id        消息id
     * @param acctId    推送号ID
     * @param userId    用户id
     */
    void insertClearRecord(@Param("id")Long id, @Param("acctId") Long acctId, @Param("uid") String userId);


    /**
     * 查询商铺最新的信息条数
     * @param limit     限制条数
     * @return          返回信息List
     */
    @Select("select id,title as houseTitle,'false' as leaseHouse from t_shop_lease where deleted = 0 ORDER BY create_time desc limit #{limit}")
    List<HouseLeaseVO> selectShopLatest(int limit);


    /**
     * 查询租房最新的信息条数
     * @param limit     限制条数
     * @return          返回信息List
     */
    @Select("select id,house_title as houseTitle,'true' as leaseHouse from t_house_lease where deleted = 0  ORDER BY create_time desc limit #{limit}")
    List<HouseLeaseVO> selectLeaseLatest(int limit);

    /**
     * @author: Pipi
     * @description: 分页查询推送号的推送消息
     * @param acctId:
     * @return: java.util.List<com.jsy.community.entity.PushInformEntity>
     * @date: 2021/11/9 15:07
     **/
    List<PushInformEntity> selectInformPage(@Param("acctId") Long acctId, @Param("startNum") Long startNum, @Param("pageSize") Long pageSize);
}
