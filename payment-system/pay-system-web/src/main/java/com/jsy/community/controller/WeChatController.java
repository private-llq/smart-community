package com.jsy.community.controller;

import cn.hutool.json.JSONUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IWeChatService;
import com.jsy.community.config.PublicConfig;
import com.jsy.community.config.WechatConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.payment.WeChatOrderEntity;
import com.jsy.community.qo.payment.WeChatPayQO;
import com.jsy.community.qo.payment.WithdrawalQO;
import com.jsy.community.utils.MyHttpClient;
import com.jsy.community.utils.OrderNoUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.XmlUtil;
import com.jsy.community.vo.CommonResult;
import net.sf.json.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xmlpull.v1.XmlPullParserException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @program: pay
 * @description:  微信支付控制器
 * @author: Hu
 * @create: 2021-01-21 17:05
 **/
@RestController
@ApiJSYController
public class WeChatController {

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private IWeChatService weChatService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private HttpClient httpClient;

    /**
     * @Description: app下单并返回调起支付参数
     * @author: Hu
     * @since: 2021/2/25 14:28
     * @Param:
     * @return:
     */
    @PostMapping("/wxPay")
    public CommonResult wxPay(@RequestBody WeChatPayQO weChatPayQO) throws Exception {
        //支付的请求参数信息(此参数与微信支付文档一致，文档地址：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_2_1.shtml)
        Map hashMap = new LinkedHashMap();
        //hashMap.put("total",weChatPayQO.getAmount().multiply(new BigDecimal(100)));
        hashMap.put("total",1);
        hashMap.put("currency","CNY");
//        LinkedHashMap<String, Object> map4 = new LinkedHashMap<>();
//        map4.put("payer_client_ip",weChatPayQO.getPayerClientIp());

        Map<Object, Object> map = new LinkedHashMap<>();

        map.put("appid", WechatConfig.APPID);
        map.put("mchid",WechatConfig.MCH_ID);
        map.put("description",weChatPayQO.getDescription());
        map.put("out_trade_no", OrderNoUtil.getOrder());
        map.put("notify_url","http://hyf.free.vipnps.vip/api/v1/payment/callback");
        map.put("amount",hashMap);
//        map.put("scene_info",map4);


        String wxPayRequestJsonStr = JSONUtil.toJsonStr(map);

        WeChatOrderEntity msg = new WeChatOrderEntity();
        msg.setId((String) map.get("out_trade_no"));
        msg.setUid(UserUtils.getUserId());
        msg.setOpenId(weChatPayQO.getOpenId());
        msg.setDescription(weChatPayQO.getDescription());
        msg.setAmount(weChatPayQO.getAmount());
        msg.setOrderStatus(1);
        msg.setArriveStatus(1);
        msg.setCreateTime(LocalDateTime.now());


        //mq异步保存账单到数据库
        rabbitTemplate.convertAndSend("exchange_topics_wechat","queue.wechat",msg);
        //半个小时如果还没支付就自动删除数据库账单
        rabbitTemplate.convertAndSend("exchange_delay_wechat", "queue.wechat.delay", map.get("out_trade_no"), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setHeader("x-delay",60000*30);
                return message;
            }
        });
        //第一步获取prepay_id
        String prepayId = PublicConfig.V3PayGet("/v3/pay/transactions/app", wxPayRequestJsonStr, WechatConfig.MCH_ID, WechatConfig.MCH_SERIAL_NO, WechatConfig.APICLIENT_KEY);
        //第二步获取调起支付的参数
        JSONObject object = JSONObject.fromObject(PublicConfig.WxTuneUp(prepayId, WechatConfig.APPID, WechatConfig.APICLIENT_KEY));
        object.put("orderNum",map.get("out_trade_no"));
        return CommonResult.ok(object);
    }
