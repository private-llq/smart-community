package com.jsy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.entity.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.SmsSendRecordQO;
import com.jsy.community.qo.property.SmsAliPayQO;
import com.jsy.community.qo.property.SmsWeChatPayQO;
import com.jsy.community.untils.wechat.OrderNoUtil;
import com.jsy.community.untils.wechat.WechatConfig;
import com.jsy.community.util.HttpUtil;
import com.jsy.community.util.QRCodeGenerator;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyCompanyService propertyCompanyService;
    
    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private AliAppPayService aliAppPayService;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPayConfigureService payConfigureService;
    
    @Resource
    private AlipayUtils alipayUtils;
    
    @Resource
    private RabbitTemplate rabbitTemplate;
    
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
     * @Description: 购买短信套餐微信扫码支付
     * @author: DKS
     * @since: 2021/12/11 16:06
     * @Param: [smsWeChatPayQO]
     * @return: com.jsy.community.vo.CommonResult
     */
    @LoginIgnore
    @PostMapping("/wechat/pay")
    @Transactional(rollbackFor = Exception.class)
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
        req.put("appid", WechatConfig.APPID); //appId
        req.put("mch_id", WechatConfig.MCH_ID); // 商户号
        req.put("nonce_str", nonce_str); // 32位随机字符串
        req.put("body", smsWeChatPayQO.getNumber() + "条短信"); // 商品描述
        req.put("out_trade_no", OrderNoUtil.getOrder()); // 商户订单号
        req.put("total_fee", smsWeChatPayQO.getAmount()); // 标价金额(分)
        req.put("spbill_create_ip", SmsWechatPayUtils.localIp()); // 终端IP
        req.put("notify_url", "http://test.free.svipss.top/sms/wechat/pay/callback"); // 回调地址
        req.put("trade_type", "NATIVE"); // 交易类型
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
        
        // 保存短信购买记录到数据库
        SmsPurchaseRecordEntity entity = new SmsPurchaseRecordEntity();
//        entity.setCompanyId(UserUtils.getAdminCompanyId());
        entity.setCompanyId(1L);
        entity.setOrderNum(req.get("out_trade_no").toString());
        entity.setGoods(smsWeChatPayQO.getNumber());
        entity.setOrderMoney(new BigDecimal(smsWeChatPayQO.getAmount()).divide(new BigDecimal("100")));
