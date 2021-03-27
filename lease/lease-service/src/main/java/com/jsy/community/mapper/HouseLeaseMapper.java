package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.vo.lease.HouseImageVo;
import com.jsy.community.vo.lease.HouseLeaseSimpleVO;
import com.jsy.community.vo.lease.HouseLeaseVO;
import com.jsy.community.vo.HouseVo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 房屋租售 Mapper 接口
 * @author YuLF
 * @since 2020-12-10
 */
public interface HouseLeaseMapper extends BaseMapper<HouseLeaseEntity> {





    /**
     * 插入房屋图片至中间库
     * @param houseLeaseQo  参数对象
     */
    void insertHouseImages(@Param("houseLeaseQO") HouseLeaseQO houseLeaseQo);



    /**
     * 根据 rowGuid 删除t_house_lease 表中的数据
     * @param id        数据唯一标识业务主键 暂定
     * @param uid       用户id
     * @return          返回影响行数
     */
    int delHouseLeaseInfo(@Param("id") Long id, @Param("uid")String uid);


    /**
     * 根据参数对象条件查询 出租房屋数据
     * @param baseQo            查询参数对象
     * @return                  返回数据集合
     */
    List<HouseLeaseVO> queryHouseLeaseByList(BaseQO<HouseLeaseQO> baseQo);




    /**
     * 通过imgId 和 数据标识行ID 查出 该数据最早插入的一张图片
     * @param houseImageId          图片Id
     * @param rowGuid               房屋数据行唯一标识
     * @return                      返回图片
     */
    @Select("select img_url from t_house_image where hid = #{hid} and field_id = #{image_id} ORDER BY create_time LIMIT 1")
    List<String> queryHouseImgById(@Param("image_id") Long houseImageId, @Param("hid") Long rowGuid);



    /**
     * 用家具ID 查出所有 家具名称
     * @param furnitureId       家具ID列表
     * @param type              常量类型
     * @return                  返回名称集合
     */
    List<String> queryHouseConstNameByFurnitureId(List<Long> furnitureId, long type);


    /**
     * 根据id查询房屋详情单条数据
     * @param houseId       房屋id
     * @return              返回这条数据的详情
     */
    HouseLeaseVO queryHouseLeaseOne(@Param("houseId") Long houseId);

    /**
     *@Author: Pipi
     *@Description: 查询房屋出租数据单条简略详情
     *@param: houseId: 出租房屋主键
     *@Return: com.jsy.community.vo.lease.HouseLeaseSimpleVO
     *@Date: 2021/3/27 16:47
     **/
    HouseLeaseSimpleVO queryHouseLeaseSimpleDetail(@Param("houseId") Long houseId);


    /**
     * 按图片id查出所有图片
     * @param houseImageId      图片id
     * @return                  返回List数据
     */
    @Select("select img_url from t_house_image where field_id = #{houseImageId}")
    List<String> queryHouseAllImgById(@Param("houseImageId") Long houseImageId);


    /**
     * 【整租、合租、单间】参数对象属性更新房屋出租数据
     * @param houseLeaseQo          参数对象
     * @return                      返回更新影响行数
     */
    Integer updateHouseLease(HouseLeaseQO houseLeaseQo);


    /**
     * 按房屋出租id删除所属图片
     * @param id        房屋数据id
     */
    @Delete("delete from t_house_image where hid = #{id}")
    void deleteImageById(@Param("id") Long id);


    /**
     * 按id保存图片数组地址
     * @param images                图片地址
     * @param houseImageId          图片表示id
     * @param hid                   房屋id
     */
    void saveHouseLeaseImageById(@Param("images") String[] images , @Param("houseImageId")Long houseImageId, @Param("hid")Long hid);


    /**
     * 通过用户id社区id房屋id验证用户是否存在此处房产
     * @param userId                用户id
     * @param houseCommunityId      社区id
     * @param houseId               房屋id
     * @return                      返回是否存在结果
     */
    @Select("select count(*) from t_user_house as uh LEFT JOIN t_house as h on uh.house_id = h.id where uh.uid = #{userId} and uh.check_status = 1 and uh.community_id = #{houseCommunityId} and uh.deleted = 0 and h.id = #{houseId} and h.type =4 and h.deleted = 0")
    Integer isExistUserHouse(@Param("userId") String userId, @Param("houseCommunityId") Long houseCommunityId, @Param("houseId") Long houseId);


