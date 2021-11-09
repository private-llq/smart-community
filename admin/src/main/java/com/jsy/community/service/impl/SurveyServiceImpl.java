package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.*;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.service.ISurveyService;
import com.jsy.community.vo.sys.SurveyVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 大后台 概况实现类
 *
 * @author DKS
 * @since 2021-11-09
 */
@Service
public class SurveyServiceImpl extends ServiceImpl<SurveyMapper, SurveyVo> implements ISurveyService {
	
	@Resource
	private CommunityMapper communityMapper;
	
	@Resource
	private HouseMemberMapper houseMemberMapper;

	@Resource
	private HouseMapper houseMapper;

	@Resource
	private CarMapper carMapper;

	@Resource
	private CarPositionMapper carPositionMapper;
	
	@Resource
	private PropertyCompanyMapper propertyCompanyMapper;
	
	@Resource
	private PeopleHistoryMapper peopleHistoryMapper;
	
	@Resource
	private CarCutOffMapper carCutOffMapper;
	
	@Resource
	private CommunityHardWareMapper communityHardWareMapper;
	
	@Resource
	private ProprietorMarketMapper proprietorMarketMapper;
	
	@Resource
	private HouseLeaseMapper houseLeaseMapper;
	
	@Resource
	private RepairMapper repairMapper;
	
	@Resource
	private PropertyFinanceOrderMapper propertyFinanceOrderMapper;
	
	@Resource
	private RepairOrderMapper repairOrderMapper;
	
	/**
	 * @Description: 获取大后台概况
	 * @author: DKS
	 * @since: 2021/11/9 10:58
	 * @Param: []
	 * @return: com.jsy.community.vo.sys.SurveyVo
	 */
	@Override
	public SurveyVo getSurvey() {
		// 返回给前端实体
		SurveyVo surveyVo = new SurveyVo();
		
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
		surveyVo.setOpenDoorCount(peopleHistoryMapper.selectOpenDoorCount(beginTime, overTime));
		// 查访客次数
		surveyVo.setVisitorCount(peopleHistoryMapper.selectVisitorCount(beginTime, overTime));
		// 查车辆进入(临时停车)
		surveyVo.setCarInfoCount(carCutOffMapper.selectCarIntoCount(beginTime, overTime));
		// 查交易额
		BigDecimal financeTurnover = propertyFinanceOrderMapper.financeTurnover(beginTime, overTime);
		BigDecimal repairTurnover = repairOrderMapper.repairTurnover(beginTime, overTime);
		if (financeTurnover == null && repairTurnover == null) {
			surveyVo.setTurnover(new BigDecimal("0.00"));
		} else if (financeTurnover == null) {
			surveyVo.setTurnover(repairTurnover);
		} else if (repairTurnover == null) {
			surveyVo.setTurnover(financeTurnover);
		} else {
			surveyVo.setTurnover(financeTurnover.add(repairTurnover));
		}
		// 查物业公司数量
		surveyVo.setCompanyCount(propertyCompanyMapper.selectCount(new QueryWrapper<PropertyCompanyEntity>().eq("deleted", 0)));
		// 查小区数量
		surveyVo.setCommunityCount(communityMapper.selectCount(new QueryWrapper<CommunityEntity>().eq("deleted", 0)));
		// 查所有楼栋总数和房屋总数
		surveyVo.setBuildingSum(houseMapper.selectCount(new QueryWrapper<HouseEntity>().eq("type", 1).eq("deleted", 0)));
		surveyVo.setHouseSum(houseMapper.selectCount(new QueryWrapper<HouseEntity>().eq("type", 4).eq("deleted", 0)));
		// 查住户数量
		surveyVo.setHouseholdCount(houseMemberMapper.selectCount(new QueryWrapper<HouseMemberEntity>().eq("status", 1).eq("deleted", 0)));
		// 查车辆数量和车位数量
		surveyVo.setCarCount(carMapper.selectCount(new QueryWrapper<CarEntity>().eq("deleted" ,0)));
		surveyVo.setCarPositionCount(carPositionMapper.selectCount(new QueryWrapper<CarPositionEntity>().eq("deleted", 0)));
		// 查门禁数量
		surveyVo.setCommunityHardWareCount(communityHardWareMapper.selectCount(new QueryWrapper<CommunityHardWareEntity>().eq("deleted", 0)));
		// 查集市数量
		surveyVo.setMarketCount(proprietorMarketMapper.selectCount(new QueryWrapper<ProprietorMarketEntity>().eq("state", 1)
		.eq("deleted", 0).eq("shield", 0).eq("remove", 0)));
		// 查房屋租赁数量
		surveyVo.setHouseLeaseCount(houseLeaseMapper.selectCount(new QueryWrapper<HouseLeaseEntity>().eq("deleted", 0)));
		// 查报修数量
		surveyVo.setRepairCount(repairMapper.selectCount(new QueryWrapper<RepairEntity>().eq("deleted", 0)));
		return surveyVo;
	}
}