//    @GetMapping("ip")
    public String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("x-forwarded-for");
        if(clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if(clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if(clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    /**
     * @Description: H5下单并返回支付连接
     * @author: Hu
     * @since: 2021/2/25 14:28
     * @Param:
     * @return:
     */
    @PostMapping("/wxPayH5")
    public CommonResult wxPayH5(HttpServletRequest request,@RequestBody WeChatPayQO weChatPayQO) throws Exception {
        //支付的请求参数信息(此参数与微信支付文档一致，文档地址：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_2_1.shtml)
        Map<Object, Object> map = new LinkedHashMap<>();

        System.out.println(getClientIpAddress(request));
        map.put("appid", WechatConfig.APPID);
        map.put("mchid",WechatConfig.MCH_ID);
        map.put("description",weChatPayQO.getDescription());
        map.put("out_trade_no", OrderNoUtil.getOrder());
        map.put("notify_url","http://222.178.212.29:9951/api/v1/payment/callback");
        Map hashMap = new LinkedHashMap();
//        hashMap.put("total",weChatPayQO.getAmount().multiply(new BigDecimal(100)));
        hashMap.put("total",1);
        hashMap.put("currency","CNY");

        Map hashMap1 = new LinkedHashMap();
        hashMap1.put("payer_client_ip",weChatPayQO.getPayerClientIp());
        LinkedHashMap hashMap2 = new LinkedHashMap();
        hashMap2.put("type",weChatPayQO.getType());
        hashMap1.put("h5_info",hashMap2);
        map.put("amount",hashMap);
        map.put("scene_info",hashMap1);


        String wxPayRequestJsonStr = JSONUtil.toJsonStr(map);
        System.out.println(wxPayRequestJsonStr);

        WeChatOrderEntity msg = new WeChatOrderEntity();
        msg.setId((String) map.get("out_trade_no"));
        msg.setUid(UserUtils.getUserId());
        msg.setOpenId(weChatPayQO.getOpenId());
        msg.setDescription(weChatPayQO.getDescription());
        msg.setAmount(weChatPayQO.getAmount());
        msg.setOrderStatus(1);
        msg.setArriveStatus(1);
        msg.setCreateTime(LocalDateTime.now());


        //mq异步保存账单到数据库
        rabbitTemplate.convertAndSend("exchange_topics_wechat","queue.wechat",msg);
        //半个小时如果还没支付就自动删除数据库账单
        rabbitTemplate.convertAndSend("exchange_delay_wechat", "queue.wechat.delay", map.get("out_trade_no"), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setHeader("x-delay",60000*30);
                return message;
            }
        });
        //第一步获取prepay_id
        String h5_url = PublicConfig.V3PayGet("/v3/pay/transactions/h5", wxPayRequestJsonStr, WechatConfig.MCH_ID, WechatConfig.MCH_SERIAL_NO, WechatConfig.APICLIENT_KEY);
        return CommonResult.ok(h5_url);
    }

    /**
     * @Description: 支付成功回调地址
     * @author: Hu
     * @since: 2021/1/26 17:08
     * @Param:
     * @return:
     */
    @RequestMapping(value = "/callback", method = {RequestMethod.POST,RequestMethod.GET})
    public void callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.err.println("回调成功");
        Map<String, String> map = PublicConfig.notify(request, response, WechatConfig.API_V3_KEY);
//        weChatService.saveStatus(out_trade_no);
        System.out.println(map.get("out_trade_no"));
        System.out.println(map.get("transaction_id"));
        weChatService.orderStatus(map);
    }


    /**
     * @Description: 提现
     * @author: Hu
     * @since: 2021/1/29 14:57
     * @Param:
     * @return:
     */
    @PostMapping(value = "/withdrawDeposit")
    @Login
    public CommonResult withdrawDeposit(@RequestBody WithdrawalQO withdrawalQO) throws Exception {

        String body=null;
        Map<String, String> restmap=null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("mch_appid",WechatConfig.APPID);
        map.put("mchid",WechatConfig.MCH_ID);
        map.put("nonce_str",UUID.randomUUID().toString().replace("-", ""));
        map.put("partner_trade_no",OrderNoUtil.txOrder());
        map.put("openid",withdrawalQO.getOpenid());
        map.put("check_name","NO_CHECK");
//        map.put("amount",withdrawalQO.getAmount().multiply(new BigDecimal(100)));
        map.put("amount",1);
        map.put("desc",withdrawalQO.getDesc());
        map.put("sign",PublicConfig.getSignToken(map,WechatConfig.PRIVATE_KEY));

        //System.out.println(XmlUtil.xmlFormat(map, true));

        String xml = PublicConfig.getXml(map);
        HttpPost httpPost = new HttpPost(WechatConfig.TRANSFERS_PAY);
        //装填参数
        StringEntity s = new StringEntity(xml, "UTF-8");
        //设置参数到请求对象中
        httpPost.setEntity(s);

        HttpResponse response = MyHttpClient.getSSLConnectionSocket().execute(httpPost);
        body = EntityUtils.toString(response.getEntity(), "UTF-8");
        restmap = XmlUtil.xmlParse(body);

        if (restmap.equals(null) && "SUCCESS".equals(restmap.get("result_code"))){
            System.out.println("转账成功");
            System.out.println(restmap.get("result_code"));
        }else {
            System.out.println("转账失败");
            System.out.println(restmap.get("err_code") + ":" + restmap.get("err_code_des"));
        }

        return CommonResult.ok(restmap);
    }
    /**
     * @Description:  支付查询
     * @author: Hu
     * @since: 2021/2/1 11:14
     * @Param:
     * @return:
     */
    @GetMapping("/wxPayQuery")
    @Login
    public CommonResult wxPayQuery(@RequestParam("orderId")String orderId){
        String body = "";
        HttpGet httpGet = new HttpGet(WechatConfig.WXPAY_PAY+orderId+""+"?mchid="+WechatConfig.MCH_ID+"");
        httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        httpGet.setHeader("Accept", "application/json");
        CloseableHttpResponse execute=null;
        try {
            execute = MyHttpClient.createHttpClient().execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = execute.getEntity();
        if (entity!=null){
            try {
                body = EntityUtils.toString(entity, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return CommonResult.ok(body);
    }
    /**
     * @Description:  提现查询
     * @author: Hu
     * @since: 2021/2/1 11:14
     * @Param:
     * @return:
     */
    @GetMapping("/withdrawDepositQuery")
    @Login
    public CommonResult withdrawDepositQuery(@RequestParam("orderId")String orderId){
        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("appid",WechatConfig.APPID);
        map.put("mch_id",WechatConfig.MCH_ID);
        map.put("nonce_str",UUID.randomUUID().toString().replace("-", ""));
        map.put("partner_trade_no",orderId);
        map.put("sign",PublicConfig.getSignToken(map,WechatConfig.PRIVATE_KEY));
        String xml = XmlUtil.xmlFormat(map,true);
        HttpPost httpPost = new HttpPost(WechatConfig.QUERY_PAY);
        //装填参数
        StringEntity s = new StringEntity(xml, "UTF-8");
        //设置参数到请求对象中
        httpPost.setEntity(s);

        HttpResponse response = null;
        try {
            response = MyHttpClient.getSSLConnectionSocket().execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String body = null;
        Map<String, String> restmap =null;
        try {
            body = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            restmap = XmlUtil.xmlParse(body);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (restmap!=null&&"SUCCESS".equals(restmap.get("return_code"))){
            System.out.println("查询成功");
            System.out.println(restmap.get("return_code"));
        }else {
            System.out.println("查询失败");
            System.out.println(restmap.get("return_code"));
            System.out.println(restmap.get("return_msg"));
        }
        if ("SUCCESS".equals(restmap.get("result_code"))&&"SUCCESS".equals(restmap.get("return_code"))){
            System.out.println(restmap.get("partner_trade_no"));
            System.out.println(restmap.get("detail_id"));
            System.out.println(restmap.get("status"));
            System.out.println(restmap.get("reason"));
            System.out.println(restmap.get("openid"));
            System.out.println(restmap.get("transfer_name "));
            System.out.println(restmap.get("payment_amount"));
        }else {
            System.out.println(restmap.get("err_code"));
            System.out.println(restmap.get("err_code_des"));
        }

        return CommonResult.ok(restmap);
    }


}
