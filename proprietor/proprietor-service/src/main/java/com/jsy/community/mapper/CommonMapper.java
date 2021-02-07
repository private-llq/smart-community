package com.jsy.community.mapper;


import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.entity.RegionEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 公共 Mapper 接口
 * @author YuLF
 * @since 2020-11-10
 */
public interface CommonMapper {

    List<Map<String, Object>> getAllCommunityFormCityId(Long id, Integer page, Integer pageSize);

    List<Map<String, Object>> getBuildingOrUnitByCommunityId(Long id, Integer houseLevelMode);

    List<Map<String, Object>> getBuildingOrUnitById(Long id, Integer houseLevelMode);

    List<Map<String, Object>> getAllDoorFormFloor(Long id);

    List<Map<String, Object>> getFloorByBuildingOrUnitId(Long id, Integer houseLevelMode);

    //以下为全量导入elasticSearch的数据
    /**
     * flag和 RecordFlag.java 中的标记 一致
     * 所有商铺数据
     * @return  返回所有 商铺标题、id
     */
    @Select("select id,title,'LEASE_SHOP' as flag from t_shop_lease where deleted = 0")
    List<FullTextSearchEntity> getAllShop();

    /**
     * flag和 RecordFlag.java 中的标记 一致
     * 所有房屋租赁数据
     * @return  返回所有 房屋标题、id
     */
    @Select("select id,house_title as title,'LEASE_HOUSE' as flag from t_house_lease where deleted = 0 ")
    List<FullTextSearchEntity> getAllHouseLease();

    /**
     * flag和 RecordFlag.java 中的标记 一致
     * 所有公共社区消息数据
     * @return  返回所有 消息标题、id、 社区id
     */
    @Select("select id,push_title as title,acct_id,'INFORM' as flag from t_acct_push_inform where deleted = 0 and push_target = 0")
    List<FullTextSearchEntity> getAllInform();


    /**
     * flag和 RecordFlag.java 中的标记 一致
     * 所有社区趣事数据
     * @return  返回所有 社区趣事标题、id、
     */
    @Select("select id,title_name as title,small_image_url as picture,'FUN' as flag from t_community_fun where deleted = 0")
    List<FullTextSearchEntity> getAllFun();

    /**
     * 查询t_lease_house单张图片
     * @param id    t_lease_house 数据id
     * @return      返回图片URL 作为列表缩略图
     */
    @Select("select img_url from t_house_image where hid = #{id} limit 1")
    String getLeaseHousePicture(Long id);


    /**
     * 查询单张图片
     * @param id    t_shop_lease 数据id
     * @return      返回图片URL 作为列表缩略图
     */
    @Select("select img_url from t_shop_img where shop_id = #{id} limit 1")
    String getLeaseShopPicture(Long id);

    /**
     * 查询单张图片
     * @param id    t_community_fun 数据id
     * @return      返回图片URL 作为列表缩略图
     */
    @Select("select small_image_url   from t_community_fun where id = #{id} limit 1")
    String getFunPicture(Long id);


    /**
     * 查询单张图片
     * @param id    t_acct_push_inform 数据id
     * @return      返回图片URL 作为列表缩略图
     */
    @Select("select acct_avatar from t_acct_push_inform where id = #{id} limit 1")
    String getInformPicture(Long id);
}
