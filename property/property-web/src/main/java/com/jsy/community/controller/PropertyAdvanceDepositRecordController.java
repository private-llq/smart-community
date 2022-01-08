package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IPropertyAdvanceDepositRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyAdvanceDepositRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositRecordQO;
import com.jsy.community.util.excel.impl.AdvanceDepositExcelHandlerImpl;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 物业预存款余额明细记录表
 * @author: DKS
 * @create: 2021-08-12 14:15
 **/
@Api(tags = "物业预存款余额明细记录表")
@RestController
@RequestMapping("/advance/deposit/record")
// @ApiJSYController
public class PropertyAdvanceDepositRecordController {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyAdvanceDepositRecordService propertyAdvanceDepositRecordService;
	
	@Autowired
	private AdvanceDepositExcelHandlerImpl advanceDepositExcelHandler;
    
    /**
     * @program: com.jsy.community
     * @description: 新增预存款变更明细记录
     * @author: DKS
     * @create: 2021-08-12 15:23
     **/
    @ApiOperation("新增预存款变更明细记录")
    @PostMapping("/add")
    @businessLog(operation = "新增",content = "新增了【预存款变更明细记录】")
    @Permit("community:property:advance:deposit:record:add")
    public CommonResult addPropertyAdvanceDepositRecord(@RequestBody PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity){
	    if(propertyAdvanceDepositRecordEntity.getType() == null){
		    throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少类型参数");
	    }
	    if (propertyAdvanceDepositRecordEntity.getType().equals(1) && propertyAdvanceDepositRecordEntity.getOrderId() == null) {
		    throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少账单id参数");
	    }
	    if(propertyAdvanceDepositRecordEntity.getAdvanceDepositId() == null){
		    throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少预存款id参数");
	    }
        ValidatorUtils.validateEntity(propertyAdvanceDepositRecordEntity);
        AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
        propertyAdvanceDepositRecordEntity.setCommunityId(loginUser.getCommunityId());
        propertyAdvanceDepositRecordEntity.setCreateBy(loginUser.getUid());
        boolean result = propertyAdvanceDepositRecordService.addPropertyAdvanceDepositRecord(propertyAdvanceDepositRecordEntity);
        return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增预存款变更明细记录失败");
    }
    
    /**
     * @Description: 预存款分页查询变更明细
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyAdvanceDepositEntity>>
     * @Author: DKS
     * @Date: 2021/08/12
     **/
    @ApiOperation("预存款分页查询变更明细")
    @PostMapping("/query")
    @Permit("community:property:advance:deposit:record:query")
    public CommonResult<PageInfo<PropertyAdvanceDepositRecordEntity>> queryPropertyDepositRecord(@RequestBody BaseQO<PropertyAdvanceDepositRecordQO> baseQO) {
	    PropertyAdvanceDepositRecordQO query = baseQO.getQuery();
        if(query == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        query.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyAdvanceDepositRecordService.queryPropertyAdvanceDepositRecord(baseQO));
    }
	
	/**
	 *@Author: DKS
	 *@Description: 导出预存款明细记录
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/17 9:05
	 **/
	@ApiOperation("导出预存款明细记录")
	@PostMapping("/downloadAdvanceDepositList")
	@Permit("community:property:advance:deposit:record:downloadAdvanceDepositList")
	public ResponseEntity<byte[]> downloadOrderList(@RequestBody PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity) {
		propertyAdvanceDepositRecordEntity.setCommunityId(UserUtils.getAdminCommunityId());
		List<PropertyAdvanceDepositRecordEntity> propertyAdvanceDepositRecordEntities = propertyAdvanceDepositRecordService.queryExportHouseExcel(propertyAdvanceDepositRecordEntity);
		//设置excel 响应头信息
		MultiValueMap<String, String> multiValueMap = new HttpHeaders();
		//设置响应类型为附件类型直接下载这种
		multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("预存款明细记录表.xlsx", StandardCharsets.UTF_8));
		//设置响应的文件mime类型为 xls类型
		multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
		Workbook workbook = new XSSFWorkbook();
		workbook = advanceDepositExcelHandler.exportAdvanceDeposit(propertyAdvanceDepositRecordEntities);
		//把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
		try {
			return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
		}
	}
	
	/**
	 * @Description: 通过id获取预存款明细记录打印信息
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/08/20
	 **/
	@ApiOperation("通过id获取预存款明细记录")
	@GetMapping("/getAdvanceDepositRecordById")
	@Permit("community:property:advance:deposit:record:getAdvanceDepositRecordById")
	public CommonResult getAdvanceDepositRecordById(Long id) {
		return CommonResult.ok(propertyAdvanceDepositRecordService.getAdvanceDepositRecordById(id, UserUtils.getAdminCommunityId()));
	}
}
