package com.jsy.community.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseBuildingTypeEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseBuildingTypeQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.util.excel.impl.HouseExcelHandlerImpl;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.BuildingImportErrorVO;
import com.jsy.community.vo.property.HouseImportErrorVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <p>
 * 社区楼栋 前端控制器
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-20
 */
@Api(tags = "楼栋控制器")
@RestController
@RequestMapping("house")
// @ApiJSYController
public class HouseController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IHouseService houseService;

	@Autowired
	private HouseExcelHandlerImpl houseExcelHandler;

	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IProprietorService iProprietorService;
	
	// 查询子级，后期若要封装层级，HouseQO加查询类型
//	@ApiOperation("【楼栋】查询子级楼栋")
//	@PostMapping("page/sub")
//	public CommonResult<PageInfo<HouseEntity>> queryHousePage(@RequestBody BaseQO<HouseQO> baseQO){
//		return CommonResult.ok(iHouseService.queryHousePage(baseQO));
//	}

//	@ApiOperation("【楼栋】新增楼栋信息")
//	@PostMapping("")
//	public CommonResult addHouse(@RequestBody HouseEntity houseEntity){
//		ValidatorUtils.validateEntity(houseEntity, HouseEntity.addHouseValidatedGroup.class);
//		boolean result = iHouseService.addHouse(houseEntity);
//		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增楼栋信息失败");
//	}

//	@ApiOperation("【楼栋】修改楼栋信息")
//	@PutMapping("")
//	public CommonResult updateHouse(@RequestBody HouseEntity houseEntity){
//		ValidatorUtils.validateEntity(houseEntity, HouseEntity.updateHouseValidatedGroup.class);
//		boolean result = iHouseService.updateHouse(houseEntity);
//		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改楼栋信息失败");
//	}
	
