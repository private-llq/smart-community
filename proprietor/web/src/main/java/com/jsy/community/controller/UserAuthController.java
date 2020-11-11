package com.jsy.community.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("user/auth")
@Api(tags = "用户认证控制器")
@RestController
public class UserAuthController {
}
