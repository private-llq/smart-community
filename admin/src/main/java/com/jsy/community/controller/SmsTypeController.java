package com.jsy.community.controller;

import com.jsy.community.entity.SmsTypeEntity;
import com.jsy.community.service.ISmsTypeService;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 短信分类
 * @author: DKS
 * @since: 2021/12/8 10:39
 */
@Api(tags = "短信分类")
@RestController
@RequestMapping("/sms/type")
public class SmsTypeController {
    @Resource
    private ISmsTypeService smsTypeService;
    
    /**
     * @Description: 新增短信分类
     * @author: DKS
     * @since: 2021/12/8 10:42
     * @Param: [smsTypeEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/add")
    @Permit("community:admin:sms:type:add")
    public CommonResult addSmsType(@RequestBody SmsTypeEntity smsTypeEntity){
        return CommonResult.ok(smsTypeService.addSmsType(smsTypeEntity) ? "添加成功" : "添加失败");
    }
    
    /**
     * @Description: 修改短信分类
     * @author: DKS
     * @since: 2021/12/8 10:45
     * @Param: [smsTypeEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PutMapping("/update")
    @Permit("community:admin:sms:type:update")
    public CommonResult updateSmsType(@RequestBody SmsTypeEntity smsTypeEntity){
        return CommonResult.ok(smsTypeService.updateSmsType(smsTypeEntity) ? "修改成功" : "修改失败");
    }
    
    /**
     * @Description: 删除短信分类
     * @author: DKS
     * @since: 2021/12/8 10:58
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @DeleteMapping("/delete")
    @Permit("community:admin:sms:type:delete")
    public CommonResult deleteSmsType(@RequestParam("id") Long id){
        return CommonResult.ok(smsTypeService.deleteSmsType(id) ? "删除成功" : "删除失败");
    }
    
    /**
     * @Description: 查询短信分类列表
     * @author: DKS
     * @since: 2021/12/8 10:58
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/query")
    @Permit("community:admin:sms:type:query")
    public CommonResult selectSmsType(){
        List<SmsTypeEntity> list = smsTypeService.selectSmsType();
        return CommonResult.ok(list,"查询成功");
    }
}
