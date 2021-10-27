package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.CarQO;
import com.jsy.community.service.HouseExcelHandler;
import com.jsy.community.service.ICarService;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * 车辆管理 前端控制器
 * </p>
 *
 * @author DKS
 * @since 2021-10-26
 */
@Api(tags = "车辆管理控制器")
@RestController
@RequestMapping("/car")
@ApiJSYController
public class CarController {

	@Resource
	private ICarService carService;

	@Resource
	private HouseExcelHandler houseExcelHandler;

	/**
	* @Description: 【车辆】条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.CarEntity>>
	 * @Author: DKS
	 * @Date: 2021/10/26
	**/
	@Login
	@ApiOperation("【车辆】条件查询")
	@PostMapping("query")
	public CommonResult<PageInfo<CarEntity>> queryCar(@RequestBody BaseQO<CarQO> baseQO){
		return CommonResult.ok(carService.queryCar(baseQO));
	}

	/**
	 *@Author: DKS
	 *@Description: 导出车辆管理
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/10/26 10:29
	 **/
	@Login
	@ApiOperation("导出车辆管理")
	@PostMapping("/downloadCarList")
	public ResponseEntity<byte[]> downloadOrderList(@RequestBody CarQO carQO) {
		List<CarEntity> carEntities = carService.queryExportCarExcel(carQO);
		//设置excel 响应头信息
		MultiValueMap<String, String> multiValueMap = new HttpHeaders();
		//设置响应类型为附件类型直接下载这种
		multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("车辆信息表.xlsx", StandardCharsets.UTF_8));
		//设置响应的文件mime类型为 xls类型
		multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
		Workbook workbook;
		workbook = houseExcelHandler.exportCar(carEntities);
		//把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
		try {
			return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
		}
	}
}

