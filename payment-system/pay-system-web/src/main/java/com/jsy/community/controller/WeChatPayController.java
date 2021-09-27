package com.jsy.community.controller;

import cn.hutool.json.JSONUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.untils.wechat.PublicConfig;
import com.jsy.community.untils.wechat.WechatConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderRecordEntity;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.entity.payment.WeChatOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.payment.WeChatPayQO;
import com.jsy.community.qo.payment.WithdrawalQO;
import com.jsy.community.untils.wechat.MyHttpClient;
import com.jsy.community.untils.wechat.OrderNoUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.untils.wechat.XmlUtil;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.xmlpull.v1.XmlPullParserException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: pay
 * @description:  微信支付控制器
 * @author: Hu
 * @create: 2021-01-21 17:05
 **/
@RestController
@ApiJSYController
@Slf4j
public class WeChatPayController {

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private IWeChatService weChatService;
    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private IShoppingMallService shoppingMallService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceOrderService propertyFinanceOrderService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceReceiptService propertyFinanceReceiptService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService carService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICompanyPayConfigService companyPayConfigService;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private HousingRentalOrderService housingRentalOrderService;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private AssetLeaseRecordService assetLeaseRecordService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommunityService communityService;

    @Autowired
    private RedisTemplate redisTemplate;
    //物业费redis缓存分组key
    private final String PROPERTY_FEE="PropertyFee:";

    //房屋租赁redis支付参数key
    private final String SIGNATURE="Wechat_Lease:";

