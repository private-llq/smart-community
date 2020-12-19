package com.jsy.lease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.shop.ShopImgEntity;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.shop.ShopLeaseVo;
import com.jsy.lease.api.IHouseLeaseService;
import com.jsy.lease.api.IShopLeaseService;
import com.jsy.lease.api.LeaseException;
import com.jsy.lease.mapper.ShopImgMapper;
import com.jsy.lease.mapper.ShopLeaseMapper;
import org.apache.commons.lang.ArrayUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-12-17
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class ShopLeaseServiceImpl extends ServiceImpl<ShopLeaseMapper, ShopLeaseEntity> implements IShopLeaseService {
	
	@Resource
	private ShopLeaseMapper shopLeaseMapper;
	
	@Resource
	private ShopImgMapper shopImgMapper;
	
	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private IHouseLeaseService iHouseLeaseService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Override
	@Transactional
	public void addShop(ShopLeaseVo shop) {
		//验证所属社区所属用户房屋是否存在
		if (!iHouseLeaseService.isExistUserHouse(shop.getUid(), shop.getCommunityId().intValue(), shop.getHouseId().intValue())) {
			throw new LeaseException("您并未拥有该房屋!");
		}
		
		// 存储店铺基本信息
		ShopLeaseEntity baseShop = new ShopLeaseEntity();
		BeanUtils.copyProperties(shop, baseShop);
		baseShop.setId(SnowFlake.nextId());
		shopLeaseMapper.insert(baseShop);
		
		// 存储店铺图片集合
		String[] imgPath = shop.getImgPath();
		if (imgPath != null && imgPath.length != 0) {
			List<ShopImgEntity> list = new ArrayList<>();
			for (String s : imgPath) {
				ShopImgEntity shopImgEntity = new ShopImgEntity();
				shopImgEntity.setId(SnowFlake.nextId());
				shopImgEntity.setShopId(baseShop.getId());
				shopImgEntity.setImgUrl(s);
				list.add(shopImgEntity);
			}
			shopImgMapper.insertImg(list);
			
			for (String s : imgPath) { // 垃圾图片处理
				redisTemplate.opsForSet().add("shop_img_all", s);
			}
		}
		
		// 存储标签
		Long[] typeIds = shop.getShopTypeIds();
		Long[] businessIds = shop.getShopBusinessIds();
		Long[] both = (Long[]) ArrayUtils.addAll(typeIds, businessIds);
		shopLeaseMapper.insertMiddle(baseShop.getId(), both);
	}
	
	@Override
	public ShopLeaseVo getShop(Long shopId) {
		QueryWrapper<ShopLeaseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", shopId);
		ShopLeaseEntity shop = shopLeaseMapper.selectOne(wrapper);
		
		ShopLeaseVo shopLeaseVo = new ShopLeaseVo();
		BeanUtils.copyProperties(shop, shopLeaseVo); // 封装基本信息
		
		
		QueryWrapper<ShopImgEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("shop_id", shopId);
		List<ShopImgEntity> imgs = shopImgMapper.selectList(queryWrapper);
		List<String> strings = new ArrayList<>();
		for (ShopImgEntity img : imgs) {
			strings.add(img.getImgUrl());
		}
		//第二种方法是将list转化为你所需要类型的数组，当然我们用的时候会转化为与list内容相同的类型。
		String[] imgPath = strings.toArray(new String[strings.size()]);
		shopLeaseVo.setImgPath(imgPath); // 封装图片信息
		
		Long[] shopTypeIds = shopLeaseMapper.selectTypeTags(shopId); // 店铺类型标签
		Long[] shopBusinessIds = shopLeaseMapper.selectBusinessTags(shopId); // 店铺行业标签
		shopLeaseVo.setShopTypeIds(shopTypeIds);
		shopLeaseVo.setShopBusinessIds(shopBusinessIds); // 封装标签
		
		return shopLeaseVo;
	}
	
	@Override
//	@Transactional
	public void updateShop(ShopLeaseVo shop, Long shopId) {
		//验证所属社区所属用户房屋是否存在
		if (!iHouseLeaseService.isExistUserHouse(shop.getUid(), shop.getCommunityId().intValue(), shop.getHouseId().intValue())) {
			throw new LeaseException("您并未拥有该房屋!");
		}
		
		ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
		shopLeaseEntity.setId(shopId);
		BeanUtils.copyProperties(shop, shopLeaseEntity);
		shopLeaseMapper.updateById(shopLeaseEntity); // 更新基本信息
		
		QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("shop_id", shopId);
		List<ShopImgEntity> shopImgList = shopImgMapper.selectList(wrapper);
		List<Long> longs = new ArrayList<>();
		for (ShopImgEntity shopImgEntity : shopImgList) {
			longs.add(shopImgEntity.getId());
		}
		shopImgMapper.deleteBatchIds(longs); // 删除图片信息
		
		String[] imgPath = shop.getImgPath();
		List<ShopImgEntity> imgList = new ArrayList<>();
		for (String s : imgPath) {
			ShopImgEntity entity = new ShopImgEntity();
			entity.setId(SnowFlake.nextId());
			entity.setImgUrl(s);
			entity.setShopId(shopId);
			imgList.add(entity);
		}
		shopImgMapper.insertImg(imgList); // 添加图片信息
		
		Long[] tags = shopLeaseMapper.selectTags(shopId);
		shopLeaseMapper.deleteTags(tags); // 查询出该店铺原本有的标签并删除
		
		shopLeaseMapper.insertMiddle(shopId, shop.getShopTypeIds()); // 添加标签
	}
	
	@Override
	@Transactional
	public void cancelShop(String userId, Long shopId, Long communityId, Long houseId) {
		//验证所属社区所属用户房屋是否存在
		if (!iHouseLeaseService.isExistUserHouse(userId, communityId.intValue(), houseId.intValue())) {
			throw new LeaseException("您并未拥有该房屋!");
		}
		
		shopLeaseMapper.deleteById(shopId); // 删除基本信息
		
		// 查询该店铺的图片
		QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("shop_id", shopId);
		List<ShopImgEntity> shopImgList = shopImgMapper.selectList(wrapper);
		List<Long> longs = new ArrayList<>();
		for (ShopImgEntity shopImgEntity : shopImgList) {
			longs.add(shopImgEntity.getId());
		}
		if (!CollectionUtils.isEmpty(longs)) {
			shopImgMapper.deleteBatchIds(longs); // 删除图片信息
		}
		
		Long[] tags = shopLeaseMapper.selectTags(shopId);
		if (tags != null && tags.length > 0) {
			shopLeaseMapper.deleteTags(tags); // 查询出该店铺原本有的标签并删除
		}
	}
	
	@Override
	public List<Map<String, Object>> listShop(String userId) {
		QueryWrapper<ShopLeaseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("uid", userId);
		List<Map<String, Object>> maps = new ArrayList<>();
		List<ShopLeaseEntity> list = shopLeaseMapper.selectList(wrapper);
		for (ShopLeaseEntity shopLeaseEntity : list) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("shopLease", shopLeaseEntity);
			
			QueryWrapper<ShopImgEntity> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("shop_id", shopLeaseEntity.getId());
			List<ShopImgEntity> shopImgEntities = shopImgMapper.selectList(queryWrapper);
			map.put("shopImg", shopImgEntities.get(0).getImgUrl());
			
			maps.add(map);
		}
		return maps;
	}
	
}
