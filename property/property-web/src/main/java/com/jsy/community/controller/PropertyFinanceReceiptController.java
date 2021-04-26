package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyFinanceReceiptService;
import com.jsy.community.constant.Const;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author chq459799974
 * @description 财务板块 - 收款单
 * @since 2021-04-21 17:30
 **/
@Api(tags = "财务板块 - 收款单")
@RestController
@RequestMapping("/finance/receipt")
@ApiJSYController
@Login
public class PropertyFinanceReceiptController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyFinanceReceiptService propertyFinanceReceiptService;

	@Autowired
	private FinanceExcelImpl financeExcel;
	
	/**
	* @Description: 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/22
	**/
	@ApiOperation("分页查询")
	@PostMapping("page")
	public CommonResult queryPage(@RequestBody BaseQO<PropertyFinanceReceiptEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new PropertyFinanceReceiptEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(propertyFinanceReceiptService.queryPage(baseQO),"查询成功");
	}

	@Login
	@ApiOperation("物业财务-导出收款单")
	@PostMapping("/downloadReceiptList")
	public ResponseEntity<byte[]> downloadReceiptList(@RequestBody PropertyFinanceReceiptEntity receiptEntity) {
		ValidatorUtils.validateEntity(receiptEntity, PropertyFinanceReceiptEntity.ExportValiadate.class);
		receiptEntity.setCommunityId(UserUtils.getAdminCommunityId());
		List<PropertyFinanceReceiptEntity> receiptEntities = propertyFinanceReceiptService.queryExportReceiptList(receiptEntity);
		//设置excel 响应头信息
		MultiValueMap<String, String> multiValueMap = new HttpHeaders();
		//设置响应类型为附件类型直接下载这种
		multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("收款单表.xlsx", StandardCharsets.UTF_8));
		//设置响应的文件mime类型为 xls类型
		multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
		Workbook workbook = new XSSFWorkbook();
		if (receiptEntity.getExportType() == 1) {
			workbook = financeExcel.exportMasterReceipt(receiptEntities);
		} else {
			workbook = financeExcel.exportMasterSlaveReceipt(receiptEntities);
		}
		//把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
		try {
			return new ResponseEntity<>(readWorkbook(workbook), multiValueMap, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
		}
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
}
