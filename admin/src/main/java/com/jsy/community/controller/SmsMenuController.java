package com.jsy.community.controller;

import com.jsy.community.entity.SmsMenuEntity;
import com.jsy.community.service.ISmsMenuService;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 短信套餐
 * @author: DKS
 * @since: 2021/12/9 11:11
 */
@Api(tags = "短信套餐")
@RestController
@RequestMapping("/sms/menu")
public class SmsMenuController {
    @Resource
    private ISmsMenuService smsMenuService;
    
    /**
     * @Description: 新增短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:11
     * @Param: [smsMenuEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/add")
    @Permit("community:admin:sms:menu:add")
    public CommonResult addSmsType(@RequestBody SmsMenuEntity smsMenuEntity){
        return CommonResult.ok(smsMenuService.addSmsMenu(smsMenuEntity) ? "添加成功" : "添加失败");
    }
    
    /**
     * @Description: 修改短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:11
     * @Param: [smsMenuEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PutMapping("/update")
    @Permit("community:admin:sms:menu:update")
    public CommonResult updateSmsType(@RequestBody SmsMenuEntity smsMenuEntity){
        return CommonResult.ok(smsMenuService.updateSmsMenu(smsMenuEntity) ? "修改成功" : "修改失败");
    }
    
    /**
     * @Description: 删除短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:11
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @DeleteMapping("/delete")
    @Permit("community:admin:sms:menu:delete")
    public CommonResult deleteSmsType(@RequestParam("id") Long id){
        return CommonResult.ok(smsMenuService.deleteSmsMenu(id) ? "删除成功" : "删除失败");
    }
    
    /**
     * @Description: 查询短信套餐列表
     * @author: DKS
     * @since: 2021/12/9 11:11
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/query")
    @Permit("community:admin:sms:menu:query")
    public CommonResult selectSmsType(){
        List<SmsMenuEntity> list = smsMenuService.selectSmsMenu();
        return CommonResult.ok(list,"查询成功");
    }
}
