package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyFinanceCountService;
import com.jsy.community.api.IPropertyFinanceReceiptService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceCountEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.util.excel.impl.FinanceExcelImpl;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * @author chq459799974
 * @description 财务板块 - 统计
 * @since 2021-04-26 17:12
 **/
@Api(tags = "财务板块 - 统计")
@RestController
@RequestMapping("/finance/count")
@ApiJSYController
@Login
public class PropertyFinanceCountController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyFinanceCountService propertyFinanceCountService;
	
	
	@ApiOperation("缴费统计")
	@PostMapping("orderPaid")
	public CommonResult orderPaid(@RequestBody PropertyFinanceCountEntity query){
		if(query.getStartDate() == null && query.getEndDate() == null){ //第一次默认查询本日
			query.setStartDate(LocalDate.now());
			query.setEndDate(LocalDate.now());
		}
		query.setCommunityId(UserUtils.getAdminCommunityId());
		propertyFinanceCountService.orderPaidCount(query);
		return CommonResult.ok("查询成功");
	}

}
