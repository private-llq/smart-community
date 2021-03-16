package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.ProprietorVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 业主 Mapper 接口
 * @author YuLF
 * @since 2020-11-25
 */
public interface ProprietorMapper extends BaseMapper<ProprietorEntity> {

    /**
     * [物业]通过分页参数查询 业主信息
     * @param queryParam    查询参数
     * @return              返回查询的业主信息
     */
    List<ProprietorVO> query(BaseQO<ProprietorQO> queryParam);


    /**
     * 通过社区id 拿到当前社区 所有未被登记的房屋信息
     * @param communityId           社区id
     * @param houseLevelMode        社区层级模式
     * @return                      返回房屋信息列表
     */
    List<HouseEntity> getHouseListByCommunityId(@Param("communityId") Long communityId, @Param("houseLevelMode") Integer houseLevelMode);

    /**
     * 通过社区id拿到当前社区的 层级结构 房屋层级模式：1.楼栋单元 2.单元楼栋 3.单楼栋 4.单单元
     * @param communityId       社区id
     * @author YuLF
     * @since  2020/12/24 9:19
     * @return                  返回影响行数
     */
    @Select("select house_level_mode from t_community where id = #{communityId} and deleted = 0")
    Integer queryHouseLevelModeById(@Param("communityId") Long communityId);

    /**
     * [excel]批量注册用户t_user_auth
     * @param userEntityList        用户信息实体
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
     * @param userEntityList    用户信息实体
     * @param communityId       社区id
     * @author YuLF
     * @since  2020/12/24 16:27
     */
    void registerHouseBatch(@Param("userEntityList") List<UserEntity> userEntityList, @Param("communityId") Long communityId);


    /**
     * 通过当前社区id查出的当前社区所有已登记的房屋
     * @param communityId       社区id
     * @param houseLevelMode    社区层级模式
     * @author YuLF
     * @since  2020/12/25 11:10
     * @return          返回当前社区已经被登记的所有房屋信息
     */
    List<HouseVo> queryHouseByCommunityId(@Param("communityId") long communityId, @Param("houseLevelMode") Integer houseLevelMode);



    /**
     * 通过社区ID 在t_user_house 拿到所有已审核的房屋id和uid 主要用于 对excel业主家属录入信息 进行核实
     * @param communityId       社区id
     * @author YuLF
     * @since  2020/12/25 15:04
     * @return                  返回房屋id和用户uid的查询结果
     */
    @Select("select uid,house_id  from t_user_house where community_id = #{communityId} and deleted = 0 and check_status = 1")
    List<UserHouseEntity> queryUserHouseByCommunityId(@Param("communityId") long communityId);


    /**
     * [excel]批量导入业主家属信息
     * @param userEntityList    用户信息实体
     * @param communityId       社区id
     * @author YuLF
     * @since  2020/12/25 16:41
     * @return                  返回影响行数
     */
    Integer saveUserMemberBatch(@Param("userEntityList") List<UserEntity> userEntityList, @Param("communityId") Long communityId);


    /**
     * 检查已认证房屋是否存在
     * @param houseId       房屋id
     * @return              返回查询行数
     */
    @Select("select count(house_id) from t_user_house where house_id = #{houseId} and check_status = 1 and deleted = 0 ")
    Integer checkHouse(Long houseId);


    /**
     * 通过证件号码查询 用户uid
     * @param idCard     用户证件号码
     * @return           返回uid
     */
    @Select("select uid from t_user where id_card = #{idCard} and deleted = 0")
    String queryUserByIdCard(String idCard);


    /**
     * 保存用户信息
     * @param qo    用户信息参数
     */
    void saveUser(ProprietorQO qo);


    /**
     * 插入管理员 对用户的操作日志
     * @param id                数据id
     * @param operationPerson   更新人
     * @param operationTime     更新时间
     * @param pid               业主id
     * @param operationType     操作类型 1创建 2更新
     */
    void insertOperationLog( Long id,  String operationPerson, String operationTime, Long pid , Integer operationType);


    /**
     * 通过管理员uid查出 管理员姓名
     * @param adminUid  管理员uid
     * @return          返回管理员姓名
     */
    String queryAdminNameByUid(String adminUid);


    /**
     * 通过房屋id 判断是否存在 房屋
     * @param houseId       房屋id
     * @param communityId   社区id
     * @return              返回结果行数
     */
    Integer existHouse(Long communityId, Long houseId);

    /**
     * 根据id 拿到用户uid
     * @param id     数据id
     * @return       返回uid
     */
    @Select("select uid from t_user where id = #{id} and deleted = 0")
    String selectUidById(Long id);


    /**
     * [物业]解绑业主房屋信息
     * @param id            id
     * @return              返回影响行数
     */
    @Update("update t_proprietor set deleted = 1 where id = #{id} and deleted = 0")
    Integer unbindHouse(Long id);

    /**
     * 根据uid 查询 用户 当前记录的创建时间
     * @param id                用户id
     * @param operationType     操作类型 1创建 2更新
     * @return                  返回 创建人 / 时间
     */
    @Select("select CONCAT(operation_person,' / ',operation_time) from t_admin_proprietor_log where pid = #{id} and operation_type = #{operationType} ORDER BY create_time desc limit 1")
    String queryOperationDate(Long id, Integer operationType);



}
