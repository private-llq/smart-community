package com.jsy.community.controller;

import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.entity.SmsPurchaseRecordEntity;
import com.jsy.community.entity.SmsSendRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.SmsSendRecordQO;
import com.jsy.community.qo.property.SmsWeChatPayQO;
import com.jsy.community.untils.wechat.OrderNoUtil;
import com.jsy.community.untils.wechat.WechatConfig;
import com.jsy.community.util.HttpUtil;
import com.jsy.community.util.QRCodeGenerator;
import com.jsy.community.utils.Base64Util;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SmsWechatPayUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @program: com.jsy.community
 * @description: 短信控制器
 * @author: DKS
 * @create: 2021-12-10 10:11
 **/
@Slf4j
@Api(tags = "短信控制器")
@RestController
@RequestMapping("/sms")
public class SmsController {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ISmsSendRecordService smsSendRecordService;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ISmsPurchaseRecordService smsPurchaseRecordService;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ISmsMenuService smsMenuService;
    
    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICompanyPayConfigService companyPayConfigService;
    
    /**
     * @Description: 分页查询短信发送记录
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsSendRecordEntity>>
     * @Author: DKS
     * @Date: 2021/09/08
     **/
    @ApiOperation("分页查询短信发送记录")
    @PostMapping("/send/query")
    @Permit("community:property:sms:send:query")
    public CommonResult<PageInfo<SmsSendRecordEntity>> queryPropertyDeposit(@RequestBody BaseQO<SmsSendRecordQO> baseQO) {
        SmsSendRecordQO query = baseQO.getQuery();
        if(query == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        List<String> adminCommunityIdList = UserUtils.getAdminCommunityIdList();
        return CommonResult.ok(smsSendRecordService.querySmsSendRecord(baseQO, adminCommunityIdList));
    }
    
    /**
     * @Description: 查询短信购买记录
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsPurchaseRecordEntity>>
     * @Author: DKS
     * @Date: 2021/09/14
     **/
    @ApiOperation("查询短信购买记录")
    @PostMapping("/purchase/query")
    @Permit("community:property:sms:purchase:query")
    public CommonResult<List<SmsPurchaseRecordEntity>> queryPropertyDeposit() {
        Long companyId = UserUtils.getAdminCompanyId();
        return CommonResult.ok(smsPurchaseRecordService.querySmsPurchaseRecord(companyId));
    }
    
    /**
     * @Description: 查询短信套餐列表
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsPurchaseRecordEntity>>
     * @Author: DKS
     * @Date: 2021/09/14
     **/
    @PostMapping("/menu/query")
    @Permit("community:property:sms:menu:query")
    public CommonResult selectSmsMenu() {
        return CommonResult.ok(smsMenuService.selectSmsMenu());
    }
    
    /**
     * @Description: 购买短信套餐微信支付
     * @author: DKS
     * @since: 2021/12/11 16:06
     * @Param: [smsWeChatPayQO]
     * @return: com.jsy.community.vo.CommonResult
     */
    @LoginIgnore
    @PostMapping("/wechat/pay")
//    @Permit("community:property:sms:wechat:pay")
    public CommonResult buySmsMenu(@RequestBody SmsWeChatPayQO smsWeChatPayQO) {
        // 设置微信支付配置
        CompanyPayConfigEntity serviceConfig = companyPayConfigService.getCompanyConfig(0L,1);
        WechatConfig.setConfig(serviceConfig);
    
        // 时间字符串
        String currTime = SmsWechatPayUtils.getCurrTime();
        String strTime = currTime.substring(8);
        String strRandom = SmsWechatPayUtils.buildRandom(4) + "";
        String nonce_str = strTime + strRandom;
        
        // 创建请求参数
        SortedMap<Object, Object> req = new TreeMap<>();
        req.put("appid", WechatConfig.APPID);    //appId
        req.put("mch_id", WechatConfig.MCH_ID);  // 商户号
        req.put("nonce_str", nonce_str); // 32位随机字符串
        req.put("body", smsWeChatPayQO.getDescriptionStr()); // 商品描述
        req.put("out_trade_no", OrderNoUtil.getOrder());   // 商户订单号
        req.put("total_fee", smsWeChatPayQO.getAmount());    // 标价金额(分)
        req.put("spbill_create_ip", SmsWechatPayUtils.localIp());   // 终端IP
        req.put("notify_url", "http://www.baidu.com");  // 回调地址
        req.put("trade_type", "NATIVE");    // 交易类型
        req.put("sign", SmsWechatPayUtils.createSign("UTF-8", req, WechatConfig.PRIVATE_KEY));  // 签名
    
        // 生成要发送的 xml
        // 将请求参数转换成String类型
        String requestXML = SmsWechatPayUtils.getRequestXml(req);
        log.info("微信支付请求参数的报文"+requestXML);
    
        // 发送 POST 请求 统一下单 API 并携带 xmlBody 内容,然后获得返回接口结果
        // 解析请求之后的xml参数并且转换成String类型
        String resXml = HttpUtil.postData("https://api.mch.weixin.qq.com/pay/unifiedorder",requestXML);
        log.info("微信支付响应参数的报文"+resXml);
    
        // 将返回结果从 xml 格式转换为 map 格式
        Map resultMap = new HashMap<>();
        // 二维码
        String qrCode = "";
        try {
            resultMap = SmsWechatPayUtils.doXMLParse(resXml);
            if (CollectionUtils.isEmpty(resultMap)) {
                throw new PropertyException("微信支付响应参数的报文解析错误");
            }
            byte[] bytes = QRCodeGenerator.generateQRCode(resultMap.get("code_url").toString(), 300, 300);
            qrCode = Base64Util.byteToBase64(bytes);
        } catch (Exception e) {
            log.error("微信支付响应参数的报文解析成map出错" + resXml + ",map:" + resultMap, e);
        }
        Map<Object, Object> map = new HashMap<>();
        if (CollectionUtils.isEmpty(resultMap)) {
            throw new PropertyException("微信支付响应参数的报文解析错误");
        }
        map.put("code_url", resultMap.get("code_url")); // 支付地址
        map.put("qr_code", qrCode); // 二维码
        map.put("total_fee", smsWeChatPayQO.getAmount()); // 总金额
        map.put("out_trade_no", req.get("out_trade_no"));    // 订单号
        return CommonResult.ok(map);
    }
    // TODO:购买大后台短信套餐支付回调
}
