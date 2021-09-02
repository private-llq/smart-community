package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author jsy
 * @since 2020-12-17
 */
public interface ShopLeaseMapper extends BaseMapper<ShopLeaseEntity> {

    void insertMiddle(@Param("id") Long id, @Param("tagIds") Long[] tagIds);

    @Select("select house_const_id from t_shop_const where shop_lease_id = #{shopId}")
    Long[] selectTags(Long shopId);

    void deleteTags(@Param("shopId") Long shopId);

    /**
     * @return java.lang.Long[]
     * @Author lihao
     * @Description 根据店铺id查询房源类型标签
     * @Date 2020/12/17 18:42
     * @Param [shopId]
     **/
    Long[] selectTypeTags(Long shopId);

    Long[] selectBusinessTags(Long shopId);  // 注意：若使用了mybatisPlugin会提示结果类型不匹配


    List<Long> selectMiddle(List<Long> advantage);

    /**
     * @return java.util.List<com.jsy.community.entity.shop.ShopLeaseEntity>
     * @Author lihao
     * @Description 条件分页查询商铺
     * @Date 2021/4/20 17:25
     * @Param [baseQO, info]
     **/
    List<ShopLeaseEntity> getShopByCondition(@Param("qo") BaseQO<HouseLeaseQO> baseQO, @Param("info") Page<ShopLeaseEntity> info);

    /**
     * @Description: 查询一条商铺详情
     * @author: Hu
     * @since: 2021/7/8 10:48
     * @Param:
     * @return:
     */
    ShopLeaseEntity selectByShopId(Long shopId);
}
