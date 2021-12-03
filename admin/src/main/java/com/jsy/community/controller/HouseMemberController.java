package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseMemberQO;
import com.jsy.community.service.HouseExcelHandler;
import com.jsy.community.service.IHouseMemberService;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
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
 * 住户信息 前端控制器
 * </p>
 *
 * @author DKS
 * @since 2021-10-22
 */
@Api(tags = "住户信息控制器")
@RestController
@RequestMapping("house/member")
// @ApiJSYController
public class HouseMemberController {

	@Resource
	private IHouseMemberService houseMemberService;

	@Resource
	private HouseExcelHandler houseExcelHandler;

	/**
	* @Description: 【住户】条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseMemberEntity>>
	 * @Author: DKS
	 * @Date: 2021/10/22
	**/
	@ApiOperation("【住户】条件查询")
	@PostMapping("query")
	@Permit("community:admin:house:member:query")
	public CommonResult<PageInfo<HouseMemberEntity>> queryHouseMember(@RequestBody BaseQO<HouseMemberQO> baseQO){
		return CommonResult.ok(houseMemberService.queryHouseMember(baseQO));
	}

	/**
	 *@Author: DKS
	 *@Description: 导出住户信息
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/10/22 16:38
	 **/
	@ApiOperation("导出住户信息")
	@PostMapping("/downloadHouseMemberList")
	@Permit("community:admin:house:member:downloadHouseMemberList")
	public ResponseEntity<byte[]> downloadOrderList(@RequestBody HouseMemberQO houseMemberQO) {
		List<HouseMemberEntity> houseMemberEntities = houseMemberService.queryExportHouseMemberExcel(houseMemberQO);
		//设置excel 响应头信息
		MultiValueMap<String, String> multiValueMap = new HttpHeaders();
		//设置响应类型为附件类型直接下载这种
		multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("房屋信息表.xlsx", StandardCharsets.UTF_8));
		//设置响应的文件mime类型为 xls类型
		multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
		Workbook workbook;
		workbook = houseExcelHandler.exportHouseMember(houseMemberEntities);
		//把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
		try {
			return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
		}
	}
}

