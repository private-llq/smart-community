package com.jsy.community.controller;

import cn.hutool.json.JSONUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderRecordEntity;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.entity.payment.WeChatOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.payment.WeChatH5PayQO;
import com.jsy.community.qo.payment.WeChatPayQO;
import com.jsy.community.qo.payment.WithdrawalQO;
import com.jsy.community.untils.wechat.*;
import com.jsy.community.utils.UserUtils;
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
import java.net.URLEncoder;
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
public class WeChatH5PayController {
    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICompanyPayConfigService companyPayConfigService;
    private static String unifiedorder_url = "/pay/unifiedorder";

//    @Login
    @PostMapping("/wxH5Pay")
    public CommonResult<Map<String, Object>> wxH5Pay(HttpServletRequest request,String code) throws Exception {
            //拼接下单地址参数
            Map<String, Object> paraMap = new HashMap<String, Object>();
            //下单金额
            Map<String, Object> amount = new HashMap<String, Object>();
            amount.put("total",1);
            amount.put("currency","CNY");
            //下单金额
            Map<String, Object> payer = new HashMap<String, Object>();
            //返回参数
            Map<String, Object> result = new HashMap<String, Object>();
            //获取ip地址
            String ip = getClientIpAddress(request);
            //获取微信需要的相关参数
            CompanyPayConfigEntity serviceConfig = companyPayConfigService.getCompanyConfig(1l);
            WechatConfig.setConfig(serviceConfig);

            //获取openid的地址
            String getopenid_url = "https://api.weixin.qq.com/sns/oauth2/access_token";
            //公众号的参数
            String secret="";
      //  @RequestBody WeChatH5PayQO weChatH5PayQO,


        //拼接相关参数
        String  param="appid="+WechatConfig.APPID+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";

        //发送请求 得到openId
        String openid = HttpRequest.sendGet(getopenid_url,param);
        payer.put("openid",openid);

            paraMap.put("appid", WechatConfig.APPID);//微信分配的公众账号ID
            paraMap.put("mch_id", WechatConfig.MCH_ID);//微信支付分配的商户号
//            paraMap.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));//随机字符串，不长于32位。推荐随机数生成算法
//            paraMap.put("body", "E到家-停车缴费");//商品描述
            paraMap.put("out_trade_no","20150806125346");//商品的订单号每次要唯一
//            paraMap.put("total_fee", "1");//订单总金额，单位为分，详见支付金额
//            paraMap.put("spbill_create_ip", ip);//必须传正确的用户端IP
            paraMap.put("description","Image形象店-深圳腾大-QQ公仔");
            //接收微信支付异步通知回调地址，通知url必须为直接可访问的url  不能携带参数。
            paraMap.put("notify_url", "http://baidu.com"); // 此路径是微信服务器调用支付结果通知路径
            paraMap.put("payer",payer);
//            paraMap.put("trade_type", "JSAPI");//JSAPI支付
//            paraMap.put("scene_info", "{\"h5_info\":{\"type\":\"Wap\",\"wap_url\":\"https://zhsj.co\",\"wap_name\": \"订单支付\"}}");

//            //生成签名
//            String sign = PublicConfig.getSignToken(paraMap, WechatConfig.PRIVATE_KEY);
//            paraMap.put("sign",sign);
//            paraMap.put("openid",openid);

          //将所有参数(map)转xml格式
            String xml = PublicConfig.getXml(paraMap);
            System.out.println("ip"+ip);
            System.out.println("xml"+xml);

//            StringEntity s = new StringEntity(xml, "UTF-8");//将xml转换为实体类
//            Map map = new HashMap();
//
//            String mweb_url = "";
        String prepayId = PublicConfig.V3PayGet(unifiedorder_url,JSONUtil.toJsonStr(paraMap),WechatConfig.MCH_ID,WechatConfig.MCH_SERIAL_NO,WechatConfig.APICLIENT_KEY);
        //第二步获取调起支付的参数
        JSONObject object = JSONObject.fromObject(PublicConfig.WxTuneUp(prepayId, WechatConfig.APPID, WechatConfig.APICLIENT_KEY));
        object.put("msg","后台相应成功");
        System.out.println("微信返回参数m"+object);
//            //统一下单
//            try {
                //将map参数转为xml格式的字符串 像微信发送请求  获取map参数
//                map = WebUtils.getMwebUrl(unifiedorder_url, xml);


//                String return_code = (String) map.get("return_code");
//                String return_msg = (String) map.get("return_msg");
//
//                if ("SUCCESS".equals(return_code) && "OK".equals(return_msg)) {
//                    System.out.println("请求成功");
//                    System.out.println("mweb_url=" + mweb_url);
//                } else {
//                    System.out.println("统一支付接口获取预支付订单出错");
//                    result.put("msg", "支付错误");
//                }
//                //当俩个结果都成功是  才能拿到mweb_url
//                if ("SUCCESS".equals(map.get("result_code"))&&"SUCCESS".equals(map.get("return_code"))){
//                    System.out.println("返回参数没问题");
//                    String prepay_id = (String) map.get("prepay_id");
//                    String trade_type = (String) map.get("trade_type");
//                    String nonce_str = (String) map.get("nonce_str");
//                    String sign1 = (String) map.get("sign");
//                    String appid = (String) map.get("appid");
//                    JSONObject jsonObject = PublicConfig.WxTuneUp(prepay_id, appid, WechatConfig.APICLIENT_KEY);
//                    mweb_url = (String) map.get("mweb_url");//调微信支付接口地址
//                    System.out.println("mweb_url="+mweb_url);
////                    //支付完返回浏览器跳转的地址，如跳到查看订单页面   重定向地址  需要拼接到后面返给前端
////                    String redirect_url = "https://music.163.com/";
////                    String redirect_urlEncode =  URLEncoder.encode(redirect_url,"utf-8");//对上面地址urlencode
////
////                mweb_url = mweb_url + "&redirect_url="+redirect_urlEncode;//拼接返回地址
//
//                    result.put("prepay_id",prepay_id);//交易标识
//                    result.put("trade_type",trade_type);//交易类型
//                    result.put("nonce_str",nonce_str);//随机字串
//                    result.put("sign",sign1);//签名
//                    result.put("appid",appid);//appid
//                    result.put("msg","支付成功");
//
//                    //时间搓
//                    long timestamp = new Date().getTime();
//                    result.put("timestamp",timestamp);
//                    result.put("mweb_url",mweb_url);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("统一支付接口获取预支付订单出错");
//              result.put("msg", "支付错误");
//            }
        return CommonResult.ok(object);
    }

/**
 * @Description: JAapi支付成功回调地址
 * @Param: [request, response]
 * @Return: void
 * @Author: Tian
 * @Date: 2021/10/7-10:02
 **/
    @RequestMapping(value = "/callback", method = {RequestMethod.POST,RequestMethod.GET})
    public void callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("回调成功");
        //回调验证
        Map<String, String> map = PublicConfig.notify(request ,response, WechatConfig.API_V3_KEY);
        log.info(String.valueOf(map));
    }

    @GetMapping("ip")
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