    @Value("${pay.order.timeout}")
    private int payOrderTimeout;

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
    @Login
    @PostMapping("/wxPay")
    public CommonResult wxPay(@RequestBody WeChatPayQO weChatPayQO) throws Exception {
        if (weChatPayQO.getTradeFrom()==9){
            weChatPayQO.setCommunityId(1L);
        }
        CommunityEntity entity = communityService.getCommunityNameById(weChatPayQO.getCommunityId());
        CompanyPayConfigEntity serviceConfig = null;
        if (Objects.nonNull(entity)){
            serviceConfig = companyPayConfigService.getCompanyConfig(entity.getPropertyId());
            WechatConfig.setConfig(serviceConfig);
        } else {
            throw new PaymentException("当前小区不支持微信支付！");
        }
        //封装微信支付下单请求参数
        Map hashMap = new LinkedHashMap();
        Map<Object, Object> map = new LinkedHashMap<>();
        //支付的请求参数信息(此参数与微信支付文档一致，文档地址：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_2_1.shtml)
        map.put("appid", WechatConfig.APPID);
        map.put("mchid",WechatConfig.MCH_ID);
        map.put("description", weChatPayQO.getDescriptionStr());
        map.put("out_trade_no", OrderNoUtil.getOrder());
        map.put("notify_url","http://tb2korpp.dongtaiyuming.net/api/v1/payment/callback/"+serviceConfig.getCompanyId());
        map.put("amount",hashMap);
        //hashMap.put("total",weChatPayQO.getAmount().multiply(new BigDecimal(100)));
        hashMap.put("total",1);
        hashMap.put("currency","CNY");

        //商城业务逻辑
        if (weChatPayQO.getTradeFrom()==2){
            Map<String, Object> objectMap = shoppingMallService.validateShopOrder(weChatPayQO.getOrderData(), UserUtils.getUserToken());
            if(0 != (int)objectMap.get("code")){
                throw new JSYException((int)objectMap.get("code"),String.valueOf(objectMap.get("msg")));
            }
            map.put("attach",weChatPayQO.getTradeFrom()+","+weChatPayQO.getOrderData().get("uuid"));
        } else
        //物业费业务逻辑
        if (weChatPayQO.getTradeFrom()==4){
            if ("".equals(weChatPayQO.getIds())||weChatPayQO.getIds()==null){
                return CommonResult.error("物业缴费账单id不能为空！");
            }
            if ("".equals(weChatPayQO.getDescriptionStr())||weChatPayQO.getDescriptionStr()==null) {
                map.put("description", "物业缴费");
            }
//            hashMap.put("total",propertyFinanceOrderService.getTotalMoney(weChatPayQO.getIds()).multiply(new BigDecimal(100)));
            //缓存物业缴费的账单id到redis
            redisTemplate.opsForValue().set(PROPERTY_FEE+map.get("out_trade_no"),weChatPayQO.getIds(),payOrderTimeout, TimeUnit.HOURS);
            map.put("attach",4+","+map.get("out_trade_no")+","+propertyFinanceOrderService.getTotalMoney(weChatPayQO.getIds()));
        } else
        //停车缴费逻辑
        if (weChatPayQO.getTradeFrom()==8){
            if ("".equals(weChatPayQO.getServiceOrderNo())||weChatPayQO.getServiceOrderNo()==null){
                return CommonResult.error("车位缴费临时订单记录id不能为空！");
            }
            if ("".equals(weChatPayQO.getDescriptionStr())||weChatPayQO.getDescriptionStr()==null) {
                map.put("description", "车位缴费");
            }
//            hashMap.put("total",propertyFinanceOrderService.getTotalMoney(weChatPayQO.getIds()).multiply(new BigDecimal(100)));
            map.put("attach",8+","+weChatPayQO.getServiceOrderNo());
        } else
        //房屋租赁业务逻辑
        if (weChatPayQO.getTradeFrom()==9){
            if (weChatPayQO.getServiceOrderNo()==null){
                return CommonResult.error("合同id不能为空！");
            }
            WeChatOrderEntity weChatOrderEntity = weChatService.getSignature(weChatPayQO.getServiceOrderNo());
            if (weChatOrderEntity!=null){
                return CommonResult.ok(JSONObject.fromObject(redisTemplate.opsForValue().get(SIGNATURE + weChatPayQO.getServiceOrderNo())));
            } else {
//                hashMap.put("total",propertyFinanceOrderService.getTotalMoney(weChatPayQO.getIds()).multiply(new BigDecimal(100)));
                map.put("attach",9+","+weChatPayQO.getServiceOrderNo());
            }
        }
        //新增数据库订单记录
        WeChatOrderEntity msg = new WeChatOrderEntity();
        msg.setId((String) map.get("out_trade_no"));
        msg.setUid(UserUtils.getUserId());
        msg.setServiceOrderNo(weChatPayQO.getServiceOrderNo());
        msg.setPayType(weChatPayQO.getTradeFrom());
        msg.setDescription(weChatPayQO.getDescriptionStr());
        msg.setAmount(weChatPayQO.getAmount());
        msg.setOrderStatus(1);
        msg.setArriveStatus(1);
        msg.setCompanyId(serviceConfig.getCompanyId());
        msg.setCreateTime(LocalDateTime.now());

        //mq异步保存账单到数据库
//        rabbitTemplate.convertAndSend("exchange_topics_wechat","queue.wechat",msg);
        weChatService.insertOrder(msg);
        //半个小时如果还没支付就自动删除数据库账单
        rabbitTemplate.convertAndSend("exchange_delay_wechat", "queue.wechat.delay", map.get("out_trade_no")+","+entity.getPropertyId(), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setHeader("x-delay",60000*120);
                return message;
            }
        });
        //第一步获取prepay_id
        String prepayId = PublicConfig.V3PayGet("/v3/pay/transactions/app", JSONUtil.toJsonStr(map), WechatConfig.MCH_ID, WechatConfig.MCH_SERIAL_NO, WechatConfig.APICLIENT_KEY);
        //第二步获取调起支付的参数
        JSONObject object = JSONObject.fromObject(PublicConfig.WxTuneUp(prepayId, WechatConfig.APPID, WechatConfig.APICLIENT_KEY));
        object.put("orderNum",map.get("out_trade_no"));
        if (weChatPayQO.getTradeFrom()==9){
            redisTemplate.opsForValue().set(SIGNATURE+weChatPayQO.getServiceOrderNo(),JSONUtil.toJsonStr(object),2,TimeUnit.HOURS);
        }
        return CommonResult.ok(object);
    }

    /**
     * @Description: 支付成功回调地址
     * @author: Hu
     * @since: 2021/1/26 17:08
     * @Param: dsds
     * @return:
     */
    @RequestMapping(value = "/callback/{companyId}", method = {RequestMethod.POST,RequestMethod.GET})
    public void callback(HttpServletRequest request, HttpServletResponse response,@PathVariable("companyId") Long companyId) throws Exception {
        log.info("回调成功");
        log.info(String.valueOf(companyId));
        CompanyPayConfigEntity configEntity = companyPayConfigService.getCompanyConfig(companyId);
        if (Objects.nonNull(configEntity)){
            WechatConfig.setConfig(configEntity);
        }
        log.info("配置参数："+configEntity);
        //回调验证
        Map<String, String> map = PublicConfig.notify(request ,response, WechatConfig.API_V3_KEY);
        log.info(String.valueOf(map));
        weChatService.orderStatus(map);
        if (map.get("attach")!=null){
            String[] split = map.get("attach").split(",");
            //处理商城支付回调后的业务逻辑
            if (split[0].equals("2")){
                shoppingMallService.completeShopOrder(split[1]);
                log.info("处理完成");
            } else
            //处理物业费支付回调后的业务逻辑
            if (split[0].equals("4")){
	            log.info("开始处理物业费订单：" + map.get("out_trade_no"));
                Object ids = redisTemplate.opsForValue().get(PROPERTY_FEE + map.get("out_trade_no"));
                if (ids == null){
                    log.error("微信物业费订单回调处理异常，订单号：" + map.get("out_trade_no"));
                    return;
                }
                log.info("账单ids：" + String.valueOf(ids).split(","));
                propertyFinanceOrderService.updateOrderStatusBatch(1,map.get("out_trade_no"),String.valueOf(ids).split(","));
                //获取一条账单，得到社区id
                PropertyFinanceOrderEntity financeOrderEntity = propertyFinanceOrderService.findOne(Long.valueOf(String.valueOf(ids).split(",")[0]));
                PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
                receiptEntity.setCommunityId(financeOrderEntity.getCommunityId());
                receiptEntity.setReceiptNum(OrderNoUtil.getOrder());
                receiptEntity.setTransactionNo(String.valueOf(map.get("transaction_id")));
                receiptEntity.setTransactionType(2);
                receiptEntity.setReceiptMoney(new BigDecimal(split[2]));
                propertyFinanceReceiptService.add(receiptEntity);
                log.info("处理完成");
            } else
            //停车缴费后记业务
            if (split[0].equals("8")){
                CarOrderRecordEntity recordEntity = carService.findOne(Long.valueOf(split[1]));
                recordEntity.setPayType(1);
                recordEntity.setOrderNum(map.get("out_trade_no"));
                if (recordEntity!=null){
                    if (recordEntity.getType()==1){
                        carService.bindingMonthCar(recordEntity);
                    }else {
                        carService.renewMonthCar(recordEntity);
                    }
                }
                log.info("处理完成");
            } else
            //房屋租赁业务逻辑
            if (split[0].equals("9")){
                // 修改签章合同支付状态
                Map<String, Object> houseMap = housingRentalOrderService.completeLeasingOrder(map.get("out_trade_no"), split[1]);
                // 修改租房签约支付状态
                assetLeaseRecordService.updateOperationPayStatus( split[1]);
                if(0 != (int)houseMap.get("code")){
                    throw new PaymentException((int)houseMap.get("code"),String.valueOf(map.get("msg")));
                }
                redisTemplate.delete(SIGNATURE+split[1]);
                log.info("房屋押金/房租缴费订单状态修改完成，订单号：" + map.get("out_trade_no"));
                log.info("租赁处理完成！");
            }
        }
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
    public CommonResult<Map<String, String>> withdrawDeposit(@RequestBody WithdrawalQO withdrawalQO) throws Exception {

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
     * @Param: orderId
     * @return: CommonResult
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
            System.out.println(restmap.get("transfer_name"));
            System.out.println(restmap.get("payment_amount"));
        }else {
            System.out.println(restmap.get("err_code"));
            System.out.println(restmap.get("err_code_des"));
        }

        return CommonResult.ok(restmap);
    }
