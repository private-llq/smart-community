package com.jsy.community.controller;

import cn.hutool.json.JSONUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarChargeService;
import com.jsy.community.api.ICarMonthlyVehicleService;
import com.jsy.community.api.ICarOrderService;
import com.jsy.community.api.ICompanyPayConfigService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.qo.payment.AliOrderContentQO;
import com.jsy.community.qo.property.orderChargeDto;
import com.jsy.community.untils.wechat.OrderNoUtil;
import com.jsy.community.untils.wechat.PublicConfig;
import com.jsy.community.untils.wechat.WechatConfig;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @program: pay
 * @description:  微信支付控制器
 * @author: Hu
 * @create: 2021-01-21 17:05
 **/
@RestController
// @ApiJSYController
@Slf4j
public class WeChatH5PayController {
    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICompanyPayConfigService companyPayConfigService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarChargeService carChargeService;

    //    private static String unifiedorder_url = "/v3/pay/transactions/jsapi";
    private static String APPID="wxc7e3b98bb50243a6";
    //    //获取openid的地址
//    private static String getopenid_url = "https://api.weixin.qq.com/sns/oauth2/access_token";
//    //公众号的参数
    private static String secret="ffef760d5868be78a88506122ee6bcfa";

    @Value("${notifyUrl}")
    private String notifyUrl;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarOrderService carOrderService;


    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarMonthlyVehicleService carMonthlyVehicleService;
    
    @LoginIgnore
    @PostMapping("/wxH5Pay")
    // @Permit("community:payment:wxH5Pay")
    public CommonResult<Map<String, Object>> wxH5Pay(@RequestBody AliOrderContentQO qo) throws Exception {
        System.out.println(qo);
        //返回前端参数
        Map<String, Object> result = new HashMap<String, Object>();
        //获取微信需要的相关参数
        CompanyPayConfigEntity serviceConfig = companyPayConfigService.getCompanyConfig(qo.getCommunityId(), 2);
        WechatConfig.setConfig(serviceConfig);

        System.out.println("code\n\n"+qo.getCode());
        orderChargeDto orderCharge =carChargeService.orderCharge(qo.getCommunityId(), qo.getCarNumber());
        System.out.println(orderCharge);
        if (orderCharge.getId()==null){
            System.out.println("id悟空");
        }
        //拼接下单所需要的参数
        Map<String, Object> paraMap = new HashMap<String, Object>();
        //参数中：下单金额
        Map<String, Object> amount = new HashMap<String, Object>();
        amount.put("total",1);
        amount.put("currency","CNY");
        //参数中：openid
        Map<String, Object> payer = new HashMap<String, Object>();
        //获取openid
        String openid = PublicConfig.getOpenId(WechatConfig.APPID, WechatConfig.APPID_SECRET, qo.getCode());

        System.out.println(qo+"\n\n");
        payer.put("openid",openid);
        paraMap.put("appid", WechatConfig.APPID);//微信分配的公众账号ID
        paraMap.put("mchid", WechatConfig.MCH_ID);//微信支付分配的商户号
        paraMap.put("out_trade_no",OrderNoUtil.getOrder());//商品的订单号每次要唯一
        paraMap.put("description",qo.getCommunityName()+"-临时停车" + qo.getTime());
        //接收微信支付异步通知回调地址，通知url必须为直接可访问的url  不能携带参数。
        paraMap.put("notify_url", notifyUrl+"/api/v1/payment/callback/H5/"+qo.getCommunityId()+","+orderCharge.getId()); // 此路径是微信服务器调用支付结果通知路径
        paraMap.put("payer",payer);
        paraMap.put("amount",amount);
        paraMap.put("attach",qo.getCommunityName()+"-临时停车" + qo.getTime());

        //将所有参数(map)转xml格式
        String xml = PublicConfig.getXml(paraMap);

        String prepayId = PublicConfig.V3PayGet("/v3/pay/transactions/jsapi",JSONUtil.toJsonStr(paraMap),WechatConfig.MCH_ID,WechatConfig.MCH_SERIAL_NO,WechatConfig.APICLIENT_KEY);
        //第二步获取调起支付的参数
        System.out.println(prepayId);
        JSONObject object = JSONObject.fromObject(PublicConfig.WxTuneUp1(prepayId,WechatConfig.APPID, WechatConfig.APICLIENT_KEY));
        result.put("appId",WechatConfig.APPID);
        result.put("timeStamp",object.get("timestamp"));
        result.put("nonceStr",object.get("noncestr"));
        result.put("package",object.get("prepayid"));
        result.put("signType","RSA");
        result.put("paySign",object.get("sign"));
        result.put("money",1);

        System.out.println("\n\n返回结果\n"+result);
        return CommonResult.ok(result);
    }

    /**
     * @Description: JAapi支付成功回调地址
     * @Param: [request, response]
     * @Return: void
     * @Author: Tian
     * @Date: 2021/10/7-10:02
     **/
    @LoginIgnore
    @RequestMapping(value = "/callback/H5/{companyId}", method = {RequestMethod.POST,RequestMethod.GET})
    // @Permit("community:payment:callback:H5")
    public void callback(HttpServletRequest request, HttpServletResponse response,@PathVariable("companyId") String companyId) throws Exception {
        log.info("\n\n\n\n回调成功\n\n\n\n");
        System.out.println(companyId);
        String[] split = companyId.split(",");
        //回调验证
        CompanyPayConfigEntity configEntity = companyPayConfigService.getCompanyConfig(Long.parseLong(split[0]),2);
        if (Objects.nonNull(configEntity)){
            WechatConfig.setConfig(configEntity);
        }
        //回调验证
        Map<String, String> params = PublicConfig.notify(request ,response, WechatConfig.API_V3_KEY);
        log.info(String.valueOf(params));
//        System.out.println(params);

        //处理业务逻辑
        CarOrderEntity entity;
        //订单号
        entity = carOrderService.selectId(Long.parseLong(split[1]));
        if (entity.getOverdueState()==1){
             carMonthlyVehicleService.UpdateoVerduefee(entity.getCarPlate(), entity.getCommunityId());
        }
        System.out.println("\n\n"+entity);
        //付款金额 单位是分 需要除 100在存入数据库
        String total = params.get("amount");
        BigDecimal divide = new BigDecimal(total).divide(new BigDecimal(100));
        entity.setOrderNum(params.get("out_trade_no"));
        entity.setMoney(divide);//支付金额
        //支付状态
        entity.setOrderStatus(1);//0未支付，1已支付
        //支付类型
        entity.setPayType(4);//1app支付 2物业后台 3支付宝手机H5 4微信公众号

        entity.setRise(params.get("attach"));//商品订单抬头
        //设置支付方式  0扫码  1APP
        entity.setIsPayAnother(0);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            //获取支付时间  2018-06-08T10:34:56+08:00
            date = formatter.parse(params.get("success_time"));
            LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            //支付时间
            entity.setOrderTime(localDateTime);//付款时间

        } catch (ParseException e) {
            e.printStackTrace();
        }
        entity.setOrderNum(params.get("out_trade_no"));//商户订单编号
        entity.setBillNum(params.get("transaction_id"));//微信账单编号
        System.out.println("\n\n\n"+entity);
        boolean b = carOrderService.updateOrderId(entity,Long.parseLong(split[1]));//根据订单号修改订单信息
        System.out.println(b);

    }

    @LoginIgnore
    @GetMapping("ip")
    // @Permit("community:payment:ip")
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

}