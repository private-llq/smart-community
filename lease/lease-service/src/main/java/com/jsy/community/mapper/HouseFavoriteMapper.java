package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseFavoriteEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseFavoriteQO;
import com.jsy.community.vo.lease.HouseFavoriteVO;
import com.jsy.community.vo.lease.HouseImageVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房屋租售 Mapper 接口
 * @author YuLF
 * @since 2020-12-29
 */
public interface HouseFavoriteMapper extends BaseMapper<HouseFavoriteEntity> {


    /**
     * 查询房屋收藏列表
     * @param qo            请求采纳数
     * @author YuLF
     * @since  2020/12/30 11:29
     * @return              返回收藏列表
     */
    List<HouseFavoriteVO> leaseFavorite(BaseQO<HouseFavoriteQO> qo);


    /**
     * 查询我的商铺收藏列表
     * @param qo            收藏查询的请求参数
     * @author YuLF
     * @since  2020/12/30 11:29
     * @return              返回商铺收藏列表数据
     */
    List<HouseFavoriteVO> shopFavorite(BaseQO<HouseFavoriteQO> qo);

    /**
     * 获取t_shop_img 的商铺房屋图片地址
     * @param shopIds  商铺房屋ids
     * @return         返回图片路径
     */
    List<HouseImageVo> getShopImage(List<Long> shopIds);

    /**
     * 获取t_lease_image 的房屋图片地址
     * @param houseIds       房屋id
     * @return              返回图片路径
     */
    List<HouseImageVo> getHouseLeaseImage(List<Long> houseIds);


    /**
     * 删除收藏数据
     * @param id            收藏数据id
     * @param userId        用户id
     * @return              返回影响行数
     */
    @Delete("delete from t_house_favorite where id = #{id} and uid = #{uid}")
    Integer deleteById(@Param("id") Long id, @Param("uid") String userId);

    /**
     * 是否存在已发布商铺
     * @param favoriteId    收藏数据id
     * @return              返回影响行数
     */
    @Select("select count(1) from  t_shop_lease where id = #{favoriteId} and deleted = 0")
    Integer existShop(Long favoriteId);

    /**
     * 是否存在已发布出租房屋
     * @param favoriteId    收藏数据id
     * @return              返回影响行数
     */
    @Select("select count(*) from t_house_lease where deleted = 0  and id = #{favoriteId} ")
    Integer existHouse(Long favoriteId);
}
