package com.jsy.community.controller;

import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
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
     * @Description: 购买短信套餐微信支付
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
     * @Description: 短信购买微信支付回调地址
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
        String key = WechatConfig.API_V3_KEY;
        
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
                //支付完成时间
                String timeEnd = (String)packageParams.get("time_end");
                
                // 更改订单号支付状态
                SmsPurchaseRecordEntity smsPurchaseRecordEntity = smsPurchaseRecordService.querySmsPurchaseByOrderNum(outTradeNo);
                if (smsPurchaseRecordEntity != null) {
                    if (smsPurchaseRecordEntity.getStatus() == 0) {
                        smsPurchaseRecordEntity.setStatus(1);
                        smsPurchaseRecordEntity.setTransactionId(transactionId);
                        smsPurchaseRecordEntity.setPayTime(timeEnd);
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
}
