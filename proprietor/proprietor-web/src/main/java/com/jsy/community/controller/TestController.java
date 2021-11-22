package com.jsy.community.controller;

/*import com.zhsj.basecommon.vo.R;
import com.zhsj.baseweb.annotation.Permit;
import com.zhsj.baseweb.support.ContextHolder;
import com.zhsj.baseweb.support.LoginUser;
import org.springframework.web.bind.annotation.GetMapping;*/
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/18 16:46
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/test")
public class TestController {

 /*   @GetMapping("/test")
    @Permit("community:proprietor:test:test")
    public R<Void> test(){
        LoginUser loginUser = ContextHolder.getContext().getLoginUser();
        return R.ok();
    }*/
}
