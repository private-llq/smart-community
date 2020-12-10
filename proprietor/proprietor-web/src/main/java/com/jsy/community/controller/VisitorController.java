package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IVisitingCarService;
import com.jsy.community.api.IVisitorPersonService;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.qo.proprietor.VisitingCarQO;
import com.jsy.community.qo.proprietor.VisitorPersonQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.qo.proprietor.VisitorQO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chq459799974
 * @since 2020-11-18 16:19
 **/
@RequestMapping("visitor")
@Api(tags = "访客控制器")
@RestController
@Login
@ApiJSYController
public class VisitorController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IVisitorService iVisitorService;
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IVisitorPersonService iVisitorPersonService;
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IVisitingCarService iVisitingCarService;
	
	/**
	 * @Description: 访客登记 新增
	 * @Param: [visitorEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/11
	 **/
	@ApiOperation("【访客】新增")
	@Transactional(rollbackFor = Exception.class)
	@PostMapping("")
	public CommonResult save(@RequestBody VisitorEntity visitorEntity) {
		ValidatorUtils.validateEntity(visitorEntity);
		visitorEntity.setUid(UserUtils.getUserId());
		//雪花算法生成ID
		long visitorId = SnowFlake.nextId();
		visitorEntity.setId(visitorId);
		//添加访客
		iVisitorService.addVisitor(visitorEntity);
		//添加随行人员
		List<VisitorPersonRecordEntity> personRecordList = visitorEntity.getVisitorPersonRecordList();
		if (!CollectionUtils.isEmpty(personRecordList)) {
			for (VisitorPersonRecordEntity personRecord : personRecordList) {
				personRecord.setVisitorId(visitorId);
				personRecord.setId(SnowFlake.nextId());
			}
			iVisitorService.addPersonBatch(personRecordList);
		}
		//添加随行车辆
		List<VisitingCarRecordEntity> carRecordList = visitorEntity.getVisitingCarRecordList();
		if (!CollectionUtils.isEmpty(carRecordList)) {
			for (VisitingCarRecordEntity carRecord : carRecordList) {
				carRecord.setVisitorId(visitorId);
				carRecord.setId(SnowFlake.nextId());
			}
			iVisitorService.addCarBatch(carRecordList);
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
	@DeleteMapping("")
	public CommonResult delete(@RequestParam("id") Long id) {
		boolean delResult = iVisitorService.deleteVisitorById(id);
		if (delResult) {
			//关联删除
			iVisitorService.deletePersonAndCar(id);
		}
		return delResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "删除失败");
	}
	
	/**
	* @Description: 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
	 * @Author: chq459799974
	 * @Date: 2020/11/18
	**/
	@ApiOperation("【访客】分页查询")
	@PostMapping("page")
	public CommonResult<Page> query(@RequestBody BaseQO<VisitorQO> baseQO) {
		return CommonResult.ok(iVisitorService.queryByPage(baseQO));
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
	public CommonResult<VisitorEntity> queryById(@RequestParam("id") Long id) {
		VisitorEntity visitorEntity = iVisitorService.selectOneById(id);
		if (visitorEntity == null) {
			return CommonResult.ok(null);
		}
		visitorEntity.setVisitorPersonRecordList(iVisitorService.queryPersonRecordList(visitorEntity.getId()));
		visitorEntity.setVisitingCarRecordList(iVisitorService.queryCarRecordList(visitorEntity.getId()));
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
	public CommonResult addPerson(@RequestBody VisitorPersonEntity visitorPersonEntity) {
		ValidatorUtils.validateEntity(visitorPersonEntity,VisitorPersonEntity.addPersonValidatedGroup.class);
		visitorPersonEntity.setUid(UserUtils.getUserId());
		visitorPersonEntity.setId(SnowFlake.nextId());
		boolean result = iVisitorPersonService.addVisitorPerson(visitorPersonEntity);
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
	public CommonResult updatePerson(@RequestBody VisitorPersonQO visitorPersonQO) {
		ValidatorUtils.validateEntity(visitorPersonQO);
		boolean updateResult = iVisitorPersonService.updateVisitorPersonById(visitorPersonQO);
		return updateResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行人员 修改失败");
	}
	
	/**
	 * @Description: 删除随行人员
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@ApiOperation("【随行人员】删除")
	@DeleteMapping("person")
	public CommonResult deletePerson(@RequestParam("id") Long id) {
		boolean result = iVisitorPersonService.deleteVisitorPersonById(id);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行人员 删除失败");
	}
	
	/**
	* @Description: 随行人员 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	**/
	@ApiOperation("【随行人员】分页查询")
	@PostMapping("person/page")
	public CommonResult queryPersonPage(@RequestBody BaseQO<String> baseQO){
		baseQO.setQuery(UserUtils.getUserId());
		return CommonResult.ok(iVisitorPersonService.queryVisitorPersonPage(baseQO));
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
	public CommonResult addCar(@RequestBody VisitingCarEntity visitingCarEntity) {
		ValidatorUtils.validateEntity(visitingCarEntity,VisitingCarEntity.addCarValidatedGroup.class);
		visitingCarEntity.setUid(UserUtils.getUserId());
		visitingCarEntity.setId(SnowFlake.nextId());
		boolean result = iVisitingCarService.addVisitingCar(visitingCarEntity);
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
	public CommonResult updateCar(@RequestBody VisitingCarQO visitingCarQO) {
		ValidatorUtils.validateEntity(visitingCarQO);
		boolean updateResult = iVisitingCarService.updateVisitingCarById(visitingCarQO);
		return updateResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行车辆 修改失败");
	}
	
	/**
	 * @Description: 删除随行车辆
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@ApiOperation("【随行车辆】删除")
	@DeleteMapping("car")
	public CommonResult deleteCar(@RequestParam("id") Long id) {
		boolean result = iVisitingCarService.deleteVisitingCarById(id);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "随行车辆 删除失败");
	}
	
	/**
	 * @Description: 随行车辆 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	@ApiOperation("【随行车辆】分页查询")
	@PostMapping("car/page")
	public CommonResult queryCarPage(@RequestBody BaseQO<String> baseQO){
		baseQO.setQuery(UserUtils.getUserId());
		return CommonResult.ok(iVisitingCarService.queryVisitingCarPage(baseQO));
	}
	
}