//        entity.setPayBy(UserUtils.getUserId());
        entity.setPayBy("1465506950023352321");
        entity.setPayType(1);
        smsPurchaseRecordService.addSmsPurchaseRecord(entity);
        
        // 二维码有效时间两个小时 如果还没支付就自动更改订单为未付款状态
        rabbitTemplate.convertAndSend("exchange_sms_purchase", "queue.sms.purchase", req.get("out_trade_no"), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",60000*120);
                return message;
            }
        });
        
        map.put("code_url", resultMap.get("code_url")); // 支付地址
        map.put("qr_code", qrCode); // 二维码
        map.put("total_fee", smsWeChatPayQO.getAmount()); // 总金额
        map.put("out_trade_no", req.get("out_trade_no"));    // 订单号
        return CommonResult.ok(map);
    }
    
    /**
     * @Description: 短信购买微信扫码支付回调地址
     * @author: DKS
     * @since: 2021/12/13 17:13
     * @Param: [request, response]
     * @return: void
     */
    @LoginIgnore
    @RequestMapping(value = "/wechat/pay/callback", method = {RequestMethod.POST,RequestMethod.GET})
    public void SmsPurchaseWechatCallback(HttpServletRequest request, HttpServletResponse response) throws Exception{
        // 设置微信支付配置
        CompanyPayConfigEntity serviceConfig = companyPayConfigService.getCompanyConfig(0L,1);
        WechatConfig.setConfig(serviceConfig);
        
        // 读取参数
        InputStream inputStream ;
        StringBuilder sb = new StringBuilder();
        inputStream = request.getInputStream();
        String s ;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        while ((s = in.readLine()) != null){
            sb.append(s);
        }
        in.close();
        inputStream.close();
        
        // 解析xml成map
        Map<String, String> m;
        m = SmsWechatPayUtils.doXMLParse(sb.toString());
        
        // 过滤空 设置 TreeMap
        SortedMap<Object,Object> packageParams = new TreeMap<Object,Object>();
        Iterator<String> it = m.keySet().iterator();
        while (it.hasNext()) {
            String parameter = it.next();
            String parameterValue = m.get(parameter);
            String v = "";
            if(null != parameterValue) {
                v = parameterValue.trim();
            }
            packageParams.put(parameter, v);
        }
        // 微信支付的API密钥
        String key = WechatConfig.PRIVATE_KEY;
        log.info("微信支付返回回来的参数："+packageParams);
        // 判断签名是否正确
        if(SmsWechatPayUtils.isTenpaySign("UTF-8", packageParams,key)) {
            //------------------------------
            // 处理业务开始
            //------------------------------
            String resXml;
            if("SUCCESS".equals(packageParams.get("result_code"))){
                // 支付成功
                // 执行自己的业务逻辑开始
                //商户订单号
                String outTradeNo = (String)packageParams.get("out_trade_no");
                //微信生成的交易订单号
                String transactionId = (String)packageParams.get("transaction_id");//微信支付订单号
                
                // 更改订单号支付状态
                SmsPurchaseRecordEntity smsPurchaseRecordEntity = smsPurchaseRecordService.querySmsPurchaseByOrderNum(outTradeNo);
                if (smsPurchaseRecordEntity != null) {
                    if (smsPurchaseRecordEntity.getStatus() == 0) {
                        smsPurchaseRecordEntity.setStatus(1);
                        smsPurchaseRecordEntity.setTransactionId(transactionId);
                        smsPurchaseRecordEntity.setPayTime(LocalDateTime.now());
                        smsPurchaseRecordService.updateSmsPurchase(smsPurchaseRecordEntity);
                    }
                    // 更改短信剩余数量
                    PropertyCompanyEntity companyEntity = propertyCompanyService.selectCompany(smsPurchaseRecordEntity.getCompanyId());
                    companyEntity.setMessageQuantity(companyEntity.getMessageQuantity() + smsPurchaseRecordEntity.getGoods());
                    propertyCompanyService.updatePropertyCompany(companyEntity);
                }
                
                //执行自己的业务逻辑结束
                log.info(outTradeNo + "支付成功");
                //通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                
            } else {
                log.info("支付失败,错误信息：" + packageParams.get("err_code"));
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
            //------------------------------
            //处理业务完毕
            //------------------------------
            BufferedOutputStream out = new BufferedOutputStream(
                response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        } else{
            log.info("签名验证失败");
        }
    }
    
    @LoginIgnore
    @ApiOperation("支付宝电脑网站下单")
    @PostMapping("/alipay/order")
    // @Permit("community:property:sms:alipay:order")
    public CommonResult getOrderStr(@RequestBody SmsAliPayQO smsAliPayQO){
    
        String order = OrderNoUtil.getOrder();
        //TODO 测试金额 0.01
        smsAliPayQO.setAmount("0.01");
        smsAliPayQO.setOutTradeNo(order);
    
        PayConfigureEntity companyConfig = payConfigureService.getCompanyConfig(0L);
        ConstClasses.AliPayDataEntity.setConfig(companyConfig);
        AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
        // 设置请求参数，请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 页面跳转同步通知页面路径
        alipayRequest.setReturnUrl("http://test.free.svipss.top/sms/alipay/return");
        // 服务器异步通知页面路径，在公共参数中设置回跳和通知地址
        alipayRequest.setNotifyUrl("http://test.free.svipss.top/sms/alipay/notify");
    
        // 填充业务参数
        alipayRequest.setBizContent("{" + "\"out_trade_no\":\"" + smsAliPayQO.getOutTradeNo() + "\"," +
            "\"total_amount\":\"" + smsAliPayQO.getAmount() + "\"," +
            "\"subject\":\"" + smsAliPayQO.getSubject() + "条短信" + "\"," +
//			"\"body\":\"" + body + "\"," +
            "\"timeout_express\":\"120m\"" +
//			"\"passback_params\":\"" + passback_params + "\"," +
            "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
    
        AlipayTradePagePayResponse response = null;
        try {
            // 调用SDK生成表单
            response = alipayClient.pageExecute(alipayRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response == null) {
            response = new AlipayTradePagePayResponse();
        }

        if(response.isSuccess()){
            // 保存短信购买记录到数据库
            SmsPurchaseRecordEntity entity = new SmsPurchaseRecordEntity();
//        entity.setCompanyId(UserUtils.getAdminCompanyId());
            entity.setCompanyId(1L);
            entity.setOrderNum(order);
            entity.setGoods(smsAliPayQO.getSubject());
            entity.setOrderMoney(new BigDecimal(smsAliPayQO.getAmount()));
//        entity.setPayBy(UserUtils.getUserId());
            entity.setPayBy("1465506950023352321");
            entity.setPayType(2);
            smsPurchaseRecordService.addSmsPurchaseRecord(entity);
    
            // 有效时间两个小时 如果还没支付就自动更改订单为未付款状态
            rabbitTemplate.convertAndSend("exchange_sms_purchase", "queue.sms.purchase", order, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setHeader("x-delay",60000*120);
                    return message;
                }
            });
        } else {
            return CommonResult.error(JSYError.INTERNAL.getCode(),"下单失败");
        }
        Map<String, String> returnMap = new HashMap<>(1);
        returnMap.put("body",response.getBody());
        return CommonResult.ok(returnMap, "下单成功");
    }
    
    /**
     * @Title: alipayNotify
     * @Description: 支付宝电脑网站支付notify回调接口
     * @author nelson
     * @param request
     * @param outTradeNo 商户订单号
     * @param tradeNo 支付宝交易凭证号
     * @param tradeStatus 交易状态
     * @return String
     * @throws
     */
    @LoginIgnore
    @PostMapping("/alipay/notify")
    private String alipayNotify(HttpServletRequest request, String outTradeNo, String tradeNo, String tradeStatus) {
        Map<String, String> map = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                System.out.println(valueStr);
            }
            map.put(name, valueStr);
        }
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(map, AlipayUtils.getAlipayPublicKey(ConstClasses.AliPayDataEntity.alipayPublicCertPath), "utf-8", "RSA2");
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return ("fail");// 验签发生异常,则直接返回失败
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (signVerified) {
            //处理业务逻辑，更新订单状态
            SmsPurchaseRecordEntity smsPurchaseRecordEntity = smsPurchaseRecordService.querySmsPurchaseByOrderNum(outTradeNo);
            if (smsPurchaseRecordEntity != null) {
                if (smsPurchaseRecordEntity.getStatus() == 0) {
                    smsPurchaseRecordEntity.setStatus(1);
                    smsPurchaseRecordEntity.setTransactionId(tradeNo);
                    smsPurchaseRecordEntity.setPayTime(LocalDateTime.now());
                    smsPurchaseRecordService.updateSmsPurchase(smsPurchaseRecordEntity);
                }
                // 更改短信剩余数量
                PropertyCompanyEntity companyEntity = propertyCompanyService.selectCompany(smsPurchaseRecordEntity.getCompanyId());
                companyEntity.setMessageQuantity(companyEntity.getMessageQuantity() + smsPurchaseRecordEntity.getGoods());
                propertyCompanyService.updatePropertyCompany(companyEntity);
            }
            return ("success");
        } else {
            System.out.println("验证失败,不去更新状态");
            return ("fail");
        }
    }
    
    /**
     * @Title: alipayReturn
     * @Description: 支付宝电脑网站支付return回调接口
     * @author nelson
     * @param request
     * @param outTradeNo 商户订单号
     * @param tradeNo 支付宝交易凭证号
     * @return String
     * @throws
     */
    @LoginIgnore
    @GetMapping("/alipay/return")
    private String alipayReturn(HttpServletRequest request, String outTradeNo, String tradeNo) {
        Map<String, String> map = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                System.out.println(valueStr);
            }
            map.put(name, valueStr);
        }
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(map, AlipayUtils.getAlipayPublicKey(ConstClasses.AliPayDataEntity.alipayPublicCertPath), "utf-8", "RSA2");
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return ("fail");// 验签发生异常,则直接返回失败
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (signVerified) {
            return ("success");
        } else {
            System.out.println("验证失败,不去更新状态");
            return ("fail");
        }
    }
    
    /**
     * 电脑网站支付接口 - 简单参数(PC场景下单并支付，可传递的其它非必要参数可查阅官方文档)
     *
     * @api alipay.trade.page.pay
     * out_trade_no
     *            商户订单号，商户网站订单系统中唯一订单号，必填
     * subject
     *            订单名称，必填
     * total_amount
     *            付款金额，必填
     * body
     *            商品描述，可空
     * timeout_express
     *            该笔订单允许的最晚付款时间，逾期将关闭交易，该参数在请求到支付宝时开始计时。
     *            取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
     *            该参数数值不接受小数点， 如 1.5h，可转换为 90m。
     * passback_params
     *            公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数。支付宝只会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝，
     *            如：merchantBizType%3d3C%26merchantBizNo%3d2016010101111
     */
    @LoginIgnore
    public AlipayTradePagePayResponse simpleParamPagePay(SmsAliPayQO smsAliPayQO) {
        
        PayConfigureEntity companyConfig = payConfigureService.getCompanyConfig(0L);
        ConstClasses.AliPayDataEntity.setConfig(companyConfig);
        AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
        // 设置请求参数，请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 页面跳转同步通知页面路径
        alipayRequest.setReturnUrl("http://test.free.svipss.top/sms/alipay/return");
        // 服务器异步通知页面路径，在公共参数中设置回跳和通知地址
        alipayRequest.setNotifyUrl("http://test.free.svipss.top/sms/alipay/notify");
        
        // 填充业务参数
        alipayRequest.setBizContent("{" + "\"out_trade_no\":\"" + smsAliPayQO.getOutTradeNo() + "\"," +
            "\"total_amount\":\"" + smsAliPayQO.getAmount() + "\"," +
            "\"subject\":\"" + smsAliPayQO.getSubject() + "条短信" + "\"," +
//			"\"body\":\"" + body + "\"," +
            "\"timeout_express\":\"120m\"" +
//			"\"passback_params\":\"" + passback_params + "\"," +
            "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        
        AlipayTradePagePayResponse response = null;
        try {
            // 调用SDK生成表单
            response = alipayClient.pageExecute(alipayRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response == null) {
            response = new AlipayTradePagePayResponse();
        }
        return response;
    }
    
    /**
     * 统一收单线下交易查询
     * @api alipay.trade.query
     * @param outTradeNo
     *            商户订单号，商户网站订单系统中唯一订单号(与支付宝交易号二选一设置)
     * @param tradeNo
     *            支付宝交易号(与商户订单号二选一设置) out_trade_no、trade_no如果同时存在优先取trade_no
     */
    @LoginIgnore
    public AlipayTradeQueryResponse tradeQuery(String outTradeNo, String tradeNo) {
        
        PayConfigureEntity companyConfig = payConfigureService.getCompanyConfig(0L);
        ConstClasses.AliPayDataEntity.setConfig(companyConfig);
        AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
        
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        
        if (!StringUtils.isEmpty(tradeNo)) {
            request.setBizContent(
                "{" + "\"out_trade_no\":\"" + outTradeNo + "\"," + "\"trade_no\":\"" + tradeNo + "\"" + "  }");
        } else if (!StringUtils.isEmpty(outTradeNo)) {
            request.setBizContent("{" + "\"out_trade_no\":\"" + outTradeNo + "\"," + "\"trade_no\":\"\"" + "  }");
        }
        
        AlipayTradeQueryResponse response = null;
        
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response == null) {
            response = new AlipayTradeQueryResponse();
        }
        return response;
    }
    
    /**
     * 统一收单交易退款接口
     *
     * @api alipay.trade.refund
     * @param out_trade_no
     *            商户订单号，商户网站订单系统中唯一订单号（请二选一设置：out_trade_no/trade_no）
     * @param trade_no
     *            支付宝交易号（请二选一设置：out_trade_no/trade_no）
     *            out_trade_no、trade_no如果同时存在优先取trade_no
     * @param refund_amount
     *            需要退款的金额，该金额不能大于订单金额，必填
     * @param refund_reason
     *            退款的原因说明
     * @param out_request_no
     *            标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     */
    @LoginIgnore
    public AlipayTradeRefundResponse refund(String out_trade_no, String trade_no, String refund_amount, String refund_reason, String out_request_no) {
        
        PayConfigureEntity companyConfig = payConfigureService.getCompanyConfig(0L);
        ConstClasses.AliPayDataEntity.setConfig(companyConfig);
        AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
        
        // 设置请求参数
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"trade_no\":\"" + trade_no + "\","
            + "\"refund_amount\":\"" + refund_amount + "\"," + "\"refund_reason\":\"" + refund_reason + "\","
            + "\"out_request_no\":\"" + out_request_no + "\"}");
        
        AlipayTradeRefundResponse response = null;
        try {
            response = alipayClient.execute(alipayRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response == null) {
            response = new AlipayTradeRefundResponse();
        }
        return response;
    }
    
    /**
     * 统一收单交易退款查询接口
     *
     * @api alipay.trade.fastpay.refund.query
     * @param out_trade_no
     *            商户订单号，商户网站订单系统中唯一订单号（请二选一设置：out_trade_no/trade_no）
     * @param trade_no
     *            支付宝交易号（请二选一设置：out_trade_no/trade_no）
     *            out_trade_no、trade_no如果同时存在优先取trade_no
     * @param out_request_no
     *            请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号，必填
     */
    @LoginIgnore
    public AlipayTradeFastpayRefundQueryResponse refundQuery(String out_trade_no, String trade_no, String out_request_no) {
        
        PayConfigureEntity companyConfig = payConfigureService.getCompanyConfig(0L);
        ConstClasses.AliPayDataEntity.setConfig(companyConfig);
        AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
        
        AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
        if (!StringUtils.isEmpty(trade_no)) {
            alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"trade_no\":\"" + trade_no
                + "\"," + "\"out_request_no\":\"" + out_request_no + "\"}");
        } else if (!StringUtils.isEmpty(out_trade_no)) {
            alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"trade_no\":\"\","
                + "\"out_request_no\":\"" + out_request_no + "\"}");
        }
        
        AlipayTradeFastpayRefundQueryResponse response = null;
        try {
            response = alipayClient.execute(alipayRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response == null) {
            response = new AlipayTradeFastpayRefundQueryResponse();
        }
        return response;
    }
    
    /**
     * 统一收单交易关闭接口
     * @api alipay.trade.close
     * @param out_trade_no
     *            商户订单号，商户网站订单系统中唯一订单号（请二选一设置：out_trade_no/trade_no）
     * @param trade_no
     *            支付宝交易号（请二选一设置：out_trade_no/trade_no）
     *            out_trade_no、trade_no如果同时存在优先取trade_no
     */
    @LoginIgnore
    public AlipayTradeCloseResponse close(String out_trade_no, String trade_no) {
        
        PayConfigureEntity companyConfig = payConfigureService.getCompanyConfig(0L);
        ConstClasses.AliPayDataEntity.setConfig(companyConfig);
        AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
        
        AlipayTradeCloseRequest alipay_request = new AlipayTradeCloseRequest();
        AlipayTradeCloseModel model = new AlipayTradeCloseModel();
        model.setOutTradeNo(out_trade_no);
        model.setTradeNo(trade_no);
        alipay_request.setBizModel(model);
        
        AlipayTradeCloseResponse response = null;
        try {
            response = alipayClient.execute(alipay_request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response == null) {
            response = new AlipayTradeCloseResponse();
        }
        return response;
    }
    
    @LoginIgnore
    @ApiOperation("支付宝扫码")
    @PostMapping("/alipay/scan")
    // @Permit("community:property:sms:alipay:scan")
    public CommonResult alipayScan(@RequestBody SmsAliPayQO smsAliPayQO){
    
        PayConfigureEntity companyConfig = payConfigureService.getCompanyConfig(0L);
        ConstClasses.AliPayDataEntity.setConfig(companyConfig);
        AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
        
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        
        request.setNotifyUrl("http://test.free.svipss.top/sms/alipay/scan");
        JSONObject bizContent = new JSONObject();
        String order = OrderNoUtil.getOrder();
        bizContent.put("out_trade_no", order);
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", smsAliPayQO.getSubject() + "条短信");

        //// 商品明细信息，按需传入
        //JSONArray goodsDetail = new JSONArray();
        //JSONObject goods1 = new JSONObject();
        //goods1.put("goods_id", "goodsNo1");
        //goods1.put("goods_name", "子商品1");
        //goods1.put("quantity", 1);
        //goods1.put("price", 0.01);
        //goodsDetail.add(goods1);
        //bizContent.put("goods_detail", goodsDetail);
    
        //// 扩展信息，按需传入
        //JSONObject extendParams = new JSONObject();
        //extendParams.put("sys_service_provider_id", "2088511833207846");
        //bizContent.put("extend_params", extendParams);
    
        //// 结算信息，按需传入
        //JSONObject settleInfo = new JSONObject();
        //JSONArray settleDetailInfos = new JSONArray();
        //JSONObject settleDetail = new JSONObject();
        //settleDetail.put("trans_in_type", "defaultSettle");
        //settleDetail.put("amount", 0.01);
        //settleDetailInfos.add(settleDetail);
        //settleInfo.put("settle_detail_infos", settleDetailInfos);
        //bizContent.put("settle_info", settleInfo);
    
        //// 二级商户信息，按需传入
        //JSONObject subMerchant = new JSONObject();
        //subMerchant.put("merchant_id", "2088000603999128");
        //bizContent.put("sub_merchant", subMerchant);
    
        //// 业务参数信息，按需传入
        //JSONObject businessParams = new JSONObject();
        //businessParams.put("busi_params_key", "busiParamsValue");
        //bizContent.put("business_params", businessParams);
    
        //// 营销信息，按需传入
        //JSONObject promoParams = new JSONObject();
        //promoParams.put("promo_params_key", "promoParamsValue");
        //bizContent.put("promo_params", promoParams);
    
        request.setBizContent(bizContent.toString());
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.certificateExecute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response == null) {
            return CommonResult.error("支付宝扫码支付出错");
        }
        
        if(response.isSuccess()){
            // 保存短信购买记录到数据库
            SmsPurchaseRecordEntity entity = new SmsPurchaseRecordEntity();
//        entity.setCompanyId(UserUtils.getAdminCompanyId());
            entity.setCompanyId(1L);
            entity.setOrderNum(order);
            entity.setGoods(smsAliPayQO.getSubject());
            entity.setOrderMoney(new BigDecimal(smsAliPayQO.getAmount()));
//        entity.setPayBy(UserUtils.getUserId());
            entity.setPayBy("1465506950023352321");
            entity.setPayType(2);
            smsPurchaseRecordService.addSmsPurchaseRecord(entity);
            
            // 有效时间两个小时 如果还没支付就自动更改订单为未付款状态
            rabbitTemplate.convertAndSend("exchange_sms_purchase", "queue.sms.purchase", order, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setHeader("x-delay",60000*120);
                    return message;
                }
            });
        } else {
            return CommonResult.error(JSYError.INTERNAL.getCode(),"扫码支付失败");
        }
        Map<String, String> returnMap = new HashMap<>(2);
        returnMap.put("out_trade_no",response.getOutTradeNo());
        returnMap.put("qr_code",response.getQrCode());
        return CommonResult.ok(returnMap, "扫码支付成功");
    }
    
    @LoginIgnore
    @ApiOperation("支付宝扫码")
    @PostMapping("/alipay/scan2")
    // @Permit("community:property:sms:alipay:scan2")
    public CommonResult alipayScan2(@RequestBody SmsAliPayQO smsAliPayQO) {
    
        PayConfigureEntity companyConfig = payConfigureService.getCompanyConfig(0L);
        ConstClasses.AliPayDataEntity.setConfig(companyConfig);
        AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
    
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        String order = OrderNoUtil.getOrder();
        // 填充业务参数
        request.setBizContent("{" + "\"out_trade_no\":\"" + order + "\"," +
            "\"total_amount\":\"" + smsAliPayQO.getAmount() + "\"," +
            "\"subject\":\"" + smsAliPayQO.getSubject() + "条短信" + "\"," +
//			"\"body\":\"" + body + "\"," +
            "\"timeout_express\":\"120m\"" +
            "\"qr_code_timeout_express\":\"120m\"" +
//			"\"passback_params\":\"" + passback_params + "\"," +
            "\"product_code\":\"FACE_TO_FACE_PAYMENT\"}");
    
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.certificateExecute(request);
            log.info(response.getBody());
        } catch (AlipayApiException e) {
            log.error("支付宝扫码支付错误" + e);
        }
        String qrCode;
        if(response.isSuccess()){
            System.out.println("调用成功");
            qrCode = response.getQrCode();
        } else {
            return CommonResult.error(response.getBody());
        }
        if (StringUtils.isBlank(qrCode)) {
            throw new PropertyException("支付宝获取qrCode为空");
        }
        Map<Object, Object> map = new HashMap<>();
        map.put("qr_code", qrCode); // 二维码
        map.put("total_fee", smsAliPayQO.getAmount()); // 总金额
        map.put("out_trade_no", order);    // 订单号
        return CommonResult.ok(map);
    }
}
