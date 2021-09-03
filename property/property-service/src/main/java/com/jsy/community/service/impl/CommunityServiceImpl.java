package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.IShopLeaseService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.property.ConsoleEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.*;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
	
	@Autowired
	private PropertyCompanyMapper propertyCompanyMapper;
	
	@Autowired
	private PeopleHistoryMapper peopleHistoryMapper;
	
	@Autowired
	private CarOrderMapper carOrderMapper;

	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminConfigService adminConfigService;
	
	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private IShopLeaseService shopLeaseService;
	
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
		QueryWrapper<CommunityEntity> queryWrapper = new QueryWrapper<CommunityEntity>().select("*").eq("id", communityId);
		CommunityEntity communityEntity = communityMapper.selectOne(queryWrapper);
		if (communityEntity != null) {
			QueryWrapper<PropertyCompanyEntity> propertyCompanyEntityQueryWrapper = new QueryWrapper<>();
			propertyCompanyEntityQueryWrapper.select("name");
			propertyCompanyEntityQueryWrapper.eq("id", communityEntity.getPropertyId());
			PropertyCompanyEntity propertyCompanyEntity = propertyCompanyMapper.selectOne(propertyCompanyEntityQueryWrapper);
			if (propertyCompanyEntity != null) {
				communityEntity.setCompanyName(propertyCompanyEntity.getName());
			}
		}
		return communityEntity;
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
			queryWrapper.eq("city_id", query.getCityId());
		}
		if (query.getAreaId() != null) {
			queryWrapper.eq("area_id", query.getAreaId());
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
		
		// 获取当前时间
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		Date sDate = null;
		try {
			sDate = formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 获取当前时间后一天日期
		Calendar c = Calendar.getInstance();
		c.setTime(sDate);
		c.add(Calendar.DAY_OF_MONTH, 1);
		sDate = c.getTime();
		String format = formatter.format(sDate);
		LocalDate beginTime = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalDate overTime = LocalDate.parse(format, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		// 查小区开门次数
		communitySurveyEntity.setOpenDoorCount(peopleHistoryMapper.selectOpenDoorCount(adminCommunityId, beginTime, overTime));
		// 查访客次数
		communitySurveyEntity.setVisitorCount(peopleHistoryMapper.selectVisitorCount(adminCommunityId, beginTime, overTime));
		// 查小区下所有房屋总数和楼栋总数
		List<HouseEntity> allHouse = houseMapper.getAllHouse(adminCommunityId);
		List<HouseEntity> buildingList = houseMapper.getBuildingList(adminCommunityId);
		communitySurveyEntity.setHouseSum(allHouse.size());
		communitySurveyEntity.setBuildingSum(buildingList.size());
		List<Long> communityIdList = new ArrayList<>();
		communityIdList.add(adminCommunityId);
		// 查住宅数量
		communitySurveyEntity.setResidenceCount(houseMapper.selectAllHouseByCommunityIds(communityIdList));
		// 查商铺数量
		communitySurveyEntity.setShopCount(shopLeaseService.selectAllShopByCommunityIds(communityIdList));

		// 查小区下业主数量和租户数量
		List<HouseMemberEntity> allOwner = houseMemberMapper.getAllOwnerByCommunity(adminCommunityId);
		List<HouseMemberEntity> allTenant = houseMemberMapper.getAllTenantByCommunity(adminCommunityId);
		communitySurveyEntity.setOwnerCount(allOwner.size());
		communitySurveyEntity.setTenantCount(allTenant.size());
		
		// 已占用车位
		Integer occupyCarPosition = carPositionMapper.selectAllOccupyCarPositionByCommunityIds(communityIdList);
		communitySurveyEntity.setOccupyCarPosition(occupyCarPosition);
		// 查询小区下所有车位和车辆
		List<CarPositionEntity> allCarPosition = carPositionMapper.getAllCarPositionByCommunity(adminCommunityId);
		List<CarEntity> allCar = carMapper.getAllCarByCommunity(adminCommunityId);
		communitySurveyEntity.setCarPositionCount(allCarPosition.size());
		communitySurveyEntity.setCarCount(allCar.size());
		// 未占用车位
		communitySurveyEntity.setUnoccupiedCarPosition(allCarPosition.size() - occupyCarPosition);

		// 小区月物业费收费统计
//		if (startTime != null && endTime != null) {}
		communitySurveyEntity.setDateByPropertyFee(propertyFinanceOrderMapper.chargeByDate(startTime, endTime, adminCommunityId));
		BigDecimal monthByPropertyFee = propertyFinanceOrderMapper.chargeByMonth(startTime, endTime, adminCommunityId);
		communitySurveyEntity.setMonthByPropertyFee(monthByPropertyFee);
		// 小区月车位费收费统计
//		if (startTime != null && endTime != null) {}
		communitySurveyEntity.setDateByCarPositionFee(carOrderMapper.carPositionByDate(startTime, endTime, adminCommunityId));
		BigDecimal monthByCarPositionFee = carOrderMapper.carPositionByMonth(startTime, endTime, adminCommunityId);
		communitySurveyEntity.setMonthByCarPositionFee(monthByCarPositionFee);
		// 小区总月收入
		communitySurveyEntity.setMonthByTotalFee(monthByPropertyFee.add(monthByCarPositionFee));

		return communitySurveyEntity;
	}

	/**
	 * @author: DKS
	 * @description: 获取物业控制台
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/25 13:45
	 **/
	@Override
	public ConsoleEntity getPropertySurvey(Long companyId, List<Long> communityIdList) {
		// 返回给前端实体
		ConsoleEntity consoleEntity = new ConsoleEntity();
		// 查该物业剩余短信数量
		PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectOne(new QueryWrapper<PropertyCompanyEntity>().select("message_quantity").eq("id", companyId));
		consoleEntity.setMessageQuantity(companyEntity.getMessageQuantity());
		// 查该物业公司所属小区
		List<CommunityEntity> communityEntities = communityMapper.queryCommunityByCompanyId(companyId);
		// 设置物业所属小区数量
		consoleEntity.setCommunityNumber(communityEntities.size());
		// 查住宅数量
		Integer residenceCount = houseMapper.selectAllHouseByCommunityIds(communityIdList);
		consoleEntity.setResidenceCount(residenceCount);
		// 查商铺数量
		Integer shopCount = shopLeaseService.selectAllShopByCommunityIds(communityIdList);
		consoleEntity.setShopCount(shopCount);
		// 查询物业房屋总数量
		consoleEntity.setHouseSum(residenceCount + shopCount);
		// 查业主数量
		consoleEntity.setOwnerCount(houseMemberMapper.selectAllownerByCommunityIds(communityIdList));
		// 查租户数量
		consoleEntity.setTenantCount(houseMemberMapper.selectAlltenantByCommunityIds(communityIdList));
		// 查询物业居住人数
		consoleEntity.setLiveSum(houseMemberMapper.selectAllPeopleByCommunityIds(communityIdList));
		// 已占用车位
		Integer occupyCarPosition = carPositionMapper.selectAllOccupyCarPositionByCommunityIds(communityIdList);
		consoleEntity.setOccupyCarPosition(occupyCarPosition);
		// 查询物业车位总数
		Integer carPosition = carPositionMapper.selectAllCarPositionByCommunityIds(communityIdList);
		consoleEntity.setCarPositionSum(carPosition);
		// 未占用车位
		consoleEntity.setUnoccupiedCarPosition(carPosition - occupyCarPosition);
		return consoleEntity;
	}
	
	/**
	 * @author: DKS
	 * @description: 获取物业控制台里的收费统计
	 * @param communityId:
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/31 17:09
	 **/
	@Override
	public ConsoleEntity getPropertySurveyOrderFrom(Integer year, Long communityId) {
		LocalDate startTime = null;
		LocalDate endTime = null;
		try {
			String firstMouthDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(year);
			String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(year);
			startTime = LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			endTime = LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回给前端实体
		ConsoleEntity consoleEntity = new ConsoleEntity();
		
		// 查询年每月的物业费统计
		consoleEntity.setMonthByPropertyFee(propertyFinanceOrderMapper.selectMonthPropertyFeeByCommunityId(communityId, startTime, endTime));
		// 查询年总计物业费收入统计
		BigDecimal propertyFee = propertyFinanceOrderMapper.chargeByYear(startTime, endTime, communityId);
		consoleEntity.setYearByPropertyFee(propertyFee);
		// 查询年每月的车位费统计
		consoleEntity.setMonthByCarPositionFee(carOrderMapper.selectMonthCarPositionFeeByCommunityId(communityId, startTime, endTime));
		// 查询年总计车位费收入统计
		BigDecimal carPositionFee = carOrderMapper.CarPositionFeeByYear(startTime, endTime, communityId);
		consoleEntity.setYearByCarPositionFee(carPositionFee);
		// 物业年总收入
		consoleEntity.setYearByTotalFee(propertyFee.add(carPositionFee));
		return consoleEntity;
	}
	
	/**
	 * @author: DKS
	 * @description: 物业端-系统设置-短信群发
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/30 17:22
	 **/
	@Override
	public Boolean groupSendSMS(List<Long> communityIdList, String content, boolean isDistinct, String taskTime, int number, Long companyId) {
		List<String> mobileList;
		//根据小区id查询出所有手机号
		if (!isDistinct) {
			mobileList = houseMemberMapper.selectMobileListByCommunityIds(communityIdList);
		} else {
			mobileList = houseMemberMapper.selectDistinctMobileListByCommunityIds(communityIdList);
		}
		// 查出短信剩余数量是否充足
		PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectOne(new QueryWrapper<PropertyCompanyEntity>().select("message_quantity").eq("id", companyId));
		if (companyEntity.getMessageQuantity() < mobileList.size()) {
			throw new PropertyException(JSYError.NOT_ENOUGH.getCode(),"短信余额不足！");
		}
		if (taskTime == null) {
			for (String mobile : mobileList) {
//			SmsUtil.propertyNotice("15095880991","123456789");
				System.out.println(mobile);
			}
			SmsUtil.propertyNotice("15095880991","123456789");
			propertyCompanyMapper.updateSMSQuantity(number * mobileList.size(), companyId);
		} else {
			try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time = sdf.parse(taskTime);
			Timer timer = new Timer();
			timer.schedule(new NowTask(timer, mobileList, content), time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	class NowTask extends TimerTask{
		private Timer timer;
		private List<String> mobile;
		private String content;
		/*
		 * 构造器
		 */
		public NowTask(){
		
		}
		public NowTask(List<String> mobile){
			this.mobile = mobile;
		}
		public NowTask(Timer timer){
			this.timer =timer;
		}
		public NowTask(Timer timer , List<String> mobile, String content){
			this.timer = timer;
			this.mobile = mobile;
			this.content = content;
		}
		// 属性的get、set方法
		public Timer getTimer() {
			return timer;
		}
		public  void setTimer(Timer timer) {
			this.timer = timer;
		}
		public List<String> getMobile() {
			return mobile;
		}
		public void setMobile(List<String> mobile) {
			this.mobile = mobile;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
		@Override
		public void run(){
			// 这里写需要定时执行的方法
			for (String s : mobile) {
//				SmsUtil.propertyNotice("15095880991","123456789");
				System.out.println(s);
			}
//			propertyCompanyMapper.updateSMSQuantity(number * mobile.size(), companyId);
			System.out.println(content);
			timer.cancel(); // 传递timer进来就是为了在方法执行完后退出,必须退出
			System.out.println("结束");
		}
	}
}
