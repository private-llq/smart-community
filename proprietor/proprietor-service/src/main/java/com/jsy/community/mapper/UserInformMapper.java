package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.vo.CommunityVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserInformMapper extends BaseMapper<UserInformEntity> {

    /**
     *  查询用户在当前社区的已读消息 ids
     * @author YuLF
     * @since  2020/12/14 9:35
     * @Param
     */
    @Select("select inform_id from t_user_inform where community_id = #{communityId} and uid = #{uid} and inform_status = 1")
    List<Long> queryUserReadCommunityInform(@Param("communityId") Long communityId, @Param("uid") String uid);

    /**
     *  插入一条信息标识用户消息已读
     * @author YuLF
     * @since  2020/12/14 9:35
     * @Param
     */
    void setInformReadByUser(UserInformEntity userInformEntity);

    /**
     * 删除社区消息时 同事物理删除用户已读信息
     * @param id  社区消息ID
     */
    @Delete("delete from t_user_inform where inform_id = #{id}")
    void delUserReadInform(@Param("id") Long id);


    /**
     * @author YuLF
     * @since  2020/12/14 18:07
     * @param userId        用户ID
     * @param page          当前页
     * @param size          每次显示的条数
     * @return              返回总消息未读列表数据
     */
    List<Long> queryUserAllCommunityId(String userId, Long page, Long size);


    /**
     * 根据用户id和社区id查出用户在当前社区已读信息数量
     * @param userId            用户id
     * @param communityId       社区id
     * @author YuLF
     * @since  2020/12/14 18:0
     * @return                  返回count
     */
    @Select("select count(*) from t_user_inform where community_id = #{communityId} and uid = #{userId}")
    Integer queryReadInformById(@Param("userId") String userId, @Param("communityId") Long communityId);


    /**
     * 查出当前社区 消息总计数
     * @param communityId   社区ID
     * @author YuLF
     * @since  2020/12/14 18:0
     * @return              返回消息count
     */
    @Select("select count(*) from t_community_inform where community_id = #{communityId}")
    Integer queryCommunityInformTotalCount(@Param("communityId") Long communityId);

    /**
     * 查询社区信息
     * @author YuLF
     * @since  2020/12/15 9:14
     * @Param   communityId
     * @return  社区相关信息
     */
    CommunityVO queryCommunityInform(Long communityId);
}
