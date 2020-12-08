package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IVisitingCarService;
import com.jsy.community.api.IVisitorPersonService;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitingCarEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorPersonEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitingCarQO;
import com.jsy.community.qo.proprietor.VisitorPersonQO;
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
	private IVisitorService iTVisitorService;
	
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
	@Login
	public CommonResult save(@RequestBody VisitorEntity visitorEntity) {
		ValidatorUtils.validateEntity(visitorEntity);
		visitorEntity.setUid(UserUtils.getUserId());
		//添加访客
		Long visitorId = iTVisitorService.addVisitor(visitorEntity);
		if (visitorId == null) {
			throw new JSYException(JSYError.INTERNAL.getCode(), "新增访客登记失败");
		}
		//添加随行人员
		List<VisitorPersonEntity> personList = visitorEntity.getVisitorPersonList();
		if (!CollectionUtils.isEmpty(personList)) {
			for (VisitorPersonEntity person : personList) {
				person.setVisitorId(visitorId);
			}
			boolean saveVisitorPerson = iTVisitorService.addPersonBatch(personList);
			if(!saveVisitorPerson){
				throw new JSYException(JSYError.INTERNAL.getCode(), "新增访客登记失败");
			}
		}
		//添加随行车辆
		List<VisitingCarEntity> carList = visitorEntity.getVisitingCarList();
		if (!CollectionUtils.isEmpty(carList)) {
			for (VisitingCarEntity car : carList) {
				car.setVisitorId(visitorId);
			}
			boolean saveVisitingCar = iTVisitorService.addCarBatch(carList);
			if(!saveVisitingCar){
				throw new JSYException(JSYError.INTERNAL.getCode(), "新增访客登记失败");
			}
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
		boolean delResult = iTVisitorService.deleteVisitorById(id);
		if (delResult) {
			//关联删除
			iTVisitorService.deletePersonAndCar(id);
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
		return CommonResult.ok(iTVisitorService.queryByPage(baseQO));
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
		VisitorEntity visitorEntity = iTVisitorService.selectOneById(id);
		if (visitorEntity == null) {
			return CommonResult.ok(null);
		}
		List<VisitorPersonEntity> personList = iVisitorPersonService.queryPersonList(visitorEntity.getId());
		List<VisitingCarEntity> carList = iVisitingCarService.queryCarList(visitorEntity.getId());
		visitorEntity.setVisitorPersonList(personList);
		visitorEntity.setVisitingCarList(carList);
		return CommonResult.ok(visitorEntity);
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
		boolean updateResult = iTVisitorService.updateVisitorPersonById(visitorPersonQO);
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
	@DeleteMapping("person")
	public CommonResult deletePerson(@RequestParam("id") Long id) {
		boolean result = iTVisitorService.deleteVisitorPersonById(id);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "访客登记-随行人员 删除失败");
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
		boolean updateResult = iTVisitorService.updateVisitingCarById(visitingCarQO);
		return updateResult ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "访客登记-随行车辆 修改失败");
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
		boolean result = iTVisitorService.deleteVisitingCarById(id);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "访客登记-随行车辆 删除失败");
	}
	
}
