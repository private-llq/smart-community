package com.jsy.community.mapper;


import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.entity.RegionEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 公共 Mapper 接口
 * @author YuLF
 * @since 2020-11-10
 */
public interface CommonMapper {

    /**
     * 通过城市id 查询下面的 社区id
     * @param id        城市id
     * @param page      当前页
     * @param pageSize  每页显示条数
     * @return          返回 城市下面的 id + 社区名称
     */
    List<Map<String, Object>> getAllCommunityFormCityId(@Param("id") Long id, @Param("page") Integer page, @Param("pageSize") Integer pageSize);

    /**
     * 根据社区id 查询 所有的楼栋或单元
     * @param communityId       社区id
     * @param type              房屋类型 1楼栋 2单元
     * @return                  返回所有子集内容
     */
    List<Map<String, Object>> getAllBuild(Long communityId, Integer type );


    /**
     * 通过楼栋id 查询 下面的 单元
     * @param id    楼栋id
     * @return      返回 id + 单元
     */
    List<Map<String, Object>> getUnitByBuildingId(Long id);

    /**
     * 通过 楼栋id或 单元id 查询下面的 楼层
     * @param id        可能是楼栋id或单元id
     * @return          返回楼层 文本
     */
    List<Map<String, Object>> getFloorByBuildingId(Long id);

    /**
     * 通过单元id 查询楼层
     * @param id        单元id
     * @param page      当前页
     * @param pageSize  每页显示条数
     * @return          返回 楼层文本
     */
    List<Map<String, Object>> getFloorByUnitId(Long id, Integer page, Integer pageSize);

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
    @Select("select id,push_title as title,acct_id,'INFORM' as flag from t_push_inform where deleted = 0 and push_target = 0")
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
     * @param id    t_push_inform 数据id
     * @return      返回图片URL 作为列表缩略图
     */
    @Select("select acct_avatar from t_push_inform where id = #{id} limit 1")
    String getInformPicture(Long id);


    /**
     * 楼栋id或单元id  和 楼层文本 查下级的房屋
     * @param id        楼栋或单元id
     * @param floor     楼层文本
     * @return          返回房屋id + 房屋文本
     */
    List<Map<String, Object>> getHouseByFloor(Long id, String floor);
    
    /**
    * @Description: 查询热门城市
     * @Param: []
     * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
     * @Author: chq459799974
     * @Date: 2021/6/9
    **/
    @Select("select id,name from t_hot_city")
    List<RegionEntity> queryHotCity();
}
