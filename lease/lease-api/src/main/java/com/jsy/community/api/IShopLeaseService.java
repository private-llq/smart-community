package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.qo.shop.ShopQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.shop.IndexShopVO;
import com.jsy.community.vo.shop.ShopDetailsVO;
import com.jsy.community.vo.shop.UserShopLeaseVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-12-17
 */
public interface IShopLeaseService extends IService<ShopLeaseEntity> {

    /**
     * @return void
     * @Author lihao
     * @Description 发布商铺租赁信息
     * @Date 2020/12/17 10:49
     * @Param []
     **/
    void addShop(ShopQO shop);

    /**
     * @return com.jsy.community.vo.shop.ShopLeaseVO
     * @Author lihao
     * @Description 根据店铺id查询发布的店铺详情
     * @Date 2020/12/17 17:12
     * @Param [shopId]
     **/
    Map<String, Object> getShop(Long shopId, String uid);

    /**
     * @return void
     * @Author lihao
     * @Description 修改店铺
     * @Date 2020/12/17 17:12
     * @Param [shop]
     **/
    void updateShop(ShopQO shop);

    /**
     * @return void
     * @Author lihao
     * @Description 下架店铺
     * @Date 2020/12/17 18:20
     * @Param []
     **/
    void cancelShop(String userId, Long shopId);

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Author lihao
     * @Description 查询业主发布的房源列表
     * @Date 2020/12/17 20:11
     * @Param []
     **/
    List<Map<String, Object>> listShop(String userId);

    /**
     * @return com.jsy.community.utils.PageInfo<com.jsy.community.entity.shop.ShopLeaseEntity>
     * @Author lihao
     * @Description 根据查询条件查询商铺列表
     * @Date 2020/12/21 14:13
     * @Param [baseQO]
     **/
    PageInfo<IndexShopVO> getShopBySearch(BaseQO<ShopLeaseEntity> baseQO, String query, Integer areaId);

    /**
     * @return void
     * @Author lihao
     * @Description 测试分布式事物
     * @Date 2020/12/23 15:22
     * @Param []
     **/
    void testTransaction();


    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Author lihao
     * @Description 更多
     * @Date 2021/1/13 9:44
     * @Param []
     **/
    Map<String, Object> moreOption();

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Author lihao
     * @Description 查询商铺类型和行业[发布的时候添加]
     * @Date 2021/1/26 17:10
     * @Param []
     **/
    Map<String, Object> getPublishTags();

    /**
     * @return java.util.List<com.jsy.community.entity.CommunityEntity>
     * @Author lihao
     * @Description 根据区域id查询小区列表
     * @Date 2021/1/26 17:07
     * @Param [areaId]
     **/
    List<CommunityEntity> getCommunity(Long areaId);

    /**
     * @return com.jsy.community.vo.shop.ShopDetailsVO
     * @Author lihao
     * @Description
     * @Date 2021/3/23 17:38
     * @Param [shopId]
     **/
    ShopDetailsVO getShopForUpdate(Long shopId);

    /**
     * @return java.util.List<com.jsy.community.entity.shop.ShopLeaseEntity>
     * @Author lihao
     * @Description 查询业主发布的房源列表
     * @Date 2021/3/25 10:08
     * @Param [userId]
     **/
    List<UserShopLeaseVO> listUserShop(String userId);


    /**
     * @return com.jsy.community.utils.PageInfo<com.jsy.community.vo.shop.IndexShopVO>
     * @Author lihao
     * @Description 根据筛选条件查询商铺列表
     * @Date 2021/5/25 14:58
     * @Param [baseQO]
     **/
    PageInfo<IndexShopVO> getShopByCondition(BaseQO<HouseLeaseQO> baseQO);
    
    /**
     * @Description: 查询communityIds下所有商铺数量
     * @author: DKS
     * @since: 2021/9/3 9:31
     * @Param: communityIdList
     * @return: Integer
     */
    Integer selectAllShopByCommunityIds(List<String> communityIdList);
}
