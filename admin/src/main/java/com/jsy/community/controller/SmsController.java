package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.jsy.community.entity.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SmsPurchaseRecordQO;
import com.jsy.community.qo.sys.SmsQO;
import com.jsy.community.qo.sys.SmsTemplateQO;
import com.jsy.community.service.*;
import com.jsy.community.utils.MyHttpUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 目前仅用于内部测试
 * @author YuLF
 * @since 2021-02-25 16:58
 */
@Api(tags = "短信控制器")
@Slf4j
@RestController
@ConditionalOnProperty( value = "jsy.enable-dev-sms", havingValue = "true")
@RequestMapping("/sms")
// @ApiJSYController
public class SmsController {

    private static final String HOST = "http://smsbanling.market.alicloudapi.com";
    private static final String PATH = "/smsapis";
    private static final String APP_CODE = "abfc59f0cdbc4c038a2e804f9e9e37de";

    @Resource(name = "adminRedisTemplate")
    private RedisTemplate<String, Object> adminRedisTemplate;
    
    @Resource
    private ISmsService smsService;
    
    @Resource
    private ISmsTypeService smsTypeService;
    
    @Resource
    private ISmsTemplateService smsTemplateService;
    
    @Resource
    private ISmsMenuService smsMenuService;
    
    @Resource
    private ISmsPurchaseRecordService smsPurchaseRecordService;

    /**
     * 内部测试、暂无任何拦截及 短信在一定时间内 不能二次发送
     * @param qo    请求参数
     */
    @LoginIgnore
    @PostMapping("/send")
    public CommonResult<Boolean> send(@RequestBody SmsQO qo){

        ValidatorUtils.validateEntity(qo, SmsQO.SendSmsValid.class);
        //指定请求参数
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "APPCODE " + APP_CODE);
        Map<String, String> queryParam = new HashMap<>(3);
        queryParam.put("mobile", qo.getMobile());
        String verifyCode = getSpecifyRandomString(6);
        queryParam.put("msg", "你的验证码是 " + verifyCode + " 有效期" + qo.getExpire() + "秒!");
        queryParam.put("sign", qo.getSign());

        //存入redis
        adminRedisTemplate.opsForValue().set(qo.getRedisPrefix() + qo.getMobile() , verifyCode);
        Boolean expire = adminRedisTemplate.expire(qo.getRedisPrefix() + qo.getMobile(), qo.getExpire(), TimeUnit.SECONDS);

