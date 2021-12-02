package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IVisitingCarService;
import com.jsy.community.api.IVisitorPersonService;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitingCarEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorPersonEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitingCarQO;
import com.jsy.community.qo.proprietor.VisitorPersonQO;
import com.jsy.community.qo.proprietor.VisitorQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.VisitorEntryVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author chq459799974
 * @since 2020-11-18 16:19
 **/
@RequestMapping("visitor")
@Api(tags = "访客控制器")
@RestController
// @ApiJSYController
public class VisitorController {
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IVisitorService visitorService;
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IVisitorPersonService visitorPersonService;
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IVisitingCarService visitingCarService;
	
//	/**
//	* @Description: 【访客】验证二维码
//	 * @Param: [jsonObject]
//	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
//	 * @Author: chq459799974
//	 * @Date: 2021/5/6
//	**/
//	@Deprecated
//	@ApiOperation("【访客】验证二维码")
//	@PostMapping("/QRCode/verify")
//	@Login(allowAnonymous = true)
//	public Map<String,Object> verifyQRCode(@RequestBody JSONObject jsonObject){
//		return visitorService.verifyQRCode(jsonObject,jsonObject.getInteger("hardwareType"));
//	}
	
	/**
	 * @Description: 访客登记 新增
	 * @Param: [visitorEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/11
	 **/
	@ApiOperation("【访客】新增")
	@PostMapping("")
	@Permit("community:proprietor:visitor")
	public CommonResult<VisitorEntryVO> save(@RequestBody VisitorEntity visitorEntity) {
		ValidatorUtils.validateEntity(visitorEntity);
		if(LocalDate.now().isAfter(visitorEntity.getStartTime().toLocalDate())){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "预计来访时间不得早于当前时间");
		}
		if(!StringUtils.isEmpty(visitorEntity.getCarPlate())){
			if(!visitorEntity.getCarPlate().matches(BusinessConst.REGEX_OF_CAR) && !visitorEntity.getCarPlate().matches(BusinessConst.REGEX_OF_NEW_ENERGY_CAR)){
				throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "车牌号不合法");
			}
		}
		// 有来访车辆,如果没有选择代缴状态,默认为不代缴
		if (!StringUtils.isEmpty(visitorEntity.getCarPlate()) && visitorEntity.getCarAlternativePaymentStatus() == null) {
			visitorEntity.setCarAlternativePaymentStatus(0);
		}
		visitorEntity.setUid(UserUtils.getUserId());
		visitorEntity.setTempCodeStatus(0);
		return CommonResult.ok(visitorService.appAddVisitor(visitorEntity),"操作成功");
	}


	/*@ApiOperation("【访客】新增")
	@PostMapping("v2")
	public CommonResult<VisitorEntryVO> saveV2(@RequestBody VisitorEntity visitorEntity) {
		ValidatorUtils.validateEntity(visitorEntity);
		if(LocalDateTime.now().isAfter(visitorEntity.getStartTime())){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "预计来访时间不得早于当前时间");
		}
		if(!StringUtils.isEmpty(visitorEntity.getCarPlate())){
			if(!visitorEntity.getCarPlate().matches(BusinessConst.REGEX_OF_CAR) && !visitorEntity.getCarPlate().matches(BusinessConst.REGEX_OF_NEW_ENERGY_CAR)){
				throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "车牌号不合法");
			}
		}
		visitorEntity.setUid(UserUtils.getUserId());
		return CommonResult.ok(visitorService.addVisitorV2(visitorEntity),"操作成功");
	}*/

	/**
	 * @Description: 根据ID单查
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.entity.VisitorEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/18
	 **/
	@ApiOperation("【访客】根据ID单查详情")
	@GetMapping("v2")
	@Permit("community:proprietor:visitor:v2")
	public CommonResult<VisitorEntity> queryByIdv2(@RequestParam("id") Long id) {
		VisitorEntity visitorEntity = visitorService.selectOneByIdv2(id);
		if (visitorEntity == null) {
			return CommonResult.ok(null);
		}
//		visitorEntity.setVisitorPersonRecordList(visitorService.queryPersonRecordList(visitorEntity.getId()));
//		visitorEntity.setVisitingCarRecordList(visitorService.queryCarRecordList(visitorEntity.getId()));
		return CommonResult.ok(visitorEntity);
	}


