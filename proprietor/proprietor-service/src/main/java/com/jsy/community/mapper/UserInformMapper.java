package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.entity.sys.SysInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.vo.CommunityVO;
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
     * @Param  sysInform            标识该消息是社区消息还是实体消息
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
    CommunityVO queryCommunityInform(@Param("communityId") Long communityId);

    /**
     *  用户系统消息详情数据返回方法
     * @author YuLF
     * @since  2020/12/21 14:55
     */
    @Select("SELECT id,title,sub_title,content,enabled,browse_count,create_time FROM t_sys_inform WHERE id = #{informId}")
    SysInformEntity querySysInformById(@Param("informId") Long informId);


    /**
     * 系统消息浏览次数增量+1
     * @param informId      系统消息id
     */
    @Update("update t_sys_inform set browse_count = browse_count+1 where id = #{informId}")
    void incrementSysInformBrowse(@Param("informId") Long informId);

    /**
     * 查询该用户已读未读消息表 中 系统已读消息 计数
     * @param userId        用户id
     * @return              返回用户已读系统消息的次数
     */
    @Select("select count(*) from t_user_inform where uid = #{uid} and sys_inform = 1")
    Integer querySysReadInform(@Param("uid") String userId);

    //查询最新时间第一条系统消息的标题与时间
    CommunityVO querySysInform();

    //系统消息总计数
    @Select("select count(*) from t_sys_inform")
    Integer querySysInformCount();

    @Select("select count(*) from t_sys_inform where id = #{informId}")
    Integer sysInformExist(@Param("informId") Long informId);

    //按分页条件查出系统消息数据
    @Select("select id,title,sub_title,create_time from t_sys_inform where enabled = 1 ORDER BY create_time DESC limit #{page},#{size}")
    List<SysInformVO> selectSysInformPage(BaseQO<SysInformVO> baseQO);

    //按用户id和时间查出用户已读的系统消息id
    @Select("select inform_id from t_user_inform where uid = #{uid} and create_time > #{lastCreateTime}")
    List<Long> selectUserReadSysInform(@Param("uid") String userId, @Param("lastCreateTime") String lastCreateTime);
}
