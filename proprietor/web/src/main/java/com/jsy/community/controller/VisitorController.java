package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IRegionService;
import com.jsy.community.api.IVisitingCarService;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.api.IVisitorPersonService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitingCarEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorPersonEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitorQO;
import com.jsy.community.utils.JwtUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 访客控制器
 * @Param:
 * @Return:
 * @Author: chq459799974
 * @Date: 2020/11/17
 **/
@RequestMapping("visitor")
@Api(tags = "访客控制器")
@RestController
@Login
public class VisitorController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IVisitorService iTVisitorService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IVisitorPersonService iVisitorPersonService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IVisitingCarService iVisitingCarService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IRegionService iRegionService;
	
	/**
	 * @Description: 访客登记 新增
	 * @Param: [tVisitor]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/11
	 **/
	@ApiOperation("【访客】新增")
	@Transactional(rollbackFor = Exception.class)
	@PostMapping("")
	public CommonResult save(@RequestBody VisitorEntity visitorEntity) {
		visitorEntity.setUid(JwtUtils.getUserId());
		ValidatorUtils.validateEntity(visitorEntity, VisitorEntity.addVisitorValidatedGroup.class);
		//添加访客
		Long visitorId = iTVisitorService.addVisitor(visitorEntity);
		if (visitorId == null) {
			throw new JSYException(JSYError.INTERNAL.getCode(), "新增访客登记失败");
		}
		//添加随行人员
		List<VisitorPersonEntity> personList = visitorEntity.getVisitorPersonList();
		if (!CollectionUtils.isEmpty(personList)) {
			//TODO 测试是否可以验证List类型 否
			ValidatorUtils.validateEntity(personList, VisitorPersonEntity.addPersonValidatedGroup.class);
			for (VisitorPersonEntity person : personList) {
				person.setVisitorId(visitorId);
			}
		}
		boolean saveVisitorPerson = iTVisitorService.addPersonBatch(personList);
		if(!saveVisitorPerson){
			throw new JSYException(JSYError.INTERNAL.getCode(), "新增访客登记失败");
		}
		//添加随行车辆
		List<VisitingCarEntity> carList = visitorEntity.getVisitingCarList();
		if (!CollectionUtils.isEmpty(carList)) {
			//TODO 测试是否可以验证List类型 否
			ValidatorUtils.validateEntity(carList, VisitingCarEntity.addCarValidatedGroup.class);
			for (VisitingCarEntity car : carList) {
				car.setVisitorId(visitorId);
			}
		}
		boolean saveVisitingCar = iTVisitorService.addCarBatch(carList);
		if(!saveVisitingCar){
			throw new JSYException(JSYError.INTERNAL.getCode(), "新增访客登记失败");
		}
		return CommonResult.ok();
	}
	
	/**
	 * @Description: 访客登记 逻辑删除
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	 **/
	@ApiOperation("【访客】删除")
	@Transactional(rollbackFor = Exception.class)
	@DeleteMapping("{id}")
	public CommonResult delete(@PathVariable("id") Long id) {
		boolean delResult = iTVisitorService.deleteVisitorById(id);
		if (delResult) {
			//关联删除
			iTVisitorService.deletePersonAndCar(id);
		}
		return delResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "删除失败");
	}
	
	/**
	 * @Description: 访客登记 修改
	 * @Param: [visitorEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	 **/
	@ApiOperation("【访客】修改")
	@PutMapping("")
	public CommonResult update(@RequestBody VisitorEntity visitorEntity) {
		setNull(visitorEntity);
		boolean updateResult = iTVisitorService.updateVisitorById(visitorEntity);
		return updateResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "访客登记申请 修改失败");
	}
	
	/**
	 * 设置不能修改的字段
	 */
	private void setNull(VisitorEntity visitorEntity) {
		visitorEntity.setCommunityId(null);
		visitorEntity.setUid(null);
		visitorEntity.setIsCommunityAccess(null);
		visitorEntity.setIsBuildingAccess(null);
		visitorEntity.setCheckType(null);
		visitorEntity.setCheckStatus(null);
		visitorEntity.setCheckTime(null);
		visitorEntity.setRefuseReason(null);
		visitorEntity.setDeleted(null);
		visitorEntity.setCreateTime(null);
	}
	
	/**
	 * @Description: 分页查询
	 * @Param: [baseQO<VisitorQO>]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	 **/
	@ApiOperation("【访客】分页查询")
	@PostMapping("page")
	public CommonResult<Page> query(@RequestBody BaseQO<VisitorQO> baseQO) {
		return CommonResult.ok(iTVisitorService.queryByPage(baseQO));
	}
	
	/**
	 * @Description: 根据ID单查
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	 **/
	@ApiOperation("【访客】根据ID单查详情")
	@GetMapping("{id}")
	public CommonResult queryById(@PathVariable("id") Long id) {
		VisitorEntity visitorEntity = iTVisitorService.selectOneById(id);
		if (visitorEntity == null) {
			return CommonResult.ok("没有数据");
		}
		List<VisitorPersonEntity> personList = iVisitorPersonService.queryPersonList(visitorEntity.getId());
		List<VisitingCarEntity> carList = iVisitingCarService.queryCarList(visitorEntity.getId());
		visitorEntity.setVisitorPersonList(personList);
		visitorEntity.setVisitingCarList(carList);
		return CommonResult.ok(visitorEntity);
	}
	
	/**
	 * @Description: 修改随行人员
	 * @Param: [visitorPersonEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	 **/
	@ApiOperation("【随行人员】修改")
	@PutMapping("person")
	public CommonResult updatePerson(@RequestBody VisitorPersonEntity visitorPersonEntity) {
		ValidatorUtils.validateEntity(visitorPersonEntity, VisitingCarEntity.updateCarValidatedGroup.class);
		visitorPersonEntity.setVisitorId(null);
		boolean updateResult = iTVisitorService.updateVisitorPersonById(visitorPersonEntity);
		return updateResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "访客登记-随行人员 修改失败");
	}
	
	/**
	 * @Description: 删除随行人员
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@ApiOperation("【随行人员】删除")
	@DeleteMapping("person/{id}")
	public CommonResult deletePerson(@PathVariable("id") Long id) {
		boolean result = iTVisitorService.deleteVisitorPersonById(id);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), JSYError.INTERNAL.getMessage());
	}
	
	/**
	 * @Description: 修改随行车辆
	 * @Param: [visitingCarEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	 **/
	@ApiOperation("【随行车辆】修改")
	@PutMapping("car")
	public CommonResult updateCar(@RequestBody VisitingCarEntity visitingCarEntity) {
		ValidatorUtils.validateEntity(visitingCarEntity, VisitingCarEntity.updateCarValidatedGroup.class);
		visitingCarEntity.setVisitorId(null);
		boolean updateResult = iTVisitorService.updateVisitingCarById(visitingCarEntity);
		return updateResult ? CommonResult.ok("") : CommonResult.error(JSYError.INTERNAL.getCode(), "访客登记-随行车辆 修改失败");
	}
	
	/**
	 * @Description: 删除随行车辆
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@ApiOperation("【随行车辆】删除")
	@DeleteMapping("car/{id}")
	public CommonResult deleteCar(@PathVariable("id") Long id) {
		boolean result = iTVisitorService.deleteVisitingCarById(id);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), JSYError.INTERNAL.getMessage());
	}
	
	
}
