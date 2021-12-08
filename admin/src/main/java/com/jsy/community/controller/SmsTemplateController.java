package com.jsy.community.controller;

import com.jsy.community.entity.SmsTemplateEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SmsTemplateQO;
import com.jsy.community.service.ISmsTemplateService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 短信模板
 * @author: DKS
 * @since: 2021/12/8 11:38
 */
@Api(tags = "短信模板")
@RestController
@RequestMapping("/sms/template")
public class SmsTemplateController {
    @Resource
    private ISmsTemplateService smsTemplateService;
    
    /**
     * @Description: 新增短信模板
     * @author: DKS
     * @since: 2021/12/8 11:38
     * @Param: [smsTemplateEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/add")
    @Permit("community:admin:sms:template:add")
    public CommonResult addSmsTemplate(@RequestBody SmsTemplateEntity smsTemplateEntity){
        return CommonResult.ok(smsTemplateService.addSmsTemplate(smsTemplateEntity) ? "添加成功" : "添加失败");
    }
    
    /**
     * @Description: 修改短信模板
     * @author: DKS
     * @since: 2021/12/8 11:38
     * @Param: [smsTemplateEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PutMapping("/update")
    @Permit("community:admin:sms:template:update")
    public CommonResult updateSmsTemplate(@RequestBody SmsTemplateEntity smsTemplateEntity){
        return CommonResult.ok(smsTemplateService.updateSmsTemplate(smsTemplateEntity) ? "修改成功" : "修改失败");
    }
    
    /**
     * @Description: 删除短信模板
     * @author: DKS
     * @since: 2021/12/8 11:38
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @DeleteMapping("/delete")
    @Permit("community:admin:sms:template:delete")
    public CommonResult deleteSmsTemplate(@RequestParam("id") Long id){
        return CommonResult.ok(smsTemplateService.deleteSmsTemplate(id) ? "删除成功" : "删除失败");
    }
    
    /**
     * @Description: 查询短信模板列表
     * @author: DKS
     * @since: 2021/12/8 11:38
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/query")
    @Permit("community:admin:sms:template:query")
    public CommonResult selectSmsTemplate(){
        List<SmsTemplateEntity> list = smsTemplateService.selectSmsTemplate();
        return CommonResult.ok(list,"查询成功");
    }
    
    /**
     * @Description: 短信模板分页查询
     * @author: DKS
     * @since: 2021/12/8 11:52
     * @Param: [baseQO]
     * @return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsTemplateEntity>>
     */
    @PostMapping("/query")
    @Permit("community:admin:sms:template:query")
    public CommonResult<PageInfo<SmsTemplateEntity>> querySmsTemplatePage(@RequestBody BaseQO<SmsTemplateQO> baseQO) {
        return CommonResult.ok(smsTemplateService.querySmsTemplatePage(baseQO));
    }
}
