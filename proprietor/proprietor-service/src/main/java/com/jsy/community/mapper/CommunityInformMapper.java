package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.qo.proprietor.CommunityInformQO;
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
public interface CommunityInformMapper extends BaseMapper<CommunityInformEntity> {

    Integer updateCommunityInform(CommunityInformQO communityInformQO);


    /**
     * 社区主页 当前轮播消息 查询最近的  initialInformCount 条数量
     * @param initialInformCount     初始轮播消息条数
     * @return                       返回消息列表
     */
    List<CommunityInformEntity> rotationCommunityInform(Integer initialInformCount, Long communityId);

    /**
     * 增加一次该消息的浏览量
     * @param communityId       社区ID
     * @param informId          消息ID
     */
    @Update("update t_community_inform set browse_count = browse_count+1 where community_id = #{communityId} and id = #{informId}")
    void updateCommunityInformBrowseCount(@Param("communityId") Long communityId, @Param("informId") Long informId);


    /**
     * 验证社区消息是否存在
     * @author YuLF
     * @since  2020/12/21 17:02
     */
    @Select("select count(*) from t_community_inform where community_id = #{communityId} and id = #{informId}")
    boolean informExist(@Param("communityId") Long communityId, @Param("informId") Long informId);
}
