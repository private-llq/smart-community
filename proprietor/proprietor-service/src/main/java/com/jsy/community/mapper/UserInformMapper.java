package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.entity.sys.SysInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.vo.InformListVO;
import com.jsy.community.vo.sys.SysInformVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserInformMapper extends BaseMapper<UserInformEntity> {

    /**
     *  查询用户在当前社区的已读消息 ids
     * @author YuLF
     * @since  2020/12/14 9:35
     * @Param
     */
    @Select("select inform_id from t_user_inform where community_id = #{communityId} and uid = #{uid} and inform_status = 1 and sys_inform = 0 and create_time > #{lastCreateTime}")
    List<Long> queryUserReadCommunityInform(@Param("communityId") Long communityId, @Param("uid") String uid, @Param("lastCreateTime") String lastCreateTime);

    /**
     *  t_user_inform插入一条信息标识用户 该社区消息已读
     * @author YuLF
     * @since  2020/12/14 9:35
     * @Param  userInformEntity     消息实体和用户信息
     */
    void setInformReadByUser(UserInformEntity userInformEntity);




    /**
     * @author YuLF
     * @since  2020/12/14 18:07
     * @param userId        用户ID
     * @return              返回总消息未读列表数据
     */
    List<Long> queryUserAllCommunityId(String userId);


    /**
     * 根据用户id和社区id查出用户在当前社区已读信息数量
     * @param userId            用户id
     * @param acctId            推送id
     * @author YuLF
     * @since  2020/12/14 18:0
     * @return                  返回count
     */
    @Select("select count(*) from t_user_inform where acct_id = #{acctId} and uid = #{userId}")
    Integer queryReadInformById(@Param("userId") String userId, @Param("acctId") Long acctId);


    /**
     * 查出当前社区 消息总计数
     * @param acctId   社区ID
     * @author YuLF
     * @since  2020/12/14 18:0
     * @return              返回消息count
     */
    @Select("select count(*) from t_acct_push_inform where acct_id = #{acctId}")
    Integer queryPushInformTotalCount(@Param("acctId") Long acctId);





    /**
     * 通过 user id 查出所有推送 至所有社区 但是没有被该用户屏蔽的 推送账号ids
     * @author YuLF
     * @since  2021/1/7 15:21
     */
    List<Long> queryPushAcctIds(String userId);

    /**
     * 根据推送消息号id 查询 这一条推送号 推送的最新的一条消息
     * @param id       推送号id
     */
    InformListVO queryLatestInform(Long id);
}