//	//三方调用进来
//	@ApiOperation("【访客】社区门禁验证(模拟)")
//	@GetMapping("verifyCommunityEntry")
//	@Login(allowAnonymous = true)
//	//TODO 流程未知，参数和返回值待确定
//	public boolean verifyCommunityEntry(@RequestParam("entryToken") String token){
//		return iVisitorService.verifyEntry(token, BusinessEnum.EntryTypeEnum.COMMUNITY.getCode());
//	}
//
//	//三方调用进来
//	@ApiOperation("【访客】楼栋门禁验证(模拟)")
//	@GetMapping("verifyBuildingEntry")
//	@Login(allowAnonymous = true)
//	//TODO 流程未知，参数和返回值待确定
//	public boolean verifyBuildingEntry(@RequestParam("entryToken") String token){
//		return iVisitorService.verifyEntry(token,BusinessEnum.EntryTypeEnum.BUILDING.getCode());
//	}
	
	/**
	 * @Description: 访客登记 逻辑删除
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	 **/
	@ApiOperation("【访客】删除")
	@DeleteMapping("")
	@Permit("community:proprietor:visitor")
	public CommonResult delete(@RequestParam("id") Long id) {
		boolean delResult = visitorService.deleteVisitorById(id);
		return delResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "删除失败");
	}
	
	/**
	* @Description: 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo>
	 * @Author: chq459799974
	 * @Date: 2020/11/18
	**/
	@ApiOperation("【访客】分页查询")
	@PostMapping("page")
	@Permit("community:proprietor:visitor:page")
	public CommonResult<PageInfo> query(@RequestBody BaseQO<VisitorQO> baseQO) {
		return CommonResult.ok(visitorService.queryByPage(baseQO,UserUtils.getUserId()));
	}
	
	/**
	* @Description: 根据ID单查
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.entity.VisitorEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/18
	**/
	@ApiOperation("【访客】根据ID单查详情")
	@GetMapping("")
	@Permit("community:proprietor:visitor")
	public CommonResult<VisitorEntity> queryById(@RequestParam("id") Long id) {
		VisitorEntity visitorEntity = visitorService.selectOneById(id);
		if (visitorEntity == null) {
			return CommonResult.ok(null);
		}
		visitorEntity.setVisitorPersonRecordList(visitorService.queryPersonRecordList(visitorEntity.getId()));
		visitorEntity.setVisitingCarRecordList(visitorService.queryCarRecordList(visitorEntity.getId()));
		return CommonResult.ok(visitorEntity);
	}
	
	/**
	 * @Description: 添加随行人员
	 * @Param: [visitorPersonEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	@ApiOperation("【随行人员】添加")
	@PostMapping("person")
	@Permit("community:proprietor:visitor:person")
	public CommonResult addPerson(@RequestBody VisitorPersonEntity visitorPersonEntity) {
		ValidatorUtils.validateEntity(visitorPersonEntity,VisitorPersonEntity.addPersonValidatedGroup.class);
		visitorPersonEntity.setUid(UserUtils.getUserId());
		visitorPersonEntity.setId(SnowFlake.nextId());
		boolean result = visitorPersonService.addVisitorPerson(visitorPersonEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行人员 添加失败");
	}
	
	/**
	 * @Description: 修改随行人员
	 * @Param: [visitorPersonQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	 **/
	@ApiOperation("【随行人员】修改")
	@PutMapping("person")
	@Permit("community:proprietor:visitor:person")
	public CommonResult updatePerson(@RequestBody VisitorPersonQO visitorPersonQO) {
		ValidatorUtils.validateEntity(visitorPersonQO);
		boolean updateResult = visitorPersonService.updateVisitorPersonById(visitorPersonQO);
		return updateResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行人员 修改失败");
	}
	
	/**
	 * @Description: 批量删除随行人员
	 * @Param: [ids]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@ApiOperation("【随行人员】批量删除")
	@DeleteMapping("person")
	@Permit("community:proprietor:visitor:person")
	public CommonResult deletePerson(@RequestBody List<Long> ids) {
		boolean result = visitorPersonService.deleteVisitorPersonById(ids);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行人员 批量删除失败");
	}
	
	/**
	* @Description: 随行人员 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo>
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	**/
	@ApiOperation("【随行人员】分页查询")
	@PostMapping("person/page")
	@Permit("community:proprietor:visitor:person:page")
	public CommonResult<PageInfo<VisitorPersonEntity>> queryPersonPage(@RequestBody BaseQO<String> baseQO){
		baseQO.setQuery(UserUtils.getUserId());
		return CommonResult.ok(visitorPersonService.queryVisitorPersonPage(baseQO));
	}
	
	/**
	 * @Description: 添加随行车辆
	 * @Param: [visitingCarEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	@ApiOperation("【随行车辆】添加")
	@PostMapping("car")
	@Permit("community:proprietor:visitor:car")
	public CommonResult addCar(@RequestBody VisitingCarEntity visitingCarEntity) {
		ValidatorUtils.validateEntity(visitingCarEntity,VisitingCarEntity.addCarValidatedGroup.class);
		visitingCarEntity.setUid(UserUtils.getUserId());
		visitingCarEntity.setId(SnowFlake.nextId());
		boolean result = visitingCarService.addVisitingCar(visitingCarEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行车辆 添加失败");
	}
	
	/**
	 * @Description: 修改随行车辆
	 * @Param: [visitingCarQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	 **/
	@ApiOperation("【随行车辆】修改")
	@PutMapping("car")
	@Permit("community:proprietor:visitor:car")
	public CommonResult updateCar(@RequestBody VisitingCarQO visitingCarQO) {
		ValidatorUtils.validateEntity(visitingCarQO);
		boolean updateResult = visitingCarService.updateVisitingCarById(visitingCarQO);
		return updateResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行车辆 修改失败");
	}
	
	/**
	 * @Description: 批量删除随行车辆
	 * @Param: [ids]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@ApiOperation("【随行车辆】批量删除")
	@DeleteMapping("car")
	@Permit("community:proprietor:visitor:car")
	public CommonResult deleteCar(@RequestBody List<Long> ids) {
		boolean result = visitingCarService.deleteVisitingCarById(ids);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行车辆 批量删除失败");
	}
	
	/**
	 * @Description: 随行车辆 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo>
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	@ApiOperation("【随行车辆】分页查询")
	@PostMapping("car/page")
	@Permit("community:proprietor:visitor:car:page")
	public CommonResult<PageInfo<VisitingCarEntity>> queryCarPage(@RequestBody BaseQO<String> baseQO){
		baseQO.setQuery(UserUtils.getUserId());
		return CommonResult.ok(visitingCarService.queryVisitingCarPage(baseQO));
	}

	/**
	 * @author: Pipi
	 * @description: 查询邀请过的车辆列表
	 * @param visitorEntity: 查询条件
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/10/26 11:33
	 **/
	@PostMapping("/v2/visitorCarHistory")
	@Permit("community:proprietor:visitor:v2:visitorCarHistory")
	public CommonResult queryVisitorCar(@RequestBody VisitorEntity visitorEntity) {
		ValidatorUtils.validateEntity(visitorEntity, VisitorEntity.queryVisitorCarValidate.class);
		visitorEntity.setUid(UserUtils.getUserId());
		return CommonResult.ok(visitorService.queryVisitorCar(visitorEntity));
	}

	/**
	 * @author: Pipi
	 * @description: 生成临时通行二维码
	 * @param visitorEntity:
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/10/26 16:19
	 **/
	@PostMapping("/v2/addTempCode")
	@Permit("community:proprietor:visitor:v2:addTempCode")
	public CommonResult addTempCode(@RequestBody VisitorEntity visitorEntity) {
		ValidatorUtils.validateEntity(visitorEntity, VisitorEntity.addTempCodeValidate.class);
		visitorEntity.setUid(UserUtils.getUserId());
		visitorEntity.setTempCodeStatus(1);
		visitorEntity.setIsCommunityAccess(BusinessEnum.CommunityAccessEnum.QR_CODE.getCode());
		return CommonResult.ok(visitorService.appAddVisitor(visitorEntity),"操作成功");
	}

}
