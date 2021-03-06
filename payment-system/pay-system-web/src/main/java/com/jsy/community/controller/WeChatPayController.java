package com.jsy.community.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderRecordEntity;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.entity.payment.WeChatOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.qo.payment.WeChatPayQO;
import com.jsy.community.qo.payment.WithdrawalQO;
import com.jsy.community.untils.wechat.*;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.BaseTrade;
import com.zhsj.base.api.entity.CreateTradeEntity;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBasePayRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.basecommon.utils.MD5Util;
import com.zhsj.baseweb.annotation.LoginIgnore;
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
 * @description:  ?????????????????????
 * @author: Hu
 * @create: 2021-01-21 17:05
 **/
@RestController
// @ApiJSYController
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

    @Value("${wechatNotifyUrl}")
    private String wechatNotifyUrl;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICompanyPayConfigService companyPayConfigService;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private HousingRentalOrderService housingRentalOrderService;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private AssetLeaseRecordService assetLeaseRecordService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommunityService communityService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserAccountService userAccountService;

    @Autowired
    private RedisTemplate redisTemplate;
    //?????????redis????????????key
    private final String PROPERTY_FEE="PropertyFee:";

    //????????????redis????????????key
    private final String SIGNATURE="Wechat_Lease:";

    @Value("${pay.order.timeout}")
    private int payOrderTimeout;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private HttpClient httpClient;

    @DubboReference(version = com.zhsj.base.api.constant.RpcConst.Rpc.VERSION, group = com.zhsj.base.api.constant.RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBasePayRpcService basePayRpcService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;

    /**
     * @Description: app?????????????????????????????????
     * @author: Hu
     * @since: 2021/2/25 14:28
     * @Param:
     * @return:
     */
    @PostMapping("/wxPay")
    // @Permit("community:payment:wxPay")
    public CommonResult wxPay(@RequestBody WeChatPayQO weChatPayQO) throws Exception {
        String orderNo = OrderNoUtil.getOrder();
        if (weChatPayQO.getTradeFrom() == BusinessEnum.TradeFromEnum.HOUSING_RENTAL.getCode()
                || weChatPayQO.getTradeFrom() == BusinessEnum.TradeFromEnum.SHOPPING_MALL.getCode()){
            weChatPayQO.setCommunityId(1L);
            // ??????????????????
            CreateTradeEntity tradeEntity = new CreateTradeEntity();
            // ???????????????????????????(??????????????????/???????????????????????????)
            tradeEntity.setBusOrderNo(weChatPayQO.getServiceOrderNo());
            // ????????????id
            tradeEntity.setSendUid(UserUtils.getEHomeUserId());
            // ????????????id
            if (weChatPayQO.getTradeFrom() == BusinessEnum.TradeFromEnum.HOUSING_RENTAL.getCode()) {
                AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordService.contractDetail(weChatPayQO.getServiceOrderNo());
                UserDetail userDetail = baseUserInfoRpcService.getUserDetail(assetLeaseRecordEntity.getHomeOwnerUid());
                weChatPayQO.setReceiveUid(userDetail.getId());
            }
            tradeEntity.setReceiveUid(weChatPayQO.getReceiveUid());
            tradeEntity.setCno("RMB");
            tradeEntity.setAmount(weChatPayQO.getAmount());
            tradeEntity.setRemark(UserUtils.getUserInfo().getNickname() + "???" + BusinessEnum.TradeFromEnum.tradeMap.get(weChatPayQO.getTradeFrom()));
            tradeEntity.setType(BusinessEnum.BaseOrderExpensesTypeEnum.getExpenses(weChatPayQO.getTradeFrom()));
            tradeEntity.setTitle(BusinessEnum.TradeFromEnum.tradeMap.get(weChatPayQO.getTradeFrom()));
            tradeEntity.setSource(BusinessEnum.BaseOrderSourceEnum.getSourceByCode(weChatPayQO.getTradeFrom()));
            //??????
            String string = JSON.toJSONString(tradeEntity);
            Map map = JSON.parseObject(string, Map.class);
            map.remove("sign");
            map.put("communicationSecret", BusinessEnum.BaseOrderSourceEnum.getSecretByCode(weChatPayQO.getTradeFrom()));
            String sign = MD5Util.signStr(map);
            tradeEntity.setSign(MD5Util.getMd5Str(sign));
            //??????????????????????????????trade?????????????????????
            BaseTrade trade = basePayRpcService.createTrade(tradeEntity);
            orderNo = trade.getSysOrderNo();
        }
        CommunityEntity entity = communityService.getCommunityNameById(weChatPayQO.getCommunityId());
        CompanyPayConfigEntity serviceConfig = null;
        if (Objects.nonNull(entity)){
            serviceConfig = companyPayConfigService.getCompanyConfig(entity.getPropertyId(),1);
            WechatConfig.setConfig(serviceConfig);
        } else {
            throw new PaymentException("????????????????????????????????????");
        }
        //????????????????????????????????????
        Map hashMap = new LinkedHashMap();
        Map<Object, Object> map = new LinkedHashMap<>();
        //???????????????????????????(??????????????????????????????????????????????????????https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_2_1.shtml)
        map.put("appid", WechatConfig.APPID);
        map.put("mchid",WechatConfig.MCH_ID);
        map.put("description", weChatPayQO.getDescriptionStr());
        map.put("out_trade_no", orderNo);
        map.put("notify_url",wechatNotifyUrl+serviceConfig.getCompanyId());
        map.put("amount",hashMap);
        //hashMap.put("total",weChatPayQO.getAmount().multiply(new BigDecimal(100)));
        hashMap.put("total",1);
        hashMap.put("currency","CNY");

        //??????????????????
        if (weChatPayQO.getTradeFrom()==2){
//            Map<String, Object> objectMap = shoppingMallService.validateShopOrder(weChatPayQO.getOrderData(), UserUtils.getUserToken());
//            if(0 != (int)objectMap.get("code")){
//                throw new JSYException((int)objectMap.get("code"),String.valueOf(objectMap.get("msg")));
//            }

//            map.put("attach",weChatPayQO.getTradeFrom()+","+weChatPayQO.getOrderData().get("uuid"));
            map.put("attach",String.valueOf(weChatPayQO.getTradeFrom()));
        } else
        //?????????????????????
        if (weChatPayQO.getTradeFrom()==4){
            if ("".equals(weChatPayQO.getIds())||weChatPayQO.getIds()==null){
                return CommonResult.error("??????????????????id???????????????");
            }
            if ("".equals(weChatPayQO.getDescriptionStr())||weChatPayQO.getDescriptionStr()==null) {
                map.put("description", "????????????");
            }
//            hashMap.put("total",propertyFinanceOrderService.getTotalMoney(weChatPayQO.getIds()).multiply(new BigDecimal(100)));
            //???????????????????????????id???redis
            redisTemplate.opsForValue().set(PROPERTY_FEE+map.get("out_trade_no"),weChatPayQO.getIds(),payOrderTimeout, TimeUnit.HOURS);
            map.put("attach","4"+","+map.get("out_trade_no")+","+propertyFinanceOrderService.getTotalMoney(weChatPayQO.getIds()));
        } else
        //??????????????????
        if (weChatPayQO.getTradeFrom()==8){
            if ("".equals(weChatPayQO.getServiceOrderNo())||weChatPayQO.getServiceOrderNo()==null){
                return CommonResult.error("??????????????????????????????id???????????????");
            }
            if ("".equals(weChatPayQO.getDescriptionStr())||weChatPayQO.getDescriptionStr()==null) {
                map.put("description", "????????????");
            }
//            hashMap.put("total",propertyFinanceOrderService.getTotalMoney(weChatPayQO.getIds()).multiply(new BigDecimal(100)));
            map.put("attach","8"+","+weChatPayQO.getServiceOrderNo());
        } else
        //????????????????????????
        if (weChatPayQO.getTradeFrom()==9){
            if (weChatPayQO.getServiceOrderNo()==null){
                return CommonResult.error("??????id???????????????");
            }
            WeChatOrderEntity weChatOrderEntity = weChatService.getSignature(weChatPayQO.getServiceOrderNo());
            if (weChatOrderEntity!=null){
                return CommonResult.ok(JSONObject.fromObject(redisTemplate.opsForValue().get(SIGNATURE + weChatPayQO.getServiceOrderNo())));
            } else {
//                hashMap.put("total",propertyFinanceOrderService.getTotalMoney(weChatPayQO.getIds()).multiply(new BigDecimal(100)));
                map.put("attach","9"+","+weChatPayQO.getServiceOrderNo());
            }
        } else
        //??????????????????
        if (weChatPayQO.getTradeFrom()==10){
//            hashMap.put("total",propertyFinanceOrderService.getTotalMoney(weChatPayQO.getIds()).multiply(new BigDecimal(100)));
              map.put("attach","10"+","+weChatPayQO.getServiceOrderNo());

        }
        //???????????????????????????
        WeChatOrderEntity msg = new WeChatOrderEntity();
        msg.setId(String.valueOf(SnowFlake.nextId()));
        msg.setOrderNo((String) map.get("out_trade_no"));
        msg.setUid(UserUtils.getUserId());
        msg.setServiceOrderNo(weChatPayQO.getServiceOrderNo());
        msg.setPayType(weChatPayQO.getTradeFrom());
        msg.setDescription(weChatPayQO.getDescriptionStr());
        msg.setAmount(weChatPayQO.getAmount());
        msg.setOrderStatus(1);
        msg.setArriveStatus(1);
        msg.setCompanyId(serviceConfig.getCompanyId());
        msg.setCreateTime(LocalDateTime.now());

        //mq??????????????????????????????
//        rabbitTemplate.convertAndSend("exchange_topics_wechat","queue.wechat",msg);
        weChatService.insertOrder(msg);
        //????????????????????????????????????????????????????????????
        rabbitTemplate.convertAndSend("exchange_delay_wechat", "queue.wechat.delay", map.get("out_trade_no")+","+entity.getPropertyId(), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setHeader("x-delay",60000*120);
                return message;
            }
        });
        //???????????????prepay_id
        String prepayId = PublicConfig.V3PayGet("/v3/pay/transactions/app", JSONUtil.toJsonStr(map), WechatConfig.MCH_ID, WechatConfig.MCH_SERIAL_NO, WechatConfig.APICLIENT_KEY);
        //????????????????????????????????????
        JSONObject object = JSONObject.fromObject(PublicConfig.WxTuneUp(prepayId, WechatConfig.APPID, WechatConfig.APICLIENT_KEY));
        object.put("orderNum",map.get("out_trade_no"));
        if (weChatPayQO.getTradeFrom()==9){
            redisTemplate.opsForValue().set(SIGNATURE+weChatPayQO.getServiceOrderNo(),JSONUtil.toJsonStr(object),2,TimeUnit.HOURS);
        }
        return CommonResult.ok(object);
    }

    /**
     * @Description: ????????????????????????
     * @author: Hu
     * @since: 2021/1/26 17:08
     * @Param: dsds
     * @return:
     */
    @LoginIgnore
    @RequestMapping(value = "/callback/{companyId}", method = {RequestMethod.POST,RequestMethod.GET})
    // @Permit("community:payment:callback")
    public void callback(HttpServletRequest request, HttpServletResponse response,@PathVariable("companyId") Long companyId) throws Exception {
        log.info("????????????");
        log.info(String.valueOf(companyId));
        CompanyPayConfigEntity configEntity = companyPayConfigService.getCompanyConfig(companyId,1);
        if (Objects.nonNull(configEntity)){
            WechatConfig.setConfig(configEntity);
        }
        log.info("???????????????"+configEntity);
        //????????????
        Map<String, String> map = PublicConfig.notify(request ,response, WechatConfig.API_V3_KEY);
        log.info(String.valueOf(map));
        weChatService.orderStatus(map);
        String orderNo = map.get("out_trade_no");
        WeChatOrderEntity orderEntity = weChatService.getOrderByOrderNo(orderNo);
        UserDetail userDetail = baseUserInfoRpcService.getUserDetail(orderEntity.getUid());
        if (map.get("attach")!=null){
            String[] split = map.get("attach").split(",");
            if (split[0].equals("2")){
                //??????????????????????????????????????????
                basePayRpcService.thirdPay(orderNo, userDetail.getId(), 2, map.get("openid"));
                // shoppingMallService.completeShopOrder(map.get("out_trade_no"),map.get("transaction_id"),2);
                log.info("????????????");
            } else if (split[0].equals("4")){
                //?????????????????????????????????????????????
	            log.info("??????????????????????????????" + orderNo);
                Object ids = redisTemplate.opsForValue().get(PROPERTY_FEE + orderNo);
                if (ids == null){
                    log.error("??????????????????????????????????????????????????????" + orderNo);
                    return;
                }
                String amount = map.get("amount");

                log.info("??????ids???" + String.valueOf(ids).split(","));
                propertyFinanceOrderService.updateOrderStatusBatch(1, orderNo,String.valueOf(ids).split(","),new BigDecimal(amount).divide(new BigDecimal(100)));
                //?????????????????????????????????id
                PropertyFinanceOrderEntity financeOrderEntity = propertyFinanceOrderService.findOne(Long.valueOf(String.valueOf(ids).split(",")[0]));
                PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
                receiptEntity.setCommunityId(financeOrderEntity.getCommunityId());
                receiptEntity.setReceiptNum(OrderNoUtil.getOrder());
                receiptEntity.setTransactionNo(String.valueOf(map.get("transaction_id")));
                receiptEntity.setTransactionType(2);
                receiptEntity.setReceiptMoney(new BigDecimal(split[2]));
                propertyFinanceReceiptService.add(receiptEntity);
                log.info("????????????");
            } else if (split[0].equals("8")){
                //????????????????????????
                CarOrderRecordEntity recordEntity = carService.findOne(Long.valueOf(split[1]));
                recordEntity.setPayType(1);
                recordEntity.setOrderNum(orderNo);
                if (recordEntity!=null){
                    if (recordEntity.getType()==1){
                        carService.bindingMonthCar(recordEntity);
                    }else {
                        carService.renewMonthCar(recordEntity);
                    }
                }
                log.info("????????????");
            } else if (split[0].equals("9")){
                //????????????????????????
                basePayRpcService.thirdPay(orderNo, userDetail.getId(), 2, map.get("openid"));
                /*AssetLeaseRecordEntity leaseRecordEntity = assetLeaseRecordService.queryRecordByConId(split[1]);
                userAccountService.rentalIncome(leaseRecordEntity.getConId(), new BigDecimal(map.get("amount")).divide(new BigDecimal(100)),leaseRecordEntity.getHomeOwnerUid());
                // ??????????????????????????????
                Map<String, Object> houseMap = housingRentalOrderService.completeLeasingOrder(orderNo, split[1]);
                // ??????????????????????????????
                assetLeaseRecordService.updateOperationPayStatus( split[1],1,new BigDecimal(map.get("amount")), orderNo);
                if(0 != (int)houseMap.get("code")){
                    throw new PaymentException((int)houseMap.get("code"),String.valueOf(map.get("msg")));
                }*/
                redisTemplate.delete(SIGNATURE+split[1]);
                log.info("????????????/???????????????????????????????????????????????????" + orderNo);
                log.info("?????????????????????");
            } else
                //??????????????????
                if (split[0].equals("10")){
                    //????????????????????????????????????
                    carService.updateByOrder(split[1],new BigDecimal(map.get("amount")), orderNo,1);
                    log.info("???????????????????????????");
                }
        }
    }

    /**
     * @Description: ??????
     * @author: Hu
     * @since: 2021/1/29 14:57
     * @Param:
     * @return:
     */
    @PostMapping(value = "/withdrawDeposit")
    // @Permit("community:payment:withdrawDeposit")
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
        //????????????
        StringEntity s = new StringEntity(xml, "UTF-8");
        //??????????????????????????????
        httpPost.setEntity(s);

        HttpResponse response = MyHttpClient.getSSLConnectionSocket().execute(httpPost);
        body = EntityUtils.toString(response.getEntity(), "UTF-8");
        restmap = XmlUtil.xmlParse(body);

        if (restmap.equals(null) && "SUCCESS".equals(restmap.get("result_code"))){
            System.out.println("????????????");
            System.out.println(restmap.get("result_code"));
        }else {
            System.out.println("????????????");
            System.out.println(restmap.get("err_code") + ":" + restmap.get("err_code_des"));
        }

        return CommonResult.ok(restmap);
    }
    /**
     * @Description:  ????????????
     * @author: Hu
     * @since: 2021/2/1 11:14
     * @Param:
     * @return:
     */
    @GetMapping("/wxPayQuery")
    // @Permit("community:payment:wxPayQuery")
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
     * @Description:  ????????????
     * @author: Hu
     * @since: 2021/2/1 11:14
     * @Param: orderId
     * @return: CommonResult
     */
    @GetMapping("/withdrawDepositQuery")
    // @Permit("community:payment:withdrawDepositQuery")
    public CommonResult withdrawDepositQuery(@RequestParam("orderId")String orderId){
        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("appid",WechatConfig.APPID);
        map.put("mch_id",WechatConfig.MCH_ID);
        map.put("nonce_str",UUID.randomUUID().toString().replace("-", ""));
        map.put("partner_trade_no",orderId);
        map.put("sign",PublicConfig.getSignToken(map,WechatConfig.PRIVATE_KEY));
        String xml = XmlUtil.xmlFormat(map,true);
        HttpPost httpPost = new HttpPost(WechatConfig.QUERY_PAY);
        //????????????
        StringEntity s = new StringEntity(xml, "UTF-8");
        //??????????????????????????????
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
            System.out.println("????????????");
            System.out.println(restmap.get("return_code"));
        }else {
            System.out.println("????????????");
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

    @LoginIgnore
    @GetMapping("getOrder")
    public CommonResult getOrder(@RequestParam String orderNum){
        return CommonResult.ok(weChatService.getOrderOne(orderNum));
    }

}
