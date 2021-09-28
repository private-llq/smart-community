package com.jsy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarOrderService;
import com.jsy.community.config.web.AlipayConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.qo.payment.AliOrderContentQO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
@ApiJSYController
@RestController
@RequestMapping("/AlipayPhoneH5")
public class AlipayPhoneH5Controller {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarOrderService iCarOrderService;

    @PostMapping(value = "/pay")
    public String test(@RequestBody AliOrderContentQO qo) throws IOException {
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.URL,
                AlipayConfig.APPID,
                AlipayConfig.RSA_PRIVATE_KEY,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setNotifyUrl(AlipayConfig.notify_url);// 设置异步通知地址
        request.setReturnUrl(AlipayConfig.return_url);// 设置同步地址
        JSONObject bizContent = new JSONObject();




        //查询临时车最后订单
        CarOrderEntity carOrderEntity = iCarOrderService.selectCarOrderStatus(qo.getCommunityId(), qo.getCarNumber(), 1);

        if (carOrderEntity==null) {
            return "无临时车信息";
        }

        bizContent.put("out_trade_no",carOrderEntity.getOrderNum());// 商户订单号，商户网站订单系统中唯一订单号，必填
        bizContent.put("total_amount", qo.getMoney());// 付款金额，必填
        bizContent.put("timeout_express","2m");//// 超时时间 可空
        bizContent.put("body","临时停车"+qo.getTime());// 商品描述，可空
        bizContent.put("subject", qo.getCommunityName()+"社区临时停车");// 订单名称，必填
        bizContent.put("product_code", qo.getProductCode());// 销售产品码 必填
        request.setBizContent(bizContent.toString());

        String form="";
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


    @PostMapping("returnPay")
    public String returnPay(HttpServletRequest request) {
        System.out.println("进入异步返回");
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> parameterMapNew=new HashMap<>();//组装
        Set<String> strings = parameterMap.keySet();
        strings.forEach(s->{
            String next = "";
            for (String s1 : parameterMap.get(s)) {
                next = next +s1;
            }
            parameterMapNew.put(s,next);
        });
        return "ok";
    }








}
