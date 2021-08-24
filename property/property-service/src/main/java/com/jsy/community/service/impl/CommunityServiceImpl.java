package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.DateCalculateUtil;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.property.PropertyCommunityListVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区 服务实现类
 *
 * @author YuLF
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group)
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, CommunityEntity> implements ICommunityService {
	
	@Autowired
	private CommunityMapper communityMapper;
	
	@Autowired
	private UserHouseMapper userHouseMapper;
	
	@Autowired
	private HouseMemberMapper houseMemberMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private HouseMapper houseMapper;
	
	@Autowired
	private CarMapper carMapper;

	@Autowired
	private CarPositionMapper carPositionMapper;
	
	@Autowired
	private PropertyFinanceOrderMapper propertyFinanceOrderMapper;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminConfigService adminConfigService;
	
	@Override
	public List<CommunityEntity> listCommunityByName(String query,Integer areaId) {
		QueryWrapper<CommunityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("area_id",areaId).like("name", query).or().like("detail_address", query);
		return communityMapper.selectList(wrapper);
	}
	
	@Override
	public List<CommunityEntity> listCommunityByAreaId(Long areaId) {
		QueryWrapper<CommunityEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("area_id", areaId);
		return communityMapper.selectList(queryWrapper);
	}
	
	/**
	* @Description: 查询社区模式
	 * @Param: [id]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/1/21
	**/
	@Override
	public Integer getCommunityMode(Long id){
		return communityMapper.getCommunityMode(id);
	}
	
	@Override
	@Transactional
	@LcnTransaction
	public void addCommunityEntity() {
		CommunityEntity communityEntity = new CommunityEntity();
		communityEntity.setId(140L);
		communityEntity.setName("测试分布式事物");
		communityMapper.insert(communityEntity);
	}
	
	@Override
	public CommunityEntity getCommunityNameById(Long communityId) {
		return communityMapper.selectById(communityId);
	}
	
	/**
	* @Description: ids批量查小区
	 * @Param: [idList]
	 * @Return: java.util.List<com.jsy.community.entity.CommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Override
	public List<CommunityEntity> queryCommunityBatch(List<Long> idList){
		return communityMapper.queryCommunityBatch(idList);
	}
	
	/**
	* @Description: id单查小区
	 * @Param: [communityId]
	 * @Return: com.jsy.community.entity.CommunityEntity
	 * @Author: chq459799974
	 * @Date: 2021-07-29
	**/
	@Override
	public CommunityEntity queryDetails(Long communityId){
		return communityMapper.selectOne(new QueryWrapper<CommunityEntity>().select("*").eq("id",communityId));
	}
	
	/**
	 * 获取社区电子地图
	 */
	@Override
	public Map<String, Object> getElectronicMap(Long communityId) {
		HashMap<String, Object> hashMap = new HashMap<>();
		
		//1. 获取社区基本信息
		CommunityEntity communityEntity = communityMapper.selectById(communityId);
		String name = communityEntity.getName();
		String number = communityEntity.getNumber();
		String detailAddress = communityEntity.getDetailAddress();
		BigDecimal acreage = communityEntity.getAcreage();
		BigDecimal lon = communityEntity.getLon();
		BigDecimal lat = communityEntity.getLat();
		
		hashMap.put("name",name);
		hashMap.put("number",number);
		hashMap.put("detailAddress",detailAddress);
		hashMap.put("acreage",acreage);
		hashMap.put("lon",lon);
		hashMap.put("lat",lat);
		
		//2. 获取社区总人数
		// 认证的业主总数【根据房屋认证表获取】
//		Integer userHouseCount = userHouseMapper.selectCount(new QueryWrapper<UserHouseEntity>().eq("check_status",1));
		Integer userHouseCount = userHouseMapper.selectCount(new QueryWrapper<>());
		
		// 房间成员人数【不包含业主本人】
		Integer houseMemberCount = houseMemberMapper.selectCount(null);
		
		hashMap.put("count",userHouseCount+houseMemberCount);
		
		return hashMap;
	}
	
	/**
	* @Description: 查询所有小区ID
	 * @Param: []
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/6/26
	**/
	@Override
	public List<Long> queryAllCommunityIdList(){
		return communityMapper.queryAllCommunityIdList();
	}

	/**
	 * @param communityEntity :
	 * @author: Pipi
	 * @description: 物业端新增社区
	 * @return: java.lang.Integer
	 * @date: 2021/7/21 17:57
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addCommunity(CommunityEntity communityEntity, String uid) {
		communityEntity.setId(SnowFlake.nextId());
		int insert = communityMapper.insert(communityEntity);
		if (insert > 0) {
			adminConfigService.addAdminCommunity(uid, communityEntity.getId());
		}
		return communityEntity.getId();
	}

	/**
	 * @param baseQO : 查询条件
	 * @param communityIds    : 登录用户uid
	 * @author: Pipi
	 * @description: 分页查询小区列表
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.vo.property.PropertyCommunityListVO>
	 * @date: 2021/7/22 11:46
	 **/
	@Override
	public PageInfo<PropertyCommunityListVO> queryPropertyCommunityList(BaseQO<CommunityEntity> baseQO, List<Long> communityIds) {
		PageInfo<PropertyCommunityListVO> communityListVOPageInfo = new PageInfo<>();
		Page<CommunityEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		QueryWrapper<CommunityEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id, name, province_id, city_id, area_id, detail_address, property_id");
		queryWrapper.orderByDesc("id");
		CommunityEntity query = baseQO.getQuery();
		if (StringUtils.isNotBlank(query.getName())) {
			queryWrapper.like("name", query.getName());
		}
		if (query.getProvinceId() != null) {
			queryWrapper.eq("province_id", query.getProvinceId());
		}
		if (query.getCityId() != null) {
			queryWrapper.eq("city_id", query.getProvinceId());
		}
		if (query.getAreaId() != null) {
			queryWrapper.eq("area_id", query.getProvinceId());
		}
		if (!CollectionUtils.isEmpty(communityIds)) {
			queryWrapper.in("id", communityIds);
		} else {
			// 如果登录用户有权限的社区为null,直接返回空
			return communityListVOPageInfo;
		}
		Page<CommunityEntity> entityPage = communityMapper.selectPage(page, queryWrapper);
		if (!CollectionUtils.isEmpty(entityPage.getRecords())) {
			for (CommunityEntity record : entityPage.getRecords()) {
				PropertyCommunityListVO communityVO = new PropertyCommunityListVO();
				BeanUtils.copyProperties(record, communityVO);
				communityVO.setPropertyName("纵横物业");
				String province = (String) redisTemplate.opsForValue().get("RegionSingle:" + String.valueOf(record.getProvinceId()));
				String city = (String) redisTemplate.opsForValue().get("RegionSingle:" + String.valueOf(record.getCityId()));
				String area = (String) redisTemplate.opsForValue().get("RegionSingle:" + String.valueOf(record.getAreaId()));
				province = StringUtils.isNotBlank(province) ? province : "";
				city = StringUtils.isNotBlank(city) ? city : "";
				area = StringUtils.isNotBlank(area) ? area : "";
				communityVO.setAddress(province + city + area + record.getDetailAddress());
				communityVO.setIdStr(String.valueOf(record.getId()));
				if (communityListVOPageInfo.getRecords().size() > 0) {
					communityListVOPageInfo.getRecords().add(communityVO);
				} else {
					ArrayList<PropertyCommunityListVO> arrayList = new ArrayList<>();
					arrayList.add(communityVO);
					communityListVOPageInfo.setRecords(arrayList);
				}
			}
		}
		communityListVOPageInfo.setTotal(entityPage.getTotal());
		communityListVOPageInfo.setCurrent(entityPage.getCurrent());
		communityListVOPageInfo.setSize(entityPage.getSize());
		return communityListVOPageInfo;
	}

	/**
	 * @param communityEntity :
	 * @author: Pipi
	 * @description: 物业端更新社区信息
	 * @return: java.lang.Integer
	 * @date: 2021/7/22 18:04
	 **/
	@Override
	public Integer updateCommunity(CommunityEntity communityEntity) {
		return communityMapper.updateById(communityEntity);
	}
	
	/**
	 * @author: DKS
	 * @description: 获取小区概况
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/24 11:52
	 **/
	@Override
	public CommunitySurveyEntity getCommunitySurvey(Integer month, Long adminCommunityId) {
		LocalDate startTime = null;
		LocalDate endTime = null;
		try {
			String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(month);
			String lastYearDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(month);
			startTime = LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			endTime = LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回给前端实体
		CommunitySurveyEntity communitySurveyEntity = new CommunitySurveyEntity();
		// 查小区下所有房屋总数和楼栋总数
		List<HouseEntity> allHouse = houseMapper.getAllHouse(adminCommunityId);
		List<HouseEntity> buildingList = houseMapper.getBuildingList(adminCommunityId);
		communitySurveyEntity.setHouseSum(allHouse.size());
		communitySurveyEntity.setBuildingSum(buildingList.size());
		
		// 查小区下业主数量和租户数量
		List<HouseMemberEntity> allOwner = houseMemberMapper.getAllOwnerByCommunity(adminCommunityId);
		List<HouseMemberEntity> allTenant = houseMemberMapper.getAllTenantByCommunity(adminCommunityId);
		communitySurveyEntity.setOwnerCount(allOwner.size());
		communitySurveyEntity.setTenantCount(allTenant.size());
		
		// 查询小区下所有车位和车辆
		List<CarPositionEntity> allCarPosition = carPositionMapper.getAllCarPositionByCommunity(adminCommunityId);
		List<CarEntity> allCar = carMapper.getAllCarByCommunity(adminCommunityId);
		communitySurveyEntity.setCarPositionCount(allCarPosition.size());
		communitySurveyEntity.setCarCount(allCar.size());
		
		// 小区月收费统计
		if (startTime != null && endTime != null) {
			List<Map<String, BigDecimal>> maps = propertyFinanceOrderMapper.chargeByDate(startTime, endTime, adminCommunityId);
			communitySurveyEntity.setDateByPropertyFee(maps);
			BigDecimal monthByPropertyFee = propertyFinanceOrderMapper.chargeByMonth(startTime, endTime, adminCommunityId);
			communitySurveyEntity.setMonthByPropertyFee(monthByPropertyFee);
		}
		
		return communitySurveyEntity;
	}
}
