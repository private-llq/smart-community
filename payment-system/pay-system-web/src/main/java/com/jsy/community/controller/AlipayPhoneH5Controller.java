package com.jsy.community.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarOrderService;
import com.jsy.community.config.web.AlipayConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.qo.payment.AliOrderContentQO;
import com.jsy.community.qo.payment.RefundQO;
import com.jsy.community.utils.AlipayUtils;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// @ApiJSYController
@RestController
@RequestMapping("/AlipayPhoneH5")
public class AlipayPhoneH5Controller {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarOrderService carOrderService;

    @Autowired
    private AlipayUtils alipayUtils;
    //下单
    @LoginIgnore
    @RequestMapping(value = "/pay")
    // @Permit("community:payment:AlipayPhoneH5:pay")
    public String test(@RequestBody AliOrderContentQO qo) {
        System.out.println(qo.toString());
        //普通方式
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.URL,
                AlipayConfig.APPID,
                AlipayConfig.RSA_PRIVATE_KEY,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGNTYPE);
          //证书方式
        //AlipayClient alipayClient = AliH5CertificateConfig.getDefaultCertClient();

        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setNotifyUrl(AlipayConfig.notify_url);// 设置异步通知地址
        request.setReturnUrl(AlipayConfig.return_url);// 设置同步地址
        JSONObject bizContent = new JSONObject();


        bizContent.put("out_trade_no", qo.getOrderNum());// 商户订单号，商户网站订单系统中唯一订单号，必填
        bizContent.put("total_amount", qo.getMoney());// 付款金额，必填
        bizContent.put("timeout_express", "2m");//// 超时时间 可空
        bizContent.put("body", qo.getCommunityName() + "临时停车" + qo.getTime());// 商品描述，可空
        bizContent.put("subject", qo.getCommunityName() + "临时停车");// 订单名称，必填
        bizContent.put("product_code", qo.getProductCode());// 销售产品码 必填
        request.setBizContent(bizContent.toString());

        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return form;

//        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
//        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
//        httpResponse.getWriter().flush();
//        httpResponse.getWriter().close();


    }

    //异步通知
    @LoginIgnore
    @PostMapping("returnPay")
    // @Permit("community:payment:AlipayPhoneH5:returnPay")
    public String alipaynotify(Model model, HttpServletRequest request) {
        System.out.println("支付宝异步回调 ------------begin-----------");
        String result = "fail"; //默认验签失败
        Map<String, String> params = getMapParameter(request);//提取requst参数到map
        boolean signVerified = false;//验证结果默认为false
        try {
            //调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGNTYPE);
        } catch (AlipayApiException e1) {
            System.out.println("由于" + e1.getErrMsg() + "返回给支付宝系统的结果验签失败");
            model.addAttribute("result", "fail");
            return result;
        }
        System.out.println("支付宝验证签名：---------------------------------" + signVerified);


        /* 实际验证过程建议商户务必添加以下校验：
        1、需要验证该通知数据中的out_trade_no(商户订单号)是否为商户系统中创建的订单号，
        2、判断total_amount（即商户订单创建时的金额)是否确实为该订单的实际金额，
        3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
        4、验证app_id是否为该商户本身。
        */


        if (signVerified) {//验证成功
            String trade_status = params.get("trade_status");

            if ("TRADE_FINISHED".equals(trade_status)) {
                // 即时到账普通版。普通版不支持支付完成后的退款操作，即用户充值完成后，该交易就算是完成了，这笔交易就不能再做任何操作了

            } else if ("TRADE_SUCCESS".equals(trade_status)) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
                CarOrderEntity entity;
                try {
                    // 在这里处理支付成功后的操作，比如修改订单状态等等
                    entity = carOrderService.selectOneOrder(params.get("out_trade_no"));
                    if (BeanUtil.isEmpty(entity)) {
                        result = "fail";
                    }
                    BigDecimal money = new BigDecimal(params.get("total_amount"));
                    entity.setMoney(money);//支付金额
                    entity.setOrderStatus(1);//0未支付，1已支付
                    entity.setPayType(3);//1app支付 2物业后台 3支付宝手机H5 4微信
                    entity.setRise(params.get("body"));//商品订单详情
                    String gmt_payment = params.get("gmt_payment");//获取支付时间
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime ldt = LocalDateTime.parse(gmt_payment, df);
                    entity.setOrderTime(ldt);//付款时间
                    entity.setBillNum(params.get("trade_no"));//账单编号
                    boolean b = carOrderService.updateOrder(entity, params.get("out_trade_no"));//根据订单号修改订单信息
                    result = "success";
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    result = "fail";
                }
            } else {
                result = "fail";
            }
        } else {//验证失败
            result = "fail";
            //调试用，写文本函数记录程序运行情况是否正常
            String sWord = AlipaySignature.getSignCheckContentV1(params);
            System.out.println(sWord);
            //AlipayConfig.logResult(sWord);
            System.out.println("支付宝异步回调验签失败");
        }


        System.out.println("异步回调返回给支付宝系统的结果result:" + result);
        model.addAttribute("result", result);
        System.out.println("支付宝异步回调  -------------end ------------");
        return result;
    }


    //提取requst参数到map
    public static Map<String, String> getMapParameter(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> params = new HashMap<>();//组装
        Set<String> strings = parameterMap.keySet();
        strings.forEach(s -> {
            String next = "";
            for (String s1 : parameterMap.get(s)) {
                next = next + s1;
            }
            params.put(s, next);
        });
        return params;
    }


    //支付宝退款
    @LoginIgnore
    @RequestMapping(value = "/refund")
    // @Permit("community:payment:AlipayPhoneH5:refund")
    public String refund(@RequestBody RefundQO qo) {
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.URL,
                AlipayConfig.APPID,
                AlipayConfig.RSA_PRIVATE_KEY,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGNTYPE);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", qo.getTrade_no());//支付宝账单号
        bizContent.put("refund_amount", qo.getRefund_amount());//退款金额
        //退款请求号,标识一次退款请求，需要保证在交易号下唯一，如需部分退款，则此参数必传。
        bizContent.put("out_request_no", UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis());

        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            return "退款成功";
        } else {
            return "退款失败";
        }
    }


    //查询退款记录
    @LoginIgnore
    @RequestMapping(value = "/selectRefund")
    // @Permit("community:payment:AlipayPhoneH5:selectRefund")
    public AlipayTradeFastpayRefundQueryResponse selectRefund(@RequestBody RefundQO qo) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.URL,
                AlipayConfig.APPID,
                AlipayConfig.RSA_PRIVATE_KEY,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGNTYPE);
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", qo.getTrade_no());//支付宝账单号
        //退款请求号,标识一次退款请求，需要保证在交易号下唯一，如需部分退款，则此参数必传。
        bizContent.put("out_request_no", UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis());
        request.setBizContent(bizContent.toString());

        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return response;
    }


}
