package com.jsy.community.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PropertyEnum;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.util.ProprietorExcelCommander;
import com.jsy.community.util.excel.impl.HouseExcelHandlerImpl;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.HouseImportErrorVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
@ApiJSYController
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
	
	//TODO 修改入参QO 关联修改下级
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
	@Login
	@ApiOperation("【楼宇房屋】新增楼栋、单元、房屋")
	@PostMapping("")
	public CommonResult addHouse(@RequestBody HouseEntity houseEntity){
		if(houseEntity.getType() == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少类型参数");
		}
		if(BusinessConst.BUILDING_TYPE_DOOR == houseEntity.getType()){
			ValidatorUtils.validateEntity(houseEntity,HouseEntity.addRoomValidatedGroup.class);
		}else{
			ValidatorUtils.validateEntity(houseEntity,HouseEntity.addHouseValidatedGroup.class);
		}
		AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
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
	@Login
	@ApiOperation("【楼宇房屋】修改")
	@PutMapping("")
	public CommonResult updateHouse(@RequestBody HouseEntity houseEntity){
		ValidatorUtils.validateEntity(houseEntity, HouseEntity.updateHouseValidatedGroup.class);
		AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
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
	@Login
	@ApiOperation("【楼宇房屋】条件查询")
	@PostMapping("query")
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
		}else{
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
	@Login
	@ApiOperation("【楼宇房屋】删除")
	@DeleteMapping("")
	public CommonResult deleteHouse(@RequestParam Long id){
		return houseService.deleteHouse(id,UserUtils.getAdminCommunityId()) ? CommonResult.ok("删除成功") : CommonResult.error("删除失败");
	}
	// ============================================ 物业端产品原型确定后新加的 结束  ===========================================================


	@Login
	@ApiOperation("下载房屋信息模板")
	@GetMapping("/downloadHouseExcelTemplate")
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
			return new ResponseEntity<>(readWorkbook(workbook), multiValueMap, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
		}
	}

	/**
	 *@Author: Pipi
	 *@Description: 导入房屋信息
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/5/19 11:38
	 **/
	@Login
	@ApiOperation("导入房屋信息")
	@PostMapping("/importHoustExcel")
	public CommonResult importHoustExcel(MultipartFile excel) {
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
				// 类型是房屋,房屋编号已经存在
				if (entity.getNumber().equals(houseEntity.getNumber()) && entity.getType() == 4) {
					iterator.remove();
					HouseImportErrorVO errorVO = new HouseImportErrorVO();
					BeanUtils.copyProperties(houseEntity, errorVO);
					errorVO.setHouseType(PropertyEnum.HouseTypeEnum.getName(houseEntity.getHouseType()));
					errorVO.setPropertyType(PropertyEnum.PropertyTypeEnum.getName(houseEntity.getPropertyType()));
					errorVO.setDecoration(PropertyEnum.DecorationEnum.getName(houseEntity.getDecoration()));
					errorVO.setRemark("房屋编号已经存在!");
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
			byte[] bytes = readWorkbook(workbook);
			MultipartFile multipartFile = new MockMultipartFile("file", "houseErrorExcel", "application/vnd.ms-excel", bytes);
			return MinioUtils.upload(multipartFile, "house-error-excel");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取工作簿 返回字节数组
	 *
	 * @param workbook excel工作簿
	 * @return 返回读取完成的字节数组
	 */
	private byte[] readWorkbook(Workbook workbook) throws IOException {
		//2.3 把workbook转换为字节输入流
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		//@Cleanup注解 会在作用域的末尾将调用is.close()方法，并使用了try/finally代码块 执行。
		@Cleanup InputStream is = new ByteArrayInputStream(bos.toByteArray());
		byte[] byt = new byte[is.available()];
		//2.4 读取字节流 响应实体返回
		int read = is.read(byt);
		return byt;
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
		boolean extension = FilenameUtils.isExtension(file.getOriginalFilename(), ProprietorExcelCommander.SUPPORT_EXCEL_EXTENSION);
		if (!extension) {
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "只支持excel文件!");
		}
	}
}