//	@ApiOperation("【楼栋】删除楼栋信息")
//	@DeleteMapping("")
//	public CommonResult deleteHouse(@RequestParam("id") Long id){
//		boolean result = iHouseService.deleteHouse(id);
//		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"删除楼栋信息失败");
//	}
	
	// ============================================ 物业端产品原型确定后新加的 开始  ===========================================================
	
	/**
	* @Description: 【楼宇房屋】新增楼栋、单元、房屋
	 * @Param: [houseEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	@ApiOperation("【楼宇房屋】新增楼栋、单元、房屋")
	@PostMapping("/add")
	@businessLog(operation = "新增",content = "新增了【楼宇房屋】")
	@Permit("community:property:house:add")
	public CommonResult addHouse(@RequestBody HouseEntity houseEntity){
		if(houseEntity.getType() == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少类型参数");
		}
		if(BusinessConst.BUILDING_TYPE_DOOR == houseEntity.getType()){
			ValidatorUtils.validateEntity(houseEntity,HouseEntity.addRoomValidatedGroup.class);
		}else if (BusinessConst.BUILDING_TYPE_BUILDING == houseEntity.getType()){
			ValidatorUtils.validateEntity(houseEntity,HouseEntity.addBuildingGroup.class);
		}else if (BusinessConst.BUILDING_TYPE_UNIT == houseEntity.getType()){
			ValidatorUtils.validateEntity(houseEntity,HouseEntity.addUnitGroup.class);
		}
		AdminInfoVo loginUser = UserUtils.getAdminInfo();
		houseEntity.setCommunityId(loginUser.getCommunityId());
		houseEntity.setCreateBy(loginUser.getUid());
		boolean result = houseService.addHouse(houseEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增楼栋信息失败");
	}
	
	/**
	* @Description: 【楼宇房屋】修改
	 * @Param: [houseEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	@ApiOperation("【楼宇房屋】修改")
	@PutMapping("/update")
	@businessLog(operation = "编辑",content = "更新了【楼宇房屋】")
	@Permit("community:property:house:update")
	public CommonResult updateHouse(@RequestBody HouseEntity houseEntity){
		ValidatorUtils.validateEntity(houseEntity, HouseEntity.updateHouseValidatedGroup.class);
		AdminInfoVo loginUser = UserUtils.getAdminInfo();
		houseEntity.setCommunityId(loginUser.getCommunityId());
		houseEntity.setCreateBy(loginUser.getUid());
		boolean result = houseService.updateHouse(houseEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改楼栋信息失败");
	}
	
	/**
	* @Description: 【楼宇房屋】条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>>
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	@ApiOperation("【楼宇房屋】条件查询")
	@PostMapping("query")
	@Permit("community:property:house:query")
	public CommonResult<PageInfo<HouseEntity>> queryHouse(@RequestBody BaseQO<HouseQO> baseQO){
		HouseQO query = baseQO.getQuery();
		if(query == null || query.getType() == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
		}
		query.setCommunityId(UserUtils.getAdminCommunityId());
		if(BusinessConst.BUILDING_TYPE_BUILDING == query.getType()){
			query.setBuilding(query.getName());
		}else if(BusinessConst.BUILDING_TYPE_UNIT == query.getType()){
			query.setUnit(query.getName());
		}else if(BusinessConst.BUILDING_TYPE_DOOR == query.getType()){
			query.setDoor(query.getName());
		}else if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING == query.getType()
			|| BusinessConst.BUILDING_TYPE_DOOR_UNIT == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == query.getType()) {
		
		} else {
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"非法查询类型");
		}
		return CommonResult.ok(houseService.queryHouse(baseQO));
	}
	
	/**
	* @Description: 【楼宇房屋】删除
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	@ApiOperation("【楼宇房屋】删除")
	@DeleteMapping("/delete")
	@businessLog(operation = "删除",content = "删除了【楼宇房屋】")
	@Permit("community:property:house:delete")
	public CommonResult deleteHouse(@RequestParam Long id){
		return houseService.deleteHouse(id,UserUtils.getAdminCommunityId()) ? CommonResult.ok("删除成功") : CommonResult.error("删除失败");
	}

	/**
	* @Description: 【楼宇房屋】查询
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	@ApiOperation("【楼宇房屋】查询")
	@GetMapping("/getHouse")
	@Permit("community:property:house:getHouse")
	public CommonResult getHouse(){
		return CommonResult.ok(houseService.getHouse(UserUtils.getAdminCommunityId()));
	}

	/**
	 * @Description: 【楼宇房屋】批量删除
	 * @Param: [ids]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/08/12
	 **/
	@ApiOperation("【楼宇房屋】批量删除")
	@DeleteMapping("/deletes")
	@Permit("community:property:house:deletes")
	public CommonResult deletesHouse(@RequestParam List<Long> ids) {
		boolean result = houseService.deletesHouse(ids);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(), "【楼宇房屋】 批量删除失败");
	}

	// ============================================ 物业端产品原型确定后新加的 结束  ===========================================================
	
	/**
	 * @Description: 【楼宇房屋】新增楼宇分类
	 * @Param: [houseBuildingTypeEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	@ApiOperation("【楼宇房屋】新增楼宇分类")
	@PostMapping("/building/type/add")
	@businessLog(operation = "新增",content = "新增了【楼宇分类】")
	@Permit("community:property:house:building:type:add")
	public CommonResult addHouseBuildingType(@RequestBody HouseBuildingTypeEntity houseBuildingTypeEntity){
		ValidatorUtils.validateEntity(houseBuildingTypeEntity,HouseBuildingTypeEntity.addHouseBuildingTypeGroup.class);
		AdminInfoVo loginUser = UserUtils.getAdminInfo();
		houseBuildingTypeEntity.setCommunityId(loginUser.getCommunityId());
		houseBuildingTypeEntity.setCreateBy(UserUtils.getUserId());
		return houseService.addHouseBuildingType(houseBuildingTypeEntity)
			? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增楼宇分类失败");
	}
	
	/**
	 * @Description: 【楼宇房屋】修改楼宇分类
	 * @Param: [houseBuildingTypeEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	@ApiOperation("【楼宇房屋】修改楼宇分类")
	@PostMapping("/building/type/update")
	@businessLog(operation = "编辑",content = "更新了【楼宇分类】")
	@Permit("community:property:house:building:type:update")
	public CommonResult updateHouseBuildingType(@RequestBody HouseBuildingTypeEntity houseBuildingTypeEntity){
		ValidatorUtils.validateEntity(houseBuildingTypeEntity);
		AdminInfoVo loginUser = UserUtils.getAdminInfo();
		houseBuildingTypeEntity.setCommunityId(loginUser.getCommunityId());
		houseBuildingTypeEntity.setUpdateBy(UserUtils.getUserId());
		return houseService.updateHouseBuildingType(houseBuildingTypeEntity)
			? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改楼宇分类失败");
	}
	
	/**
	 * @Description: 【楼宇房屋】删除楼宇分类
	 * @Param: [houseBuildingTypeEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	@ApiOperation("【楼宇房屋】删除楼宇分类")
	@DeleteMapping("/building/type/delete")
	@businessLog(operation = "删除",content = "删除了【楼宇分类】")
	@Permit("community:property:house:building:type:delete")
	public CommonResult deleteHouseBuildingType(@RequestParam Long id){
		return houseService.deleteHouseBuildingType(id,UserUtils.getAdminCommunityId()) ? CommonResult.ok("删除成功") : CommonResult.error("删除失败");
	}
	
	/**
	 * @Description: 【楼宇房屋】楼宇分类分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseBuildingTypeEntity>>
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	@ApiOperation("【楼宇房屋】楼宇分类分页查询")
	@PostMapping("/building/type/query")
	@Permit("community:property:house:building:type:query")
	public CommonResult<PageInfo<HouseBuildingTypeEntity>> queryHouseBuildingType(@RequestBody BaseQO<HouseBuildingTypeQO> baseQO) {
		HouseBuildingTypeQO query = baseQO.getQuery();
		if(query == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
		}
		query.setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(houseService.queryHouseBuildingType(baseQO));
	}
	
	/**
	 *@Author: DKS
	 *@Description: 下载房屋信息导入模板
	 *@Date: 2021/8/10 9:10
	 **/
	@ApiOperation("下载房屋信息导入模板")
	@PostMapping("/downloadHouseExcelTemplate")
	@Permit("community:property:house:downloadHouseExcelTemplate")
	public ResponseEntity<byte[]> downloadHouseExcelTemplate() {
		//设置excel 响应头信息
		MultiValueMap<String, String> multiValueMap = new HttpHeaders();
		//设置响应类型为附件类型直接下载这种
		multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("房屋信息.xlsx", StandardCharsets.UTF_8));
		//设置响应的文件mime类型为 xls类型
		multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
		Workbook workbook = houseExcelHandler.exportHouseTemplate();
		//把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
		try {
			return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
		}
	}