    /**
     * 按用户id和 社区id查询 房主在当前社区出租的房源
     * @param qo                包含用户id
     * @return                  返回业主拥有的房产
     */
    List<HouseLeaseVO> ownerLeaseHouse(BaseQO<HouseLeaseQO> qo);


    /**
     * 根据用户id 和社区id 查询用户在这个社区的可发布房源
     * @param userId            用户id
     * @param communityId       社区id
     * @return                  返回List数据 如果有多条
     */
    List<HouseVo> ownerHouse(@Param("uid") String userId, @Param("communityId") Long communityId);


    /**
     * [为了后续方便修改、使用单表匹配搜索] 去缓存取标签的方式
     * 按小区名或房屋出租标题或房屋地址模糊搜索匹配接口
     * @param qo            请求参数
     * @return              返回搜索到的列表
     */
    List<HouseLeaseVO> searchLeaseHouseByText(BaseQO<HouseLeaseQO> qo);


    /**
     * 按房屋租金搜索匹配接口
     * @param qo            请求参数
     * @return              返回搜索到的列表
     */
    List<HouseLeaseVO> searchLeaseHouseByPrice(BaseQO<HouseLeaseQO> qo);

    /**
     * 根据房屋id和uid在t_house_favorite查出该数据是否被收藏
     * @param houseId       房屋id
     * @param uid           uid
     * @return              影响行数
     */
    @Select("select count(*) from t_house_favorite where favorite_id =#{houseId}  and uid = #{uid}")
    Integer isFavorite(@Param("houseId") Long houseId, @Param("uid") String uid);


    /**
     * 验证houseId是否已经发布
     * @param houseId   房屋id
     * @return          返回是否成功
     */
    @Select("select count(*) from t_house_lease where deleted = 0 and house_id = #{houseId}")
    Integer alreadyPublish(@Param("houseId") Long houseId);


    /**
     * 按用户id获取所有小区名称
     * @param uid       用户id
     * @param cityId    城市id
     * @return          返回小区名称
     */
    List<CommunityEntity> allCommunity(Long cityId, String uid);

    /**
     * 按id查询房屋详情数据
     * @param houseId       房屋id
     * @param uid           用户id
     * @return              返回这条数据的详情
     */
    HouseLeaseVO editDetails(Long houseId, String uid);

    /**
     * 整租插入房源数据
     * @param houseLeaseQo      参数对象
     * @return                  返回影响行数
     */
    Integer addWholeLeaseHouse(HouseLeaseQO houseLeaseQo);
    /**
     * 单间新增房源
     * @param qo                请求参数
     * @return                  返回新增sql影响行数
     */
    Integer addSingleLeaseHouse(HouseLeaseQO qo);

    /**
     * 合租新增房源
     * @param qo                请求参数
     * @return                  返回新增sql影响行数
     */
    Integer addCombineLeaseHouse(HouseLeaseQO qo);

    /**
     * 通过社区id 查出 社区所在区域id
     * @param houseCommunityId  社区id
     * @return                  社区所在城市区域id
     */
    @Select("select area_id from t_community where id = #{houseCommunityId}")
    Long selectAreaIdByCommunityId(Long houseCommunityId);

    /**
     * 按用户id 和 房屋id 查询已发布 房源数量
     * @param userId        用户id
     * @param houseId       用户id
     * @return              返回已发布数量
     */
    @Select("select count(*) from t_house_lease where uid = #{userId} and house_id = #{houseId}")
    Integer getPublishLease(String userId, Long houseId);


    /**
     * 根据社区id拿到社区名称 和 房屋具体地址
     * @param houseCommunityId      社区id
     * @param houseId               房屋id
     * @return                      返回社区名称和房屋名称
     */
    Map<String, String> getUserAddrById(Long houseCommunityId, Long houseId );


    /**
     * 根据图片id批量查询图片
     * @param voImageIds    图片id集合
     * @return              返回图片关系对象
     */
    List<HouseImageVo> selectBatchImage(List<Long> voImageIds);
}
