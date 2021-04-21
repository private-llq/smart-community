package com.jsy.community.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IDepartmentStaffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.qo.DepartmentStaffQO;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.DepartmentStaffVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "部门员工控制器")
@RestController
@ApiJSYController
@RequestMapping("/staff")
@Login
public class DepartmentStaffController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IDepartmentStaffService departmentStaffService;
	
	@ApiOperation("查询所有员工信息")
	@GetMapping("/listDepartmentStaff")
	public CommonResult<PageInfo<DepartmentStaffEntity>> listDepartmentStaff(@ApiParam("部门id") @RequestParam Long departmentId,
	                                                                         @RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "10") Long size) {
		PageInfo<DepartmentStaffEntity> pageInfo = departmentStaffService.listDepartmentStaff(departmentId, page, size);
		return CommonResult.ok(pageInfo);
	}
	
	@ApiOperation("根据id查询员工信息")
	@GetMapping("/getDepartmentStaffById")
	public CommonResult getDepartmentStaffById(@ApiParam("员工id") Long id) {
		DepartmentStaffEntity staffEntity = departmentStaffService.getDepartmentStaffById(id);
		return CommonResult.ok(staffEntity);
	}
	
	@ApiOperation("添加员工")
	@PostMapping("/addDepartmentStaff")
	// TODO: 2021/3/22 添加员工这里 我觉得应该是唯一的  不过需求没要求唯一
	// // TODO: 2021/4/14 今天做Excel，Excel添加的需求  当时问的经理 说是同一个部门 有一个人以上姓名相同,电话号码有一个相同  就算重复 不能添加成功
	// // TODO: 2021/4/14 但是原型上面没有体现出来，所以 3/22 就没有处理姓名相同和电话号码有一个相同 这个情况  现在来处理
	public CommonResult addDepartmentStaff(@RequestBody DepartmentStaffQO staffEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		staffEntity.setCommunityId(communityId);
		staffEntity.setId(SnowFlake.nextId());
		
		ValidatorUtils.validateEntity(staffEntity, DepartmentStaffQO.addStaffValidate.class);
		departmentStaffService.addDepartmentStaff(staffEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("修改员工信息")
	@PostMapping("/updateDepartmentStaff")
	public CommonResult updateDepartmentStaff(@RequestBody DepartmentStaffQO departmentStaffEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		departmentStaffEntity.setCommunityId(communityId);
		ValidatorUtils.validateEntity(departmentStaffEntity, DepartmentStaffQO.updateStaffValidate.class);
		departmentStaffService.updateDepartmentStaff(departmentStaffEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除员工")
	@GetMapping("/deleteDepartmentStaff")
	public CommonResult deleteStaffByIds(@ApiParam("员工id") Long id) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		departmentStaffService.deleteStaffByIds(id, communityId);
		return CommonResult.ok();
	}
	
	
	// TODO: 2021/3/29 关于这个地方的问题：   哪种情况下添加失败【产品说的那种，没有在列表添加员工的时候体现出来   所以这个功能到时候再问问】
	// TODO: 2021/3/22 添加员工这里 我觉得应该是唯一的  不过需求没要求唯一
	// // TODO: 2021/4/14 今天做Excel，Excel添加的需求  当时问的经理 说是同一个部门 有一个人以上姓名相同,电话号码有一个相同  就算重复 不能添加成功
	// // TODO: 2021/4/14 但是原型上面没有体现出来，所以 3/22 就没有处理姓名相同和电话号码有一个相同 这个情况  现在来处理
	@ApiOperation("通过Excel添加通讯录")
	@PostMapping("/addLinkByExcel")
	public CommonResult addLinkByExcel(@RequestParam("file") MultipartFile file) {
		try {
			// 获取Excel中的数据，每一行数据封装成一个String[]，将一个工作簿里面的每行数据封装成一个List<String[]>
			List<String[]> strings = POIUtils.readExcel(file);
			Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
			Map<String, Object> map = departmentStaffService.addLinkByExcel(strings, communityId);
			return CommonResult.ok(map);
		} catch (IOException e) {
			e.printStackTrace();
			return CommonResult.error("添加失败,请联系管理员");
		}
	}
	
	// TODO: 2021/4/16 这里采用从服务器下载吧
	@GetMapping("/down")
	@ApiOperation("下载模板")
	@Login(allowAnonymous = true)
	public String down() {
		return "http://222.178.212.29:9000/excel/通讯录导入模板";
	}

//	@PostMapping("/up")
//	@ApiOperation("上传模板")
//	@Login(allowAnonymous = true)
//	public String up(@RequestParam("file") MultipartFile file){
//		String excel = MinioUtils.upload(file, "excel");
//		return excel;
//	}
	
	@PostMapping("/export")
	@ApiOperation("导出错误数据")
	@Login(allowAnonymous = true)
	public void export(HttpServletResponse response, @RequestBody Map<String, Object> map) {
		List<DepartmentStaffVO> list = new ArrayList<>();
		
		List<Map<String, String>> failData = (List<Map<String, String>>) map.get("failData");
		List<DepartmentStaffVO> newArrayList = new ArrayList<>();
		for (Map<String, String> failDatum : failData) {
			// 对数据进行处理
			DepartmentStaffVO staffVO = new DepartmentStaffVO();
			String person = failDatum.get("person");
			staffVO.setPerson(person);
			
			String department = failDatum.get("department");
			staffVO.setDepartment(department);
			
			String duty = failDatum.get("duty");
			staffVO.setDuty(duty);
			
			String phone = failDatum.get("phone");
			staffVO.setPhone(phone);
			
			String email = failDatum.get("email");
			staffVO.setEmail(email);
			
			String failReason = failDatum.get("failReason");
			staffVO.setFailReason(failReason);
			
			list.add(staffVO);
			
			newArrayList = CollUtil.newArrayList(list);
		}
		
		// 通过工具类创建writer，默认创建xls格式
		ExcelWriter writer = ExcelUtil.getWriter();
		// 合并单元格后的标题行，使用默认标题样式
		writer.merge(5, "通讯录");
		//自定义标题别名
		writer.addHeaderAlias("person", "姓名").setColumnWidth(0, 20);
		writer.addHeaderAlias("department", "部门").setColumnWidth(1, 20);
		writer.addHeaderAlias("duty", "职务").setColumnWidth(2, 10);
		writer.addHeaderAlias("phone", "电话").setColumnWidth(3, 30);
		writer.addHeaderAlias("email", "邮箱").setColumnWidth(4, 20);
		writer.addHeaderAlias("failReason", "失败原因").setColumnWidth(5, 30);
		// 一次性写出内容，使用默认样式，强制输出标题
		writer.write(newArrayList, true);
		
		//out为OutputStream，需要写出到的目标流
		//response为HttpServletResponse对象
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		//test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
		response.setHeader("Content-Disposition", "attachment;filename=" + "failData" + ".xls");
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			writer.flush(out, true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭writer，释放内存
			writer.close();
		}
		//此处记得关闭输出Servlet流
		IoUtil.close(out);
	}
}