//	/**
//	 *@Author: Pipi
//	 *@Description: 导入房屋信息
//	 *@Param: excel:
//	 *@Return: com.jsy.community.vo.CommonResult
//	 *@Date: 2021/5/19 11:38
//	 **/
//	@Login
//	@ApiOperation("导入房屋信息")
//	@PostMapping("/importHouseExcel")
//	public CommonResult importHouseExcel(MultipartFile excel) {
//		//参数验证
//		validFileSuffix(excel);
//		Long adminCommunityId = UserUtils.getAdminCommunityId();
//		String userId = UserUtils.getUserId();
//		ArrayList<HouseImportErrorVO> errorVos = new ArrayList<>(32);
//		List<HouseEntity> houseEntities = houseExcelHandler.importHouseExcel(excel, errorVos);
//		List<HouseEntity> allHouse = houseService.getAllHouse(adminCommunityId);
//		// 通过物业提交的数据 和 数据库该社区已存在的数据进行效验
//		Iterator<HouseEntity> iterator = houseEntities.iterator();
//		while (iterator.hasNext()) {
//			HouseEntity houseEntity = iterator.next();
//			for (HouseEntity entity : allHouse) {
//				// 类型是房屋,房屋编号已经存在
//				if (entity.getNumber().equals(houseEntity.getNumber()) && entity.getType() == 4) {
//					iterator.remove();
//					HouseImportErrorVO errorVO = new HouseImportErrorVO();
//					BeanUtils.copyProperties(houseEntity, errorVO);
//					errorVO.setHouseType(PropertyEnum.HouseTypeEnum.getName(houseEntity.getHouseType()));
//					errorVO.setPropertyType(PropertyEnum.PropertyTypeEnum.getName(houseEntity.getPropertyType()));
//					errorVO.setDecoration(PropertyEnum.DecorationEnum.getName(houseEntity.getDecoration()));
//					errorVO.setRemark("房屋编号已经存在!");
//					errorVos.add(errorVO);
//				}
//			}
//		}
//		Integer row = 0;
//		if (CollectionUtil.isNotEmpty(houseEntities)) {
//			//获取管理员姓名 用于标识每条业主数据的创建人
//			row = houseService.saveHouseBatch(houseEntities, adminCommunityId, userId);
//		}
//		//excel导入失败的信息明细 文件下载地址
//		String errorExcelAddr = null;
//		//错误excel写入远程服务器 让物业人员可以直接下载
//		if( CollectionUtil.isNotEmpty(errorVos) ){
//			errorExcelAddr = uploadErrorExcel(errorVos);
//		}
//		//构造返回对象
//		return CommonResult.ok(new HouseImportErrorVO(row, errorVos.size(), errorExcelAddr));
//	}
	
	/**
	 *@Author: DKS
	 *@Description: 导入房屋信息
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/7 13:38
	 **/
	@ApiOperation("导入房屋信息")
	@PostMapping("/importHouseExcel")
	@Permit("community:property:house:importHouseExcel")
	public CommonResult importHouseExcel(MultipartFile excel) {
		//参数验证
		validFileSuffix(excel);
		Long adminCommunityId = UserUtils.getAdminCommunityId();
		String userId = UserUtils.getUserId();
		ArrayList<HouseImportErrorVO> errorVos = new ArrayList<>(32);
		List<HouseEntity> houseEntities = houseExcelHandler.importHouseExcel(excel, errorVos);
		List<HouseEntity> allHouse = houseService.getAllHouse(adminCommunityId);
		// 通过物业提交的数据 和 数据库该社区已存在的数据进行效验
		Iterator<HouseEntity> iterator = houseEntities.iterator();
		while (iterator.hasNext()) {
			HouseEntity houseEntity = iterator.next();
			for (HouseEntity entity : allHouse) {
				// 类型是房屋,房屋已经存在
				if (entity.getBuilding().equals(houseEntity.getBuilding()) && entity.getUnit().equals(houseEntity.getUnit()) && entity.getFloor().equals(houseEntity.getFloor())
					&& entity.getDoor().equals(houseEntity.getDoor()) && entity.getType() == 4) {
					iterator.remove();
					HouseImportErrorVO errorVO = new HouseImportErrorVO();
					BeanUtils.copyProperties(houseEntity, errorVO);
//					errorVO.setHouseType(PropertyEnum.HouseTypeEnum.getName(houseEntity.getHouseType()));
//					errorVO.setPropertyType(PropertyEnum.PropertyTypeEnum.getName(houseEntity.getPropertyType()));
//					errorVO.setDecoration(PropertyEnum.DecorationEnum.getName(houseEntity.getDecoration()));
					errorVO.setDoor(houseEntity.getDoor());
					errorVO.setBuilding(houseEntity.getBuilding());
					errorVO.setTotalFloor(houseEntity.getTotalFloor());
					errorVO.setUnit(houseEntity.getUnit());
					errorVO.setFloor(houseEntity.getFloor());
					errorVO.setBuildArea(houseEntity.getBuildArea());
					errorVO.setRemark("房屋已经存在!");
					errorVos.add(errorVO);
				}
			}
		}
		Integer row = 0;
		if (CollectionUtil.isNotEmpty(houseEntities)) {
			//获取管理员姓名 用于标识每条业主数据的创建人
			row = houseService.saveHouseBatch(houseEntities, adminCommunityId, userId);
		}
		//excel导入失败的信息明细 文件下载地址
		String errorExcelAddr = null;
		//错误excel写入远程服务器 让物业人员可以直接下载
		if( CollectionUtil.isNotEmpty(errorVos) ){
			errorExcelAddr = uploadErrorExcel(errorVos);
		}

		//构造返回对象
		return CommonResult.ok(new HouseImportErrorVO(row, errorVos.size(), errorExcelAddr));
	}

	/**
	 *@Author: Pipi
	 *@Description: 写入房屋信息导入错误信息 和 把错误信息excel文件上传至文件服务器
	 *@Param: errorVos:
	 *@Return: java.lang.String:  返回excel文件下载地址
	 *@Date: 2021/5/21 17:38
	 **/
	public String uploadErrorExcel(List<HouseImportErrorVO> errorVos) {
		Workbook workbook = houseExcelHandler.exportErrorExcel(errorVos);
		try {
			byte[] bytes = ExcelUtil.readWorkbook(workbook);
			MultipartFile multipartFile = new MockMultipartFile("file", "houseErrorExcel", "application/vnd.ms-excel", bytes);
			return MinioUtils.upload(multipartFile, "house-error-excel");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *@Author: DKS
	 *@Description: 写入楼栋信息导入错误信息 和 把错误信息excel文件上传至文件服务器
	 *@Param: errorVos:
	 *@Return: java.lang.String:  返回excel文件下载地址
	 *@Date: 2021/8/10 10:48
	 **/
	public String uploadBuildingErrorExcel(List<BuildingImportErrorVO> errorVos) {
		Workbook workbook = houseExcelHandler.exportBuildingErrorExcel(errorVos);
		try {
			byte[] bytes = ExcelUtil.readWorkbook(workbook);
			MultipartFile multipartFile = new MockMultipartFile("file", "houseErrorExcel", "application/vnd.ms-excel", bytes);
			return MinioUtils.upload(multipartFile, "house-error-excel");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * excel 文件上传上来后 验证方法
	 * @param file        excel文件
	 */
	private void validFileSuffix(MultipartFile file) {
		//参数非空验证
		if (null == file) {
			throw new JSYException(JSYError.BAD_REQUEST);
		}
		//文件后缀验证
		boolean extension = FilenameUtils.isExtension(file.getOriginalFilename(), ExcelUtil.SUPPORT_EXCEL_EXTENSION);
		if (!extension) {
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "只支持excel文件!");
		}

	}
	
	/**
	 *@Author: DKS
	 *@Description: 导出房屋信息
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/7 13:38
	 **/
	@ApiOperation("导出房屋信息")
	@PostMapping("/downloadHouseList")
	@Permit("community:property:house:downloadHouseList")
	public ResponseEntity<byte[]> downloadOrderList(@RequestBody HouseEntity houseEntity) {
		houseEntity.setCommunityId(UserUtils.getAdminCommunityId());
		List<HouseEntity> houseEntities = houseService.queryExportHouseExcel(houseEntity);
		//设置excel 响应头信息
		MultiValueMap<String, String> multiValueMap = new HttpHeaders();
		//设置响应类型为附件类型直接下载这种
		multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("房屋信息表.xlsx", StandardCharsets.UTF_8));
		//设置响应的文件mime类型为 xls类型
		multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
		Workbook workbook = new XSSFWorkbook();
		workbook = houseExcelHandler.exportHouse(houseEntities);
		//把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
		try {
			return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
		}
	}
	
	/**
	 *@Author: DKS
	 *@Description: 下载楼栋信息导入模板
	 *@Date: 2021/8/10 9:10
	 **/
	@ApiOperation("下载楼栋信息导入模板")
	@PostMapping("/downloadBuildingExcelTemplate")
	@Permit("community:property:house:downloadBuildingExcelTemplate")
	public ResponseEntity<byte[]> downloadBuildingExcelTemplate() {
		List<HouseBuildingTypeEntity> houseBuildingTypeEntities = houseService.selectHouseBuildingType(UserUtils.getAdminCommunityId());
		//设置excel 响应头信息
		MultiValueMap<String, String> multiValueMap = new HttpHeaders();
		//设置响应类型为附件类型直接下载这种
		multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("智慧小区-楼宇导入模板.xlsx", StandardCharsets.UTF_8));
		//设置响应的文件mime类型为 xls类型
		multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
		Workbook workbook = houseExcelHandler.exportBuildingTemplate(houseBuildingTypeEntities);
		//把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
		try {
			return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
		}
	}
	
	/**
	 *@Author: DKS
	 *@Description: 导入楼栋信息
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/10 10:38
	 **/
	@ApiOperation("导入楼栋信息")
	@PostMapping("/importBuildingExcel")
	@Permit("community:property:house:importBuildingExcel")
	public CommonResult importBuildingExcel(MultipartFile excel) {
		//参数验证
		validFileSuffix(excel);
		Long adminCommunityId = UserUtils.getAdminCommunityId();
		String userId = UserUtils.getUserId();
		ArrayList<BuildingImportErrorVO> errorVos = new ArrayList<>(32);
		List<HouseEntity> houseEntities = houseExcelHandler.importBuildingExcel(excel, errorVos);
		List<HouseEntity> allBuilding = houseService.selectAllBuilding(adminCommunityId);
		List<String> buildingTypeNames = new ArrayList<>();
		for (int i = 0; i < houseEntities.size(); i++) {
			buildingTypeNames.add(houseEntities.get(i).getBuildingTypeName());
		}
		if (buildingTypeNames.size() > 0) {
			Map<String, Map<String, Long>> buildingTypeIdMap = houseService.queryHouseBuildingTypeId(buildingTypeNames);
			//补楼宇分类名称id
			for (HouseEntity houseEntity : houseEntities) {
				if (houseEntity.getBuildingTypeName() != null) {
					Map<String, Long> countMap = buildingTypeIdMap.get(houseEntity.getBuildingTypeName());
					houseEntity.setBuildingType(countMap == null ? null : countMap.get("id"));
				}
			}
		}
		// 通过物业提交的数据 和 数据库该社区已存在的数据进行效验
		Iterator<HouseEntity> iterator = houseEntities.iterator();
		while (iterator.hasNext()) {
			HouseEntity houseEntity = iterator.next();
			for (HouseEntity entity : allBuilding) {
				// 类型是楼栋,楼栋已经存在
				if (entity.getBuilding().equals(houseEntity.getBuilding()) && entity.getType() == 1) {
					iterator.remove();
					BuildingImportErrorVO errorVO = new BuildingImportErrorVO();
					BeanUtils.copyProperties(houseEntity, errorVO);
					errorVO.setBuilding(houseEntity.getBuilding());
					errorVO.setTotalFloor(houseEntity.getTotalFloor());
					errorVO.setBuildingTypeName(houseEntity.getBuildingTypeName());
					errorVO.setRemark("楼栋已经存在!");
					errorVos.add(errorVO);
				}
			}
		}
		Integer row = 0;
		if (CollectionUtil.isNotEmpty(houseEntities)) {
			//获取管理员姓名 用于标识每条业主数据的创建人
			row = houseService.saveBuildingBatch(houseEntities, adminCommunityId, userId);
		}
		//excel导入失败的信息明细 文件下载地址
		String errorExcelAddr = null;
		//错误excel写入远程服务器 让物业人员可以直接下载
		if( CollectionUtil.isNotEmpty(errorVos) ){
			errorExcelAddr = uploadBuildingErrorExcel(errorVos);
		}
		//构造返回对象
		return CommonResult.ok(new BuildingImportErrorVO(row, errorVos.size(), errorExcelAddr));
	}
	
	/**
	 *@Author: DKS
	 *@Description: 显示楼栋、单元、房屋树形结构
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/10 10:38
	 **/
	@ApiOperation("显示楼栋、单元、房屋树形结构")
	@PostMapping("/tree")
	@Permit("community:property:house:tree")
	public CommonResult getHouseTree() {
		Map<String, Object> returnMap = new HashMap<>();
		HouseTreeUtil menuTree = new HouseTreeUtil();
		List<HouseEntity> allHouse = houseService.selectAllBuildingUnitDoor(UserUtils.getAdminCommunityId());
		List<Object> menuList = menuTree.menuList(allHouse);
		returnMap.put("list", menuList);
		return CommonResult.ok(returnMap);
	}

	/**
	 * @author: Pipi
	 * @description: 通用房屋名称搜索
	 * @param houseEntity: 查询条件-名称模糊查询
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/11/11 9:38
	 **/
	@ApiOperation("显示楼栋、单元、房屋树形结构")
	@PostMapping("/v2/commonQueryHouse")
	@Permit("community:property:house:v2:commonQueryHouse")
	public CommonResult commonQueryHouse(@RequestBody HouseEntity houseEntity) {
		if (houseEntity.getType() == null || houseEntity.getType() < 0 || houseEntity.getType() > 4) {
			houseEntity.setType(4);
		}
		houseEntity.setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(houseService.commonQueryHouse(houseEntity));
	}




}