        if( Objects.isNull(expire)  ){
            return CommonResult.error("发送失败!");
        }
        //发送短信
        HttpGet httpGet = MyHttpUtils.httpGet(HOST + PATH, queryParam);
        MyHttpUtils.setHeader(httpGet,headers);
        String result = (String) MyHttpUtils.exec(httpGet, 1);
        //验证结果
        if ( Objects.isNull(result) ){
            return CommonResult.error("发送失败!");
        }
        Integer resultCode = JSON.parseObject(result).getInteger("result");
        if( resultCode != 0 ){
            return CommonResult.error("发送失败!");
        }
        return CommonResult.ok("发送成功!");
    }

    private static String getSpecifyRandomString(int length)
    {
        String charList = "0123456789";
        StringBuilder rev = new StringBuilder();
        Random f = new Random();
        for(int i=0;i<length;i++)
        {
            rev.append(charList.charAt(Math.abs(f.nextInt()) % charList.length()));
        }
        return rev.toString();
    }
    
    /**
     * @Description: 新增或修改短信配置
     * @author: DKS
     * @since: 2021/12/6 11:06
     * @Param: [smsEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/add/setting")
    @Permit("community:admin:sms:add:setting")
    public CommonResult addSmsSetting(@RequestBody SmsEntity smsEntity){
        return smsService.addSmsSetting(smsEntity) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
    }
    
    /**
     * @Description: 查询短信配置
     * @author: DKS
     * @since: 2021/12/6 11:49
     * @Param: [sysType]
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/query/setting")
    @Permit("community:admin:sms:query:setting")
    public CommonResult querySmsSetting(){
        return CommonResult.ok(smsService.querySmsSetting());
    }
    
    /**
     * @Description: 新增短信分类
     * @author: DKS
     * @since: 2021/12/8 10:42
     * @Param: [smsTypeEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/type/add")
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
    @PutMapping("/type/update")
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
    @DeleteMapping("/type/delete")
    @Permit("community:admin:sms:type:delete")
    public CommonResult deleteSmsType(@RequestParam("id") List<Long> id){
        return CommonResult.ok(smsTypeService.deleteSmsType(id) ? "删除成功" : "删除失败");
    }
    
    /**
     * @Description: 查询短信分类列表
     * @author: DKS
     * @since: 2021/12/8 10:58
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/type/query")
    @Permit("community:admin:sms:type:query")
    public CommonResult selectSmsType(){
        List<SmsTypeEntity> list = smsTypeService.selectSmsType();
        return CommonResult.ok(list);
    }
    
    /**
     * @Description: 新增短信模板
     * @author: DKS
     * @since: 2021/12/8 11:38
     * @Param: [smsTemplateEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/template/add")
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
    @PutMapping("/template/update")
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
    @DeleteMapping("/template/delete")
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
    @GetMapping("/template/query")
    @Permit("community:admin:sms:template:query")
    public CommonResult selectSmsTemplate(){
        List<SmsTemplateEntity> list = smsTemplateService.selectSmsTemplate();
        return CommonResult.ok(list);
    }
    
    /**
     * @Description: 短信模板分页查询
     * @author: DKS
     * @since: 2021/12/8 11:52
     * @Param: [baseQO]
     * @return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsTemplateEntity>>
     */
    @PostMapping("/template/query")
    @Permit("community:admin:sms:template:query")
    public CommonResult<PageInfo<SmsTemplateEntity>> querySmsTemplatePage(@RequestBody BaseQO<SmsTemplateQO> baseQO) {
        return CommonResult.ok(smsTemplateService.querySmsTemplatePage(baseQO));
    }
    
    /**
     * @Description: 新增短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:11
     * @Param: [smsMenuEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/menu/add")
    @Permit("community:admin:sms:menu:add")
    public CommonResult addSmsMenu(@RequestBody SmsMenuEntity smsMenuEntity){
        ValidatorUtils.validateEntity(smsMenuEntity);
        return CommonResult.ok(smsMenuService.addSmsMenu(smsMenuEntity) ? "添加成功" : "添加失败");
    }
    
    /**
     * @Description: 修改短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:11
     * @Param: [smsMenuEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PutMapping("/menu/update")
    @Permit("community:admin:sms:menu:update")
    public CommonResult updateSmsMenu(@RequestBody SmsMenuEntity smsMenuEntity){
        ValidatorUtils.validateEntity(smsMenuEntity);
        return CommonResult.ok(smsMenuService.updateSmsMenu(smsMenuEntity) ? "修改成功" : "修改失败");
    }
    
    /**
     * @Description: 删除短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:11
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @DeleteMapping("/menu/delete")
    @Permit("community:admin:sms:menu:delete")
    public CommonResult deleteSmsMenu(@RequestParam("id") List<Long> id){
        return CommonResult.ok(smsMenuService.deleteSmsMenu(id) ? "删除成功" : "删除失败");
    }
    
    /**
     * @Description: 查询短信套餐列表
     * @author: DKS
     * @since: 2021/12/9 11:11
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/menu/query")
    @Permit("community:admin:sms:menu:query")
    public CommonResult selectSmsMenu(){
        List<SmsMenuEntity> list = smsMenuService.selectSmsMenu();
        return CommonResult.ok(list);
    }
    
    /**
     * @Description: 查询短信购买记录
     * @Param: [smsPurchaseRecordQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsPurchaseRecordEntity>>
     * @Author: DKS
     * @Date: 2021/12/9
     **/
    @PostMapping("/purchase/query")
    @Permit("community:admin:sms:purchase:query")
    public CommonResult<PageInfo<SmsPurchaseRecordEntity>> queryPropertyDeposit(@RequestBody BaseQO<SmsPurchaseRecordQO> smsPurchaseRecordQO) {
        return CommonResult.ok(smsPurchaseRecordService.querySmsPurchaseRecord(smsPurchaseRecordQO));
    }
    
    /**
     * @Description: 批量删除短信购买记录
     * @author: DKS
     * @since: 2021/12/9 17:05
     * @Param: [ids]
     * @return: com.jsy.community.vo.CommonResult
     */
    @DeleteMapping("/purchase/deleteIds")
    @Permit("community:admin:sms:purchase:deleteIds")
    public CommonResult deleteIds(@RequestParam("ids") List<Long> ids) {
        return CommonResult.ok(smsPurchaseRecordService.deleteIds(ids) ? "删除成功" : "删除失败");
    }
}
