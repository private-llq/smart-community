package com.jsy.lease.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.shop.ShopLeaseVo;

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
	void addShop(ShopLeaseVo shop);
	
	/**
	 * @return com.jsy.community.vo.shop.ShopLeaseVo
	 * @Author lihao
	 * @Description 根据店铺id查询发布的店铺详情
	 * @Date 2020/12/17 17:12
	 * @Param [shopId]
	 **/
	Map<String, Object> getShop(Long shopId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 修改店铺
	 * @Date 2020/12/17 17:12
	 * @Param [shop]
	 **/
	void updateShop(ShopLeaseVo shop, Long shopId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 下架店铺
	 * @Date 2020/12/17 18:20
	 * @Param []
	 **/
	void cancelShop(String userId, Long shopId, Long communityId, Long houseId);
	
	/**
	 * @return java.util.Map<java.lang.String, java.lang.Object>
	 * @Author lihao
	 * @Description 查询业主发布的房源列表
	 * @Date 2020/12/17 20:11
	 * @Param []
	 **/
	List<Map<String, Object>> listShop(String userId);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.shop.ShopLeaseEntity>
	 * @Author lihao
	 * @Description 根据条件查询商铺列表
	 * @Date 2020/12/21 10:09
	 * @Param [shopQO]
	 **/
	List<ShopLeaseEntity> getShopByCondition(BaseQO<HouseLeaseQO> baseQO);
	
	/**
	 * @return com.jsy.community.utils.PageInfo<com.jsy.community.entity.shop.ShopLeaseEntity>
	 * @Author lihao
	 * @Description 根据查询条件查询商铺列表
	 * @Date 2020/12/21 14:13
	 * @Param [baseQO]
	 **/
	PageInfo<ShopLeaseEntity> getShopBySearch(BaseQO<ShopLeaseEntity> baseQO, String query, Integer areaId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 测试分布式事物
	 * @Date 2020/12/23 15:22
	 * @Param []
	 **/
	void testTransaction();
	
}
