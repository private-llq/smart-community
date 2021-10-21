package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.service.HouseExcelHandler;
import com.jsy.community.service.IHouseService;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.utils.HouseTreeUtil;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 房屋信息 前端控制器
 * </p>
 *
 * @author DKS
 * @since 2021-10-21
 */
@Api(tags = "房屋信息控制器")
@RestController
@RequestMapping("house")
@ApiJSYController
public class HouseController {

	@Resource
	private IHouseService houseService;

	@Resource
	private HouseExcelHandler houseExcelHandler;

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
		}else if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING == query.getType()
			|| BusinessConst.BUILDING_TYPE_DOOR_UNIT == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == query.getType()) {

		} else {
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"非法查询类型");
		}
		return CommonResult.ok(houseService.queryHouse(baseQO));
	}

	/**
	* @Description: 【楼宇房屋】查询
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	@Login
	@ApiOperation("【楼宇房屋】查询")
	@GetMapping("/getHouse")
	@businessLog(operation = "删除",content = "删除了【楼宇房屋】")
	public CommonResult getHouse(){
		return CommonResult.ok(houseService.getHouse(UserUtils.getAdminCommunityId()));
	}

	/**
	 *@Author: DKS
	 *@Description: 导出房屋信息
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/7 13:38
	 **/
	@Login
	@ApiOperation("导出房屋信息")
	@PostMapping("/downloadHouseList")
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
	 *@Description: 显示楼栋、单元、房屋树形结构
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/10 10:38
	 **/
	@Login
	@ApiOperation("显示楼栋、单元、房屋树形结构")
	@PostMapping("/tree")
	public CommonResult getHouseTree() {
		Map<String, Object> returnMap = new HashMap<>();
		HouseTreeUtil menuTree = new HouseTreeUtil();
		List<HouseEntity> allHouse = houseService.selectAllBuildingUnitDoor(UserUtils.getAdminCommunityId());
		List<Object> menuList = menuTree.menuList(allHouse);
		returnMap.put("list", menuList);
		return CommonResult.ok(returnMap);
	}
}