//    @GetMapping("ip")
//    public String getClientIpAddress(HttpServletRequest request) {
//        String clientIp = request.getHeader("x-forwarded-for");
//        if(clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
//            clientIp = request.getHeader("Proxy-Client-IP");
//        }
//        if(clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
//            clientIp = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if(clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
//            clientIp = request.getRemoteAddr();
//        }
//        return clientIp;
//    }

    /**
     * @Description: H5下单并返回支付连接
     * @author: Hu
     * @since: 2021/2/25 14:28
     * @Param:
     * @return:
     */
//    @PostMapping("/wxPayH5")
//    public CommonResult wxPayH5(HttpServletRequest request,@RequestBody WeChatPayQO weChatPayQO) throws Exception {
//        //支付的请求参数信息(此参数与微信支付文档一致，文档地址：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_2_1.shtml)
//        Map<Object, Object> map = new LinkedHashMap<>();
//
//        System.out.println(getClientIpAddress(request));
//        map.put("appid", WechatConfig.APPID);
//        map.put("mchid",WechatConfig.MCH_ID);
//        map.put("description",weChatPayQO.getDescription());
//        map.put("out_trade_no", OrderNoUtil.getOrder());
//        map.put("notify_url","http://222.178.212.29:9951/api/v1/payment/callback");
//        Map hashMap = new LinkedHashMap();
////        hashMap.put("total",weChatPayQO.getAmount().multiply(new BigDecimal(100)));
//        hashMap.put("total",1);
//        hashMap.put("currency","CNY");
//
//        Map hashMap1 = new LinkedHashMap();
//        hashMap1.put("payer_client_ip",weChatPayQO.getPayerClientIp());
//        LinkedHashMap hashMap2 = new LinkedHashMap();
//        hashMap2.put("type",weChatPayQO.getType());
//        hashMap1.put("h5_info",hashMap2);
//        map.put("amount",hashMap);
//        map.put("scene_info",hashMap1);
//
//
//        String wxPayRequestJsonStr = JSONUtil.toJsonStr(map);
//        System.out.println(wxPayRequestJsonStr);
//
//        WeChatOrderEntity msg = new WeChatOrderEntity();
//        msg.setId((String) map.get("out_trade_no"));
//        msg.setUid(UserUtils.getUserId());
//        msg.setOpenId(weChatPayQO.getOpenId());
//        msg.setDescription(weChatPayQO.getDescription());
//        msg.setAmount(weChatPayQO.getAmount());
//        msg.setOrderStatus(1);
//        msg.setArriveStatus(1);
//        msg.setCreateTime(LocalDateTime.now());
//
//
//        //mq异步保存账单到数据库
//        rabbitTemplate.convertAndSend("exchange_topics_wechat","queue.wechat",msg);
//        //半个小时如果还没支付就自动删除数据库账单
//        rabbitTemplate.convertAndSend("exchange_delay_wechat", "queue.wechat.delay", map.get("out_trade_no"), new MessagePostProcessor() {
//            @Override
//            public Message postProcessMessage(Message message) throws AmqpException {
//                    message.getMessageProperties().setHeader("x-delay",60000*30);
//                return message;
//            }
//        });
//        //第一步获取prepay_id
//        String h5_url = PublicConfig.V3PayGet("/v3/pay/transactions/h5", wxPayRequestJsonStr, WechatConfig.MCH_ID, WechatConfig.MCH_SERIAL_NO, WechatConfig.APICLIENT_KEY);
//        return CommonResult.ok(h5_url);
//    }

}