package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.ProprietorVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 业主 Mapper 接口
 * @author YuLF
 * @since 2020-11-25
 */
public interface ProprietorMapper extends BaseMapper<UserEntity> {

    /**
     * [物业]通过分页参数查询 业主信息
     * @param queryParam    查询参数
     * @return              返回查询的业主信息
     */
    List<ProprietorVO> query(BaseQO<ProprietorQO> queryParam);

    /**
     * [物业]更新业主信息
     * @param proprietorQO  待更新参数实体
     * @return              返回sql影响行数
     */
    int update(ProprietorQO proprietorQO);

    /**
     * 通过社区id 拿到当前社区 所有未被登记的房屋信息
     * @param communityId   社区id
     * @return              返回房屋信息列表
     */
    List<HouseEntity> getHouseListByCommunityId(@Param("communityId") Long communityId, @Param("houseLevelMode") Integer houseLevelMode);

    /**
     * 通过社区id拿到当前社区的 层级结构 房屋层级模式：1.楼栋单元 2.单元楼栋 3.单楼栋 4.单单元
     * @author YuLF
     * @since  2020/12/24 9:19
     */
    @Select("select house_level_mode from t_community where id = #{communityId}")
    Integer queryHouseLevelModeById(@Param("communityId") Long communityId);

    /**
     * [excel]批量注册用户t_user_auth
     * @author YuLF
     * @since  2020/12/24 15:21
     */
    void registerBatch(@Param("userEntityList") List<UserEntity> userEntityList);


    /**
     * [excel]批量登记用户t_user
     * @param userEntityList        用户登记信息列表
     */
    void insertUserBatch(@Param("userEntityList") List<UserEntity> userEntityList);

    /**
     * [excel]批量绑定房屋
     * @author YuLF
     * @since  2020/12/24 16:27
     */
    void registerHouseBatch(@Param("userEntityList") List<UserEntity> userEntityList, @Param("communityId") Long communityId);


    /**
     * 通过当前社区id查出的当前社区所有已登记的房屋
     * @author YuLF
     * @since  2020/12/25 11:10
     * @Param
     * @return          返回当前社区已经被登记的所有房屋信息
     */
    List<HouseVo> queryHouseByCommunityId(@Param("communityId") long communityId, @Param("houseLevelMode") Integer houseLevelMode);



    /**
     * 通过社区ID 在t_user_house 拿到所有已审核的房屋id和uid 主要用于 对excel业主家属录入信息 进行核实
     * @author YuLF
     * @since  2020/12/25 15:04
     * @Param
     */
    @Select("select uid,house_id  from t_user_house where community_id = #{communityId} and deleted = 0 and check_status = 1")
    List<UserHouseEntity> queryUserHouseByCommunityId(@Param("communityId") long communityId);


    /**
     * [excel]批量导入业主家属信息
     * @author YuLF
     * @since  2020/12/25 16:41
     * @Param
     */
    Integer saveUserMemberBatch(@Param("userEntityList") List<UserEntity> userEntityList, @Param("communityId") Long communityId);
}
