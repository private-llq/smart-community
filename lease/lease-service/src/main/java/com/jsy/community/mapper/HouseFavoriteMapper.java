package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseFavoriteEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseFavoriteQO;
import com.jsy.community.vo.lease.HouseFavoriteVO;
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
     * @author YuLF
     * @since  2020/12/30 11:29
     * @Param  qo           参数对象
     */
    List<HouseFavoriteVO> shopFavorite(BaseQO<HouseFavoriteQO> qo);

    /**
     * 获取t_shop_img 的一张商铺房屋图片地址
     * @param id       商铺id
     */
    @Select("select i.img_url as houseImage from t_shop_img as i JOIN t_shop_lease as l on l.id = i.shop_id where l.id = #{id} limit 1")
    String getShopImage(@Param("id") long id);

    /**
     * 获取t_lease_image 的一张房屋图片地址
     * @param houseId       房屋id
     */
    @Select("select i.img_url as houseImage from t_house_image as i JOIN t_house_lease as l on l.id = i.hid where l.deleted = 0 and l.id = #{houseId} limit 1")
    String getHouseLeaseImage(@Param("houseId") long houseId);


    /**
     * 删除收藏数据
     */
    @Delete("delete from t_house_favorite where id = #{id} and uid = #{uid}")
    Integer deleteById(@Param("id") Long id, @Param("uid") String userId);

    /**
     * 是否存在已发布商铺
     */
    @Select("select count(1) from  t_shop_lease where id = #{favoriteId} and deleted = 0")
    Integer existShop(Long favoriteId);

    /**
     * 是否存在已发布出租房屋
     */
    @Select("select count(*) from t_house_lease where deleted = 0  and id = #{favoriteId} ")
    Integer existHouse(Long favoriteId);
}
