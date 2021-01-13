package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.shop.ShopImgEntity;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.mapper.ShopImgMapper;
import com.jsy.community.mapper.ShopLeaseMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.qo.shop.ShopQO;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.shop.IndexShopVO;
import com.jsy.community.vo.shop.ShopLeaseVO;
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
	
	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private IHouseConstService houseConstService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private ICommonConstService commonConstService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Override
	@Transactional
	public void addShop(ShopQO shop) {
		// 存储店铺基本信息
		ShopLeaseEntity baseShop = new ShopLeaseEntity();
		BeanUtils.copyProperties(shop, baseShop);
		baseShop.setId(SnowFlake.nextId());
		
		List<Long> facilityCodes = shop.getShopFacilityList();
		long facilityCode = MyMathUtils.getTypeCode(facilityCodes);// 商铺的配套设施Code
		baseShop.setShopFacility(facilityCode);
		
		List<Long> peopleTypeCodes = shop.getShopPeoples();
		long peopleCode = MyMathUtils.getTypeCode(peopleTypeCodes);// 商铺的客流人群Code
		baseShop.setShopPeople(peopleCode);
		
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
		}
	}
	
	@Override
	public Map<String, Object> getShop(Long shopId) {
		Map<String, Object> map = new HashMap<>();
		
		QueryWrapper<ShopLeaseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", shopId);
		ShopLeaseEntity shop = shopLeaseMapper.selectOne(wrapper);
		
		if (shop == null) {
			return null;
		}
		ShopLeaseVO shopLeaseVo = new ShopLeaseVO();
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
		
		// 封装配套设施
		Long shopFacility = shop.getShopFacility();
		List<Long> facilityCode = MyMathUtils.analysisTypeCode(shopFacility);
		List<String> constByTypeCodeForString = houseConstService.getConstByTypeCodeForString(facilityCode, 16L);
		if (!CollectionUtils.isEmpty(constByTypeCodeForString)) {
			shopLeaseVo.setShopFacilityStrings(constByTypeCodeForString);
		}
		
		// 封装类型
		Long shopTypeId = shop.getShopTypeId();
		CommonConst type = commonConstService.getConstById(shopTypeId);
		if (type != null) {
			shopLeaseVo.setShopTypeString(type.getConstName());
		}
		
		// 封装行业
		Long shopBusinessId = shop.getShopBusinessId();
		CommonConst business = commonConstService.getConstById(shopBusinessId);
		if (business != null) {
			shopLeaseVo.setShopBusinessString(business.getConstName());
		}
		
		// 封装客流人群
		Long shopPeople = shop.getShopPeople();
		List<Long> peopleCode = MyMathUtils.analysisTypeCode(shopPeople);
		List<String> constByPeopleCodeForString = houseConstService.getConstByTypeCodeForString(peopleCode, 17L);
		if (!CollectionUtils.isEmpty(constByPeopleCodeForString)) {
			shopLeaseVo.setShopPeopleStrings(constByPeopleCodeForString);
		}
		
		map.put("shop", shopLeaseVo);
		
		// 将标签封装成一个属性 便于前端使用
		List<String> facilityStrings = shopLeaseVo.getShopFacilityStrings();
		List<String> peopleStrings = shopLeaseVo.getShopPeopleStrings();
		ArrayList<String> list = new ArrayList<>();
		// 将两个集合封装成一个集合
		if (facilityStrings != null) {
			list.addAll(peopleStrings);
			list.addAll(facilityStrings);
			shopLeaseVo.setTags(list);
		}
		
		// 封装来源
		Integer source = shop.getSource();// 1 2 3
		Map<Integer, String> kv = BusinessEnum.SourceEnum.getKv();
		String s = kv.get(source);
		shopLeaseVo.setSourceString(s);
		
		// 封装状态
		Integer status = shop.getStatus();
		if (status == 0) {
			shopLeaseVo.setStatusString("空置中");
		} else {
			shopLeaseVo.setStatusString("经营中");
		}
		
		// 查询店铺发布人的电话和头像
		String uid = shop.getUid();
		UserEntity one = userService.selectOne(uid);
		// 详情页展示的业主名称是用户在发布的时候填写的昵称
		// 详情页展示的电话是用户在发布的时候填写的电话
		one.setRealName(shop.getNickname());
		one.setMobile(shop.getMobile());
		map.put("user", one);
		return map;
	}
	
	@Override
	@Transactional
	public void updateShop(ShopQO shop, Long shopId) {
		ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
		shopLeaseEntity.setId(shopId);
		BeanUtils.copyProperties(shop, shopLeaseEntity);
		shopLeaseMapper.updateById(shopLeaseEntity); // 更新基本信息
		
		QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("shop_id", shopId);
		List<ShopImgEntity> shopImgList = shopImgMapper.selectList(wrapper);
		if (!CollectionUtils.isEmpty(shopImgList)) {
			List<Long> longs = new ArrayList<>();
			for (ShopImgEntity shopImgEntity : shopImgList) {
				longs.add(shopImgEntity.getId());
			}
			shopImgMapper.deleteBatchIds(longs); // 删除图片信息
		}
		
		String[] imgPath = shop.getImgPath();
		if (imgPath != null && imgPath.length > 0) {
			List<ShopImgEntity> imgList = new ArrayList<>();
			for (String s : imgPath) {
				ShopImgEntity entity = new ShopImgEntity();
				entity.setId(SnowFlake.nextId());
				entity.setImgUrl(s);
				entity.setShopId(shopId);
				imgList.add(entity);
			}
			shopImgMapper.insertImg(imgList); // 添加图片信息
		}
	}
	
	@Override
	@Transactional
	public void cancelShop(String userId, Long shopId) {
		shopLeaseMapper.deleteById(shopId); // 删除基本信息
		
		// 查询该店铺的图片
		QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("shop_id", shopId);
		List<ShopImgEntity> shopImgList = shopImgMapper.selectList(wrapper);
		if (!CollectionUtils.isEmpty(shopImgList)) {
			List<Long> longs = new ArrayList<>();
			for (ShopImgEntity shopImgEntity : shopImgList) {
				longs.add(shopImgEntity.getId());
			}
			shopImgMapper.deleteBatchIds(longs); // 删除图片信息
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
			if (!CollectionUtils.isEmpty(shopImgEntities)) {
				map.put("shopImg", shopImgEntities.get(0).getImgUrl());
			}
			
			maps.add(map);
		}
		return maps;
	}
	
	@Override
	public PageInfo<IndexShopVO> getShopByCondition(BaseQO<HouseLeaseQO> baseQO, String query, Integer areaId) {
		Page<ShopLeaseEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
		
		QueryWrapper<ShopLeaseEntity> wrapper = new QueryWrapper<>();
		
		List<Long> longs = new ArrayList<>();   // 用于接收该区域的小区id集合
		List<IndexShopVO> shopVOS = new ArrayList<>(); // 用于封装最后数据
		
		HouseLeaseQO houseQO = baseQO.getQuery(); // 筛选条件
		
		// 搜索条件和筛选条件都不存在   PS：此时区域id这里从url上获取，若没有 默认查渝北区
		if (houseQO == null && query == null) {
			// 根据地区id查询该区域所有小区
			List<CommunityEntity> list = communityService.listCommunityByAreaId(areaId.longValue());
			if (CollectionUtils.isEmpty(list)) {
				return null;
			}
			for (CommunityEntity communityEntity : list) {
				longs.add(communityEntity.getId());
			}
			if (!CollectionUtils.isEmpty(longs)) {
				wrapper.in("community_id", longs);
				// 分页查询出该区域发布的所有商铺
				shopLeaseMapper.selectPage(page, wrapper);
			}
			return commonCode(page, shopVOS);
		}
		
		// 搜索条件存在但筛选条件不存在   PS：此时区域id这里从url上获取，若没有 默认查渝北区
		if (houseQO == null && (!StringUtils.isEmpty(query))) {
			// 根据小区名或地址，地区id查询该区域所有小区
			List<CommunityEntity> list = communityService.listCommunityByName(query, areaId);
			if (CollectionUtils.isEmpty(list)) {
				return null;
			}
			for (CommunityEntity communityEntity : list) {
				longs.add(communityEntity.getId());
			}
			if (!CollectionUtils.isEmpty(longs)) {
				wrapper.in("community_id", longs);
				// 分页查询出该区域发布的所有商铺
				shopLeaseMapper.selectPage(page, wrapper);
			}
			return commonCode(page, shopVOS);
		}
		
		// 搜索条件不存在但筛选条件存在
		if (houseQO != null && query == null) {
			// 区域id
			Long houseAreaId = houseQO.getHouseAreaId();
			if (houseAreaId != null) {
				List<CommunityEntity> list = communityService.listCommunityByAreaId(houseAreaId);
				if (CollectionUtils.isEmpty(list)) {
					return null;
				}
				for (CommunityEntity communityEntity : list) {
					longs.add(communityEntity.getId());
				}
				if (!CollectionUtils.isEmpty(longs)) {
					wrapper.in("community_id", longs);
				}
			} else {
				List<CommunityEntity> list = communityService.listCommunityByAreaId(500103L);// 若没有选择区域 则查询渝北区
				if (list == null) {
					return null;
				}
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
			
			// 来源
			Short sourceId = houseQO.getHouseSourceId();
			if (sourceId != null && sourceId != 3) {
				wrapper.eq("source", sourceId);
			}
			
			// 类型
			Long shopTypeIds = houseQO.getShopTypeId();
			if (shopTypeIds != null) {
				// 用户选择的商铺类型是不限
				if (shopTypeIds.equals(1L)) {
					// 根据类型值 从常量表查询出所有商铺类型id
					List<Long> typeIds = commonConstService.listByType(2L);
					wrapper.in("shop_type_id", typeIds);
				} else {
					// 选择的不是不限
					wrapper.eq("shop_type_id", shopTypeIds);
				}
			}
			
			
			// 行业
			Long shopBusinessIds = houseQO.getShopBusinessId();
			if (shopBusinessIds != null) {
				// 用户选择的店铺商业是不限
				if (shopBusinessIds.equals(9L)) {
					// 1.  根据类型值 从常量表查询出所有商铺行业id
					List<Long> businessIds = commonConstService.listByType(3L);
					if (!CollectionUtils.isEmpty(businessIds)) {
						wrapper.in("shop_business_id", businessIds);
					}
				} else {
					// 选择的不是不限
					wrapper.eq("shop_business_id", shopBusinessIds);
				}
			}
			
			// 分页
			shopLeaseMapper.selectPage(page, wrapper);
			return commonCode(page, shopVOS);
		}
		
		// 搜索条件和筛选条件都存在
		if (houseQO != null && (!StringUtils.isEmpty(query))) {
			// 区域id
			Long houseAreaId = houseQO.getHouseAreaId();
			if (houseAreaId != null) {
				List<CommunityEntity> list = communityService.listCommunityByName(query, houseAreaId.intValue());
				if (CollectionUtils.isEmpty(list)) {
					return null;
				}
				for (CommunityEntity communityEntity : list) {
					longs.add(communityEntity.getId());
				}
				if (!CollectionUtils.isEmpty(longs)) {
					wrapper.in("community_id", longs);
				}
			} else {
				List<CommunityEntity> list = communityService.listCommunityByName(query, 500103);// 若没有选择区域 则查询渝北区
				if (CollectionUtils.isEmpty(list)) {
					return null;
				}
				for (CommunityEntity communityEntity : list) {
					longs.add(communityEntity.getId());
				}
				if (!CollectionUtils.isEmpty(longs)) {
					wrapper.in("community_id", longs);
				}
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
			
			// 来源
			Short sourceId = houseQO.getHouseSourceId();
			if (sourceId != null && sourceId != 3) {
				wrapper.eq("source", sourceId);
			}
			
			// 类型
			Long shopTypeIds = houseQO.getShopTypeId();
			if (shopTypeIds != null) {
				// 用户选择的商铺类型是不限
				if (shopTypeIds.equals(1L)) {
					// 根据类型值 从常量表查询出所有商铺类型id
					List<Long> typeIds = commonConstService.listByType(2L);
					wrapper.in("shop_type_id", typeIds);
				} else {
					// 选择的不是不限
					wrapper.eq("shop_type_id", shopTypeIds);
				}
			}
			
			// 行业
			Long shopBusinessIds = houseQO.getShopBusinessId();
			if (shopBusinessIds != null) {
				// 用户选择的店铺商业是不限
				if (shopBusinessIds.equals(9L)) {
					// 1.  根据类型值 从常量表查询出所有商铺行业id
					List<Long> businessIds = commonConstService.listByType(3L);
					if (!CollectionUtils.isEmpty(businessIds)) {
						wrapper.in("shop_business_id", businessIds);
					}
				} else {
					// 选择的不是不限
					wrapper.eq("shop_business_id", shopBusinessIds);
				}
			}
			
			// 分页
			shopLeaseMapper.selectPage(page, wrapper);
			return commonCode(page, shopVOS);
		}
		return null;
	}
	
	/**
	 * @return com.jsy.community.utils.PageInfo<com.jsy.community.vo.shop.IndexShopVO>
	 * @Author lihao
	 * @Description 将shopLeaseEntity封装成IndexShopVO
	 * @Date 2021/1/5 23:47
	 * @Param [page, shopVOS]
	 **/
	private PageInfo<IndexShopVO> commonCode(Page<ShopLeaseEntity> page, List<IndexShopVO> shopVOS) {
		List<ShopLeaseEntity> records = page.getRecords();
		if (CollectionUtils.isEmpty(records)) {
			return null;
		}
		// 将商铺封装成IndexShopVO
		for (ShopLeaseEntity record : records) {
			IndexShopVO indexShopVO = new IndexShopVO();
			BeanUtils.copyProperties(record, indexShopVO);
			
			Long id = record.getId(); // 商铺id
			
			// 封装图片
			QueryWrapper<ShopImgEntity> imgWrapper = new QueryWrapper<>();
			imgWrapper.eq("shop_id", id);
			List<ShopImgEntity> shopImgEntities = shopImgMapper.selectList(imgWrapper);
			if (!CollectionUtils.isEmpty(shopImgEntities)) {
				for (ShopImgEntity imgEntity : shopImgEntities) {
					String imgUrl = imgEntity.getImgUrl();
					if (imgUrl.contains("shop-head")) {
						indexShopVO.setImgPath(imgUrl);
					}
					break;
				}
			}
			
			// 封装标签集合
			Long shopFacility = record.getShopFacility(); // 商铺配套设施
			Long shopPeople = record.getShopPeople(); // 商铺客流人群
			
			List<Long> facilityList = MyMathUtils.analysisTypeCode(shopFacility); // 将商铺配套设施从常量表中解析出来
			List<Long> facilityCodes = new ArrayList<>(); // 用于存储配套设施Code
			if (!CollectionUtils.isEmpty(facilityList)) {
				for (Long aLong : facilityList) {
					facilityCodes.add(aLong);
				}
			}
			List<String> facilitys = houseConstService.getConstByTypeCodeForString(facilityCodes, 16L);
			
			List<Long> peopleList = MyMathUtils.analysisTypeCode(shopPeople);// 将商铺客流人群从常量表中解析出来
			ArrayList<Long> peopleCodes = new ArrayList<>();// 用于存储客流人群Code
			if (!CollectionUtils.isEmpty(peopleList)) {
				for (Long aLong : peopleList) {
					peopleCodes.add(aLong);
				}
			}
			List<String> peoples = houseConstService.getConstByTypeCodeForString(peopleCodes, 17L);
			
			// 将2个集合合并为1个集合
			ArrayList<String> list = new ArrayList<>();
			// 将两个集合封装成一个集合
			if (facilitys != null) {
				list.addAll(peoples);
				list.addAll(facilitys);
				indexShopVO.setTags(list);
			}
			shopVOS.add(indexShopVO);
			
			
			// 封装地址
			Long cityId = record.getCityId();
			String city = redisTemplate.opsForValue().get("RegionSingle" + ":" + cityId);
			
			Long areaId = record.getAreaId();
			String area = redisTemplate.opsForValue().get("RegionSingle" + ":" + areaId);
			
			indexShopVO.setAddress(city + "  " + area);
		}
		PageInfo<IndexShopVO> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(page, pageInfo);
		pageInfo.setRecords(shopVOS);
		return pageInfo;
	}
	
	@Override
	public PageInfo<IndexShopVO> getShopBySearch(BaseQO<ShopLeaseEntity> baseQO, String query, Integer areaId) {
		Page<ShopLeaseEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
		QueryWrapper<ShopLeaseEntity> queryWrapper = new QueryWrapper<>();
		
		List<Long> longs = new ArrayList<>();
		List<IndexShopVO> shopVOS = new ArrayList<>();
		// 如果有条件
		if (!StringUtils.isEmpty(query)) {
			
			List<CommunityEntity> list = communityService.listCommunityByName(query, areaId);
			for (CommunityEntity communityEntity : list) {
				longs.add(communityEntity.getId());
			}
			if (!CollectionUtils.isEmpty(longs)) {
				queryWrapper.in("community_id", longs);
				shopLeaseMapper.selectPage(page, queryWrapper);
			}
			
			List<ShopLeaseEntity> records = page.getRecords();
			for (ShopLeaseEntity record : records) {
				IndexShopVO indexShopVO = new IndexShopVO();
				Long id = record.getId();
				
				// 封装图片
				QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
				wrapper.eq("shop_id", id).last("limit 1");
				ShopImgEntity shopImgEntity = shopImgMapper.selectOne(wrapper);
				indexShopVO.setImgPath(shopImgEntity.getImgUrl());
				
				// 封装标签集合
				Long[] tags = shopLeaseMapper.selectTags(id);
				List<String> constNameByConstId = houseConstService.getConstNameByConstId(tags);
				indexShopVO.setTags(constNameByConstId);
				
				BeanUtils.copyProperties(record, indexShopVO);
				shopVOS.add(indexShopVO);
			}
			
			PageInfo<IndexShopVO> pageInfo = new PageInfo<>();
			BeanUtils.copyProperties(page, pageInfo);
			pageInfo.setRecords(shopVOS);
			return pageInfo;
		}
		
		// 如果没条件
		List<CommunityEntity> list = communityService.listCommunityByAreaId(areaId.longValue());// 查出该区域有哪些小区
		for (CommunityEntity communityEntity : list) {
			longs.add(communityEntity.getId());
		}
		queryWrapper.in("community_id", longs);
		shopLeaseMapper.selectPage(page, queryWrapper);
		List<ShopLeaseEntity> records = page.getRecords();// 分页查出这些小区的商铺
		
		// 封装小区商铺
		for (ShopLeaseEntity record : records) {
			IndexShopVO indexShopVO = new IndexShopVO();
			BeanUtils.copyProperties(record, indexShopVO);
			
			Long id = record.getId();
			
			// 封装图片
			QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
			wrapper.eq("shop_id", id).last("limit 1");
			ShopImgEntity shopImgEntity = shopImgMapper.selectOne(wrapper);
			if (shopImgEntity != null) {
				indexShopVO.setImgPath(shopImgEntity.getImgUrl());
			}
			
			// 封装标签集合
			Long[] tags = shopLeaseMapper.selectTags(id);
			if (tags != null && tags.length > 0) {
				List<String> constNameByConstId = houseConstService.getConstNameByConstId(tags);
				indexShopVO.setTags(constNameByConstId);
			}
			shopVOS.add(indexShopVO);
		}
		
		PageInfo<IndexShopVO> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(page, pageInfo);
		pageInfo.setRecords(shopVOS);
		return pageInfo;
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
	
	@Override
	public Map<String, Object> moreOption() {
		// 1. 查询所有商铺分类
		List<CommonConst> typeList = commonConstService.getShopType();
		
		// 2. 查询所有商铺商业
		List<CommonConst> businessList = commonConstService.getBusiness();
		
		HashMap<String, Object> map = new HashMap<>();
		map.put("type", typeList);
		map.put("business", businessList);
		return map;
	}
	
}
