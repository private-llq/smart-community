package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyAccountService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyAccountBankEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 社区对公账户Controller
 * @since 2021-04-20 17:51
 **/
@Api(tags = "社区账户信息")
@RestController
@RequestMapping("/account")
@ApiJSYController
public class PropertyAccountController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyAccountService propertyAccountService;
	
	/**
	* @Description: 本社区社区信息查询
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/20
	**/
	@ApiOperation("本社区社区信息查询")
	@GetMapping("")
	@Permit("community:property:account")
	public CommonResult queryByCommunityId(){
		Map<String, Object> returnMap = new HashMap<>();
		//对公账户
		PropertyAccountBankEntity account = propertyAccountService.queryBankAccount(UserUtils.getAdminCommunityId());
		returnMap.put("account",account);
		
		//企业信息
		Map<String, Object> companyInfoMap = new HashMap<>();
		companyInfoMap.put("companyName","重庆纵横世纪科技有限公司");
		companyInfoMap.put("creditCode","J11233332322223321");
		companyInfoMap.put("legalPerson","张三");
		
		//结算方式
		Map<String, Object> statementMap = new HashMap<>();
		statementMap.put("content","每月5~7号自动结算上月费用，如遇法定节假日将顺延");
		
		//组装返回
		returnMap.put("companyInfo",companyInfoMap);
		returnMap.put("statement",statementMap);
		return CommonResult.ok(returnMap,"查询成功");
	}
	
	/**
	* @Description: 结算账户查询(对公账户)
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/21
	**/
	@ApiOperation("结算账户查询")
	@GetMapping("statementAccount")
	@Permit("community:property:account:statementAccount")
	public CommonResult queryByBankAccountById(@RequestParam Long id){
		//这里默认结算账户是物业银行卡账户
		return CommonResult.ok(propertyAccountService.queryBankAccountById(id));
	}
}
