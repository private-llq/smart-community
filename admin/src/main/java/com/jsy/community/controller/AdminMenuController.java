package com.jsy.community.controller;


import com.jsy.community.entity.AdminMenuEntity;
import com.jsy.community.service.IAdminMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author jsy
 * @since 2020-11-24
 */
@Api(tags = "APP菜单控制器")
//@Login
@Slf4j
@RestController
@RequestMapping("/community/adminMenu")
public class AdminMenuController {
	
	@Autowired
	private IAdminMenuService adminMenuService;
	
	@ApiOperation("查询所有App菜单信息")
	@GetMapping("/listAdminMenu")
	public List<AdminMenuEntity> listAdminMenu(){
		return adminMenuService.list();
	}
}

