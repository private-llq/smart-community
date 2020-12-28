package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.shop.ShopImgEntity;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.mapper.ShopImgMapper;
import com.jsy.community.mapper.ShopLeaseMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.shop.ShopLeaseVo;
import com.jsy.community.api.IHouseLeaseService;
import com.jsy.community.api.IShopLeaseService;
import com.jsy.community.api.LeaseException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;
	
	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private IHouseLeaseService iHouseLeaseService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IUserService userService;
	
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
	public Map<String, Object> getShop(Long shopId) {
		Map<String, Object> map = new HashMap<>();
		
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
		//将list转化为你所需要类型的数组，当然我们用的时候会转化为与list内容相同的类型。
		//Object[] objects = strings.toArray();
		//不能将上述Object[] 转化为String[]，转化的话只能是取出每一个元素再转化。
		// java中的强制类型转换只是针对单个对象的，想要偷懒将整个数组转换成另外一种类型的数组是不行的，这和数组初始化时需要一个个来也是类似的。
		String[] imgPath = strings.toArray(new String[strings.size()]);
		shopLeaseVo.setImgPath(imgPath); // 封装图片信息
		
		Long[] shopTypeIds = shopLeaseMapper.selectTypeTags(shopId); // 店铺类型标签
		Long[] shopBusinessIds = shopLeaseMapper.selectBusinessTags(shopId); // 店铺行业标签
		shopLeaseVo.setShopTypeIds(shopTypeIds);
		shopLeaseVo.setShopBusinessIds(shopBusinessIds); // 封装标签
		map.put("shop", shopLeaseVo);
		
		
		// 查询店铺发布人的电话和头像
		String uid = shop.getUid();
		UserEntity one = userService.selectOne(uid);
		map.put("user", one);
		return map;
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
	
	@Override
	public List<ShopLeaseEntity> getShopByCondition(BaseQO<HouseLeaseQO> baseQO) {
		Long page = baseQO.getPage();
		Long size = baseQO.getSize();
		
		HouseLeaseQO houseQO = baseQO.getQuery();
		
		QueryWrapper<ShopLeaseEntity> wrapper = new QueryWrapper<>();
		
		List<Long> longs = new ArrayList<>(); // 社区id
		
		if (houseQO == null) {
			// 分页
			Page<ShopLeaseEntity> shopLeaseEntityPage = new Page<>(page, size);
			shopLeaseMapper.selectPage(shopLeaseEntityPage, wrapper);
			return shopLeaseEntityPage.getRecords();
		}
		
		Long houseAreaId = houseQO.getHouseAreaId(); // 区域id
		if (houseAreaId != null) {
			List<CommunityEntity> list = communityService.listCommunityByAreaId(houseAreaId);
			if (!CollectionUtils.isEmpty(list)) {
				for (CommunityEntity communityEntity : list) {
					longs.add(communityEntity.getId());
				}
				wrapper.in("community_Id", longs);
			}
		} else {
			List<CommunityEntity> list = communityService.listCommunityByAreaId(500103L);
			for (CommunityEntity communityEntity : list) {
				longs.add(communityEntity.getId());
			}
			wrapper.in("community_Id", longs);
		}
		
		// 租金
		BigDecimal priceMin = houseQO.getHousePriceMin();
		BigDecimal priceMax = houseQO.getHousePriceMax();
		if (priceMin != null && priceMax != null) {
			wrapper.between("month_money", priceMin, priceMax);
		}
		
		// 面积
		BigDecimal squareMeterMin = houseQO.getHouseSquareMeterMin();
		BigDecimal squareMeterMax = houseQO.getHouseSquareMeterMax();
		if (squareMeterMin != null && squareMeterMax != null) {
			wrapper.between("shop_acreage", squareMeterMin, squareMeterMax);
		}
		
		// 更多
		List<Long> advantage = houseQO.getHouseAdvantage();
		if (!CollectionUtils.isEmpty(advantage)) {
			List<Long> list = shopLeaseMapper.selectMiddle(advantage);
			wrapper.in("id", list);
		}
		
		// 分页
		Page<ShopLeaseEntity> shopLeaseEntityPage = new Page<>(page, size);
		shopLeaseMapper.selectPage(shopLeaseEntityPage, wrapper);
		return shopLeaseEntityPage.getRecords();
	}
	
	@Override
	public PageInfo<ShopLeaseEntity> getShopBySearch(BaseQO<ShopLeaseEntity> baseQO, String query, Integer areaId) {
		Page<ShopLeaseEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
		
		QueryWrapper<ShopLeaseEntity> queryWrapper = new QueryWrapper<>();
		
		List<Long> longs = new ArrayList<>();
		if (!StringUtils.isEmpty(query)) {//有搜索条件的时候
//			QueryWrapper<CommunityEntity> wrapper = new QueryWrapper<>();
//			wrapper.like("name", communityName);
//			List<CommunityEntity> list = communityService.list1(wrapper);  // 这样调会报错   2020年12月21日16:43:57
			
			List<CommunityEntity> list = communityService.listCommunityByName(query, areaId);
			for (CommunityEntity communityEntity : list) {
				longs.add(communityEntity.getId());
			}
			queryWrapper.in("community_id", longs);
			shopLeaseMapper.selectPage(page, queryWrapper);
			PageInfo<ShopLeaseEntity> pageInfo = new PageInfo<>();
			BeanUtils.copyProperties(page, pageInfo);
			return pageInfo;
		} else {
			List<CommunityEntity> list = communityService.listCommunityByAreaId(areaId.longValue());
			for (CommunityEntity communityEntity : list) {
				longs.add(communityEntity.getId());
			}
			queryWrapper.in("community_id", longs);
			shopLeaseMapper.selectPage(page, queryWrapper);
			shopLeaseMapper.selectPage(page, queryWrapper);
			PageInfo<ShopLeaseEntity> info = new PageInfo<>();
			BeanUtils.copyProperties(page, info);
			return info;
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	@LcnTransaction
	public void testTransaction() {
		// 1. 调用远端服务
		communityService.addCommunityEntity();
		
//		int b =  1/0;
		
		// 2. 调用本地服务
		ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
		shopLeaseEntity.setId(1233L);
		shopLeaseEntity.setTitle("测试分布式事物");
		shopLeaseMapper.insert(shopLeaseEntity);
	}
	
	
}
