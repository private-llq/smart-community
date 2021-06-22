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
import com.jsy.community.utils.CommonUtils;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.es.ElasticsearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import com.jsy.community.vo.shop.IndexShopVO;
import com.jsy.community.vo.shop.ShopDetailsVO;
import com.jsy.community.vo.shop.ShopLeaseVO;
import com.jsy.community.vo.shop.UserShopLeaseVO;
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
import java.util.*;

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
	/**
	 * @Author lihao
	 * @Description 头图最大数量
	 * @Date 2021/1/13 15:49
	 **/
	private static final Integer HEAD_MAX = 3;
	
	/**
	 * @Author lihao
	 * @Description 室内图最大数量
	 * @Date 2021/1/13 15:50
	 **/
	private static final Integer MIDDLE_MAX = 8;
	
	/**
	 * @Author lihao
	 * @Description 其他图最大数量
	 * @Date 2021/1/13 15:50
	 **/
	private static final Integer OTHER_MAX = 8;
	
	/**
	 * @Author lihao
	 * @Description 金额临界值  大于此值变成 XX万
	 * @Date 2021/1/13 15:55
	 **/
	private static final double NORM_MONEY = 10000.00;
	
	/**
	 * @Author lihao
	 * @Description 金额临界值  等于此值变成 面议
	 * @Date 2021/1/13 15:55
	 **/
	private static final double MIN_MONEY = 0.00;
	
	/**
	 * @Author lihao
	 * @Description 商铺发布图片最大临界值
	 * @Date 2021/2/7 15:55
	 **/
	private static final Integer IMG_MAX = 19;
	
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
	
	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private ILeaseUserService leaseUserService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	/**
	 * @Author lihao
	 * @Description 商铺类型为不限
	 * @Date 2021/1/13 16:04
	 **/
	private static final Long SHOP_TYPE = 1L;
	
	/**
	 * @Author lihao
	 * @Description 商铺行业为不限
	 * @Date 2021/1/13 16:11
	 **/
	private static final Long SHOP_BUSINESS = 9L;
	
	/**
	 * @Author lihao
	 * @Description 商铺来源为不限
	 * @Date 2021/1/14 9:42
	 **/
	private static final Short SHOP_SOURCE = 3;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addShop(ShopQO shop) {
		// 存储店铺基本信息
		ShopLeaseEntity baseShop = new ShopLeaseEntity();
		BeanUtils.copyProperties(shop, baseShop);
		baseShop.setId(SnowFlake.nextId());
		
		List<Long> facilityCodes = shop.getShopFacilityList();
		// 商铺的配套设施Code
		long facilityCode = MyMathUtils.getTypeCode(facilityCodes);
		baseShop.setShopFacility(facilityCode);
		
		List<Long> peopleTypeCodes = shop.getShopPeoples();
		// 商铺的客流人群Code
		long peopleCode = MyMathUtils.getTypeCode(peopleTypeCodes);
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
		ElasticsearchImportProvider.elasticOperationSingle(baseShop.getId(), RecordFlag.LEASE_SHOP, Operation.INSERT, baseShop.getTitle(), CommonUtils.isEmpty(imgPath) ? null : imgPath[0]);
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
		// 封装基本信息
		BeanUtils.copyProperties(shop, shopLeaseVo);
		
		
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
		// 封装图片信息
		shopLeaseVo.setImgPath(imgPath);
		
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
		Integer source = shop.getSource();
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
		
		// 封装详细地址   // TODO: 2021/2/19 详情页并没有这个内容    于林风需要
		Long areaId = shop.getAreaId();
		String area = redisTemplate.opsForValue().get("RegionSingle" + ":" + areaId);
		
		Long communityId = shop.getCommunityId();
		CommunityEntity community = communityService.getCommunityNameById(communityId);
		shopLeaseVo.setShopAddress(area + "  " + community.getName());
		
		
		// 查询店铺发布人的电话和头像
		String uid = shop.getUid();
		UserEntity one = userService.selectOne(uid);
		// 详情页展示的业主名称是用户在发布的时候填写的昵称
		// 详情页展示的电话是用户在发布的时候填写的电话
		one.setRealName(shop.getNickname());
		one.setMobile(shop.getMobile());
		//查询发布人聊天id(im_id)
		String imId = leaseUserService.queryIMIdByUid(uid);
		one.setImId(imId);
		map.put("user", one);
		return map;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateShop(ShopQO shop) {
		ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
		shopLeaseEntity.setId(shop.getShopId());
		List<Long> shopFacilityList = shop.getShopFacilityList();
		List<Long> shopPeoples = shop.getShopPeoples();
		
		BeanUtils.copyProperties(shop, shopLeaseEntity);
		
		// 商铺的配套设施Code
		long facilityCode = MyMathUtils.getTypeCode(shopFacilityList);
		long peopleCode = MyMathUtils.getTypeCode(shopPeoples);
		shopLeaseEntity.setShopFacility(facilityCode);
		shopLeaseEntity.setShopPeople(peopleCode);
		// 更新基本信息
//		shopLeaseEntity.setUpdateTime(null);
		shopLeaseMapper.updateById(shopLeaseEntity);
		
		QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("shop_id", shop.getShopId());
		List<ShopImgEntity> shopImgList = shopImgMapper.selectList(wrapper);
		if (!CollectionUtils.isEmpty(shopImgList)) {
			List<Long> longs = new ArrayList<>();
			for (ShopImgEntity shopImgEntity : shopImgList) {
				longs.add(shopImgEntity.getId());
			}
			// 删除图片信息
			shopImgMapper.deleteBatchIds(longs);
		}
		
		String[] imgPath = shop.getImgPath();
		if (imgPath != null && imgPath.length > 0) {
			List<ShopImgEntity> imgList = new ArrayList<>();
			for (String s : imgPath) {
				ShopImgEntity entity = new ShopImgEntity();
				entity.setId(SnowFlake.nextId());
				entity.setImgUrl(s);
				entity.setShopId(shop.getShopId());
				imgList.add(entity);
			}
			// 添加图片信息
			shopImgMapper.insertImg(imgList);
		}
		ElasticsearchImportProvider.elasticOperationSingle(shop.getShopId(), RecordFlag.LEASE_SHOP, Operation.UPDATE, shop.getTitle(), CommonUtils.isEmpty(imgPath) ? null : imgPath[0]);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void cancelShop(String userId, Long shopId) {
		// 删除基本信息
		shopLeaseMapper.deleteById(shopId);
		
		// 查询该店铺的图片
		QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("shop_id", shopId);
		List<ShopImgEntity> shopImgList = shopImgMapper.selectList(wrapper);
		if (!CollectionUtils.isEmpty(shopImgList)) {
			List<Long> longs = new ArrayList<>();
			for (ShopImgEntity shopImgEntity : shopImgList) {
				longs.add(shopImgEntity.getId());
			}
			// 删除图片信息
			shopImgMapper.deleteBatchIds(longs);
		}
		ElasticsearchImportProvider.elasticOperationSingle(shopId, RecordFlag.LEASE_SHOP, Operation.DELETE, null, null);
	}
	
	
	@Override
	public List<Map<String, Object>> listShop(String userId) {
		QueryWrapper<ShopLeaseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("uid", userId).orderByDesc("create_time");
		List<Map<String, Object>> maps = new ArrayList<>();
		List<ShopLeaseEntity> list = shopLeaseMapper.selectList(wrapper);
		for (ShopLeaseEntity shopLeaseEntity : list) {
			HashMap<String, Object> map = new HashMap<>();
			Integer status = shopLeaseEntity.getStatus();
			if (0 == (status)) {
				shopLeaseEntity.setStatusString("空置中");
			}
			if (1 == (status)) {
				shopLeaseEntity.setStatusString("营业中");
			}
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
			
			// 商铺id
			Long id = record.getId();
			
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
			// 商铺配套设施
			Long shopFacility = record.getShopFacility();
			// 商铺客流人群
			Long shopPeople = record.getShopPeople();
			
			// 将商铺配套设施从常量表中解析出来
			List<Long> facilityList = MyMathUtils.analysisTypeCode(shopFacility);
			// 用于存储配套设施Code
			List<Long> facilityCodes = new ArrayList<>();
			if (!CollectionUtils.isEmpty(facilityList)) {
				facilityCodes.addAll(facilityList);
			}
			List<String> facilitys = houseConstService.getConstByTypeCodeForString(facilityCodes, 16L);
			
			// 将商铺客流人群从常量表中解析出来
			List<Long> peopleList = MyMathUtils.analysisTypeCode(shopPeople);
			// 用于存储客流人群Code
			ArrayList<Long> peopleCodes = new ArrayList<>();
			if (!CollectionUtils.isEmpty(peopleList)) {
				peopleCodes.addAll(peopleList);
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
			// 城市地址
			Long areaId = record.getAreaId();
			String area = redisTemplate.opsForValue().get("RegionSingle" + ":" + areaId);
			
			Long communityId = record.getCommunityId();
			CommunityEntity community = communityService.getCommunityNameById(communityId);
			
			indexShopVO.setAddress(area + "  " + community.getName());
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
		// 查出该区域有哪些小区
		List<CommunityEntity> list = communityService.listCommunityByAreaId(areaId.longValue());
		for (CommunityEntity communityEntity : list) {
			longs.add(communityEntity.getId());
		}
		queryWrapper.in("community_id", longs);
		shopLeaseMapper.selectPage(page, queryWrapper);
		// 分页查出这些小区的商铺
		List<ShopLeaseEntity> records = page.getRecords();
		
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
		int b = 1 / 0;
		// 2. 调用本地服务
		ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
		shopLeaseEntity.setId(140L);
		shopLeaseEntity.setTitle("测试分布式事物");
		shopLeaseMapper.insert(shopLeaseEntity);
		
		System.out.println(11);
		
		
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
		
		// 3. 来源   后面考虑写到配置文件中
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("id", 1);
		hashMap.put("type", "业主");
		
		HashMap<String, Object> hashMap2 = new HashMap<>();
		hashMap2.put("id", 2);
		hashMap2.put("type", "物业");
		
		HashMap<String, Object> hashMap3 = new HashMap<>();
		hashMap3.put("id", 3);
		hashMap3.put("type", "不限");
		
		List<Map<String, Object>> maps = new ArrayList<>();
		maps.add(hashMap);
		maps.add(hashMap2);
		maps.add(hashMap3);
		map.put("source", maps);
		
		
		return map;
	}
	
	@Override
	public Map<String, Object> getPublishTags() {
		// 1. 查询所有商铺分类
		List<CommonConst> typeList = commonConstService.getShopType();
		// 排除掉不限选项[添加的时候不允许选择不限]
		List<CommonConst> types = new ArrayList<>();
		for (CommonConst commonConst : typeList) {
			if (("不限").equals(commonConst.getConstName())) {
				continue;
			}
			types.add(commonConst);
		}
		
		// 2. 查询所有商铺商业
		List<CommonConst> businessList = commonConstService.getBusiness();
		// 排除掉不限选项[添加的时候不允许选择不限]
		List<CommonConst> business = new ArrayList<>();
		for (CommonConst commonConst : businessList) {
			if (("不限").equals(commonConst.getConstName())) {
				continue;
			}
			business.add(commonConst);
		}
		
		HashMap<String, Object> map = new HashMap<>();
		map.put("type", types);
		map.put("business", business);
		
		return map;
	}
	
	@Override
	public List<CommunityEntity> getCommunity(Long areaId) {
		return communityService.listCommunityByAreaId(areaId);
	}
	
	@Override
	public ShopDetailsVO getShopForUpdate(Long shopId) {
		ShopLeaseEntity entity = shopLeaseMapper.selectById(shopId);
		if (entity == null) {
			throw new LeaseException("你选择的商铺不存在");
		}
		ShopDetailsVO detailsVO = new ShopDetailsVO();
		BeanUtils.copyProperties(entity, detailsVO);
		
		// 社区id（商铺所在地址）
		Long communityId = entity.getCommunityId();
		CommunityEntity communityNameById = communityService.getCommunityNameById(communityId);
		detailsVO.setCommunity(communityNameById.getName());
		// 商铺类型
		Long shopTypeId = entity.getShopTypeId();
		CommonConst constType = commonConstService.getConstById(shopTypeId);
		detailsVO.setShopType(constType.getConstName());
		// 商铺行业
		Long shopBusinessId = entity.getShopBusinessId();
		CommonConst constBusiness = commonConstService.getConstById(shopBusinessId);
		if (constBusiness != null) {
			detailsVO.setShopBusiness(constBusiness.getConstName());
		}
		// 商铺图片
		QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("shop_id", entity.getId()).select("img_url");
		List<ShopImgEntity> shopImgEntities = shopImgMapper.selectList(wrapper);
		if (!CollectionUtils.isEmpty(shopImgEntities)) {
			List<String> head = new ArrayList<>();
			List<String> middle = new ArrayList<>();
			List<String> other = new ArrayList<>();
			for (ShopImgEntity shopImgEntity : shopImgEntities) {
				String imgUrl = shopImgEntity.getImgUrl();
				if (imgUrl.contains("shop-head-img")) {
					head.add(imgUrl);
				} else if (imgUrl.contains("shop-middle-img")) {
					middle.add(imgUrl);
				} else {
					other.add(imgUrl);
				}
			}
			detailsVO.setHeadImg(head);
			detailsVO.setMiddleImg(middle);
			detailsVO.setOtherImg(other);
		}
		
		// 配套设施Code
		Long shopFacility = entity.getShopFacility();
		List<Long> facilityList = MyMathUtils.analysisTypeCode(shopFacility);
		detailsVO.setShopFacilityList(facilityList);
		// 客流人群Code
		Long shopPeople = entity.getShopPeople();
		List<Long> peopleList = MyMathUtils.analysisTypeCode(shopPeople);
		detailsVO.setShopPeoples(peopleList);
		return detailsVO;
	}
	
	@Override
	public List<UserShopLeaseVO> listUserShop(String userId) {
		QueryWrapper<ShopLeaseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("uid", userId).orderByDesc("create_time");
		List<ShopLeaseEntity> list = shopLeaseMapper.selectList(wrapper);
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<UserShopLeaseVO>();
		}
		
		ArrayList<UserShopLeaseVO> userShopLeaseVOS = new ArrayList<>();
		for (ShopLeaseEntity shopLeaseEntity : list) {
			UserShopLeaseVO leaseVO = new UserShopLeaseVO();
			BeanUtils.copyProperties(shopLeaseEntity, leaseVO);
			
			Integer status = shopLeaseEntity.getStatus();
			if (0 == (status)) {
				leaseVO.setStatusString("空置中");
			}
			if (1 == (status)) {
				leaseVO.setStatusString("营业中");
			}
			
			
			// 商铺类型
			Long shopTypeId = shopLeaseEntity.getShopTypeId();
			CommonConst constById = commonConstService.getConstById(shopTypeId);
			if (constById != null) {
				leaseVO.setShopType(constById.getConstName());
			}
			
			// 商铺地址
			String city = shopLeaseEntity.getCity();
			String area = shopLeaseEntity.getArea();
			Long communityId = shopLeaseEntity.getCommunityId();
			CommunityEntity communityNameById = communityService.getCommunityNameById(communityId);
			String community = "";
			if (communityNameById != null) {
				community = communityNameById.getName();
				
			}
			leaseVO.setAddress(city + area + community);
			
			QueryWrapper<ShopImgEntity> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("shop_id", shopLeaseEntity.getId());
			List<ShopImgEntity> shopImgEntities = shopImgMapper.selectList(queryWrapper);
			if (!CollectionUtils.isEmpty(shopImgEntities)) {
				leaseVO.setShopShowImg(shopImgEntities.get(0).getImgUrl());
			}
			
			userShopLeaseVOS.add(leaseVO);
		}
		return userShopLeaseVOS;
	}

	
	@Override
	public PageInfo<IndexShopVO> getShopByCondition(BaseQO<HouseLeaseQO> baseQO) {
		Long page = baseQO.getPage();
		Long size = baseQO.getSize();

		Page<ShopLeaseEntity> info = new Page<>(page, size);
		List<ShopLeaseEntity> shopList = shopLeaseMapper.getShopByCondition(baseQO, info);
		ArrayList<IndexShopVO> shopVOS = new ArrayList<>();
		for (ShopLeaseEntity shopLeaseEntity : shopList) {
			IndexShopVO indexShopVO = new IndexShopVO();
			BeanUtils.copyProperties(shopLeaseEntity, indexShopVO);
			// 封装图片
			QueryWrapper<ShopImgEntity> imgWrapper = new QueryWrapper<>();
			imgWrapper.eq("shop_id", shopLeaseEntity.getId());
			imgWrapper.orderByDesc("create_time");
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
			
			// 封装地址
			// 城市地址
			Long areaId = shopLeaseEntity.getAreaId();
			String area = redisTemplate.opsForValue().get("RegionSingle" + ":" + areaId);
			if (StringUtils.isEmpty(area)) {
				area = "";
			}
			Long communityId = shopLeaseEntity.getCommunityId();
			CommunityEntity community = communityService.getCommunityNameById(communityId);
			indexShopVO.setAddress(area + "  " + community.getName());
			
			// 封装金额
			if (shopLeaseEntity.getMonthMoney().doubleValue() > NORM_MONEY) {
				String s = String.format("%.2f", shopLeaseEntity.getMonthMoney().doubleValue() / NORM_MONEY) + "万";
				indexShopVO.setMonthMoneyString(s);
			} else if (shopLeaseEntity.getMonthMoney().compareTo(new BigDecimal(MIN_MONEY)) == 0) {
				String s = "面议";
				indexShopVO.setMonthMoneyString(s);
			} else {
				String s = "" + shopLeaseEntity.getMonthMoney();
				int i = s.lastIndexOf(".");
				String substring = s.substring(0, i) + "元";
				indexShopVO.setMonthMoneyString(substring);
			}
			
			
			// 封装标签集合
			// 商铺配套设施
			Long shopFacility = shopLeaseEntity.getShopFacility();
			// 商铺客流人群
			Long shopPeople = shopLeaseEntity.getShopPeople();
			
			
			// 将商铺配套设施从常量表中解析出来
			List<Long> facilityList = MyMathUtils.analysisTypeCode(shopFacility);
			// 用于存储配套设施Code
			List<Long> facilityCodes = new ArrayList<>();
			if (!CollectionUtils.isEmpty(facilityList)) {
				facilityCodes.addAll(facilityList);
			}
			List<String> facilitys = houseConstService.getConstByTypeCodeForString(facilityCodes, 16L);
			
			// 将商铺客流人群从常量表中解析出来
			List<Long> peopleList = MyMathUtils.analysisTypeCode(shopPeople);
			// 用于存储客流人群Code
			ArrayList<Long> peopleCodes = new ArrayList<>();
			if (!CollectionUtils.isEmpty(peopleList)) {
				peopleCodes.addAll(peopleList);
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
		}
		
		PageInfo<IndexShopVO> objectPageInfo = new PageInfo<>();
		BeanUtils.copyProperties(info, objectPageInfo);
		objectPageInfo.setRecords(shopVOS);
		return objectPageInfo;
	}
}
