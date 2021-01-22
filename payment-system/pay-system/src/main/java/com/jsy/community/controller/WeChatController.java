package com.jsy.community.controller;

import cn.hutool.json.JSONUtil;
import com.jsy.community.config.PublicConfig;
import com.jsy.community.config.WehatConfig;
import com.jsy.community.qo.WeChatPayVO;
import com.jsy.community.utils.OrderNoUtil;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @program: pay
 * @description:
 * @author: Hu
 * @create: 2021-01-21 17:05
 **/
@RestController
public class WeChatController {

    @PostMapping("/wxPay")
    public Object wxPay(@RequestBody WeChatPayVO weChatPayVO) throws Exception {
        //支付的请求参数信息(此参数与微信支付文档一致，文档地址：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_2_1.shtml)
        Map hashMap = new LinkedHashMap();
        hashMap.put("total",weChatPayVO.getTotal().multiply(new BigDecimal(100)));
        hashMap.put("currency","CNY");
        Map<Object, Object> map = new LinkedHashMap<>();

        map.put("appid", WehatConfig.APPID);
        map.put("mchid",WehatConfig.MCH_ID);
        map.put("description",weChatPayVO.getDescription());
        map.put("out_trade_no", OrderNoUtil.getOrder());
        map.put("notify_url","http://jsy.free.vipnps.vip/callback");
        map.put("amount",hashMap);

        String wxPayRequestJsonStr = JSONUtil.toJsonStr(map);
        //第一步获取prepay_id
        String prepayId = PublicConfig.V3PayGet("v3/pay/transactions/app", wxPayRequestJsonStr, WehatConfig.MCH_ID, WehatConfig.MCH_SERIAL_NO, WehatConfig.FILE_NAME);
        //第二步获取调起支付的参数
        JSONObject object = JSONObject.fromObject(PublicConfig.WxTuneUp(prepayId, WehatConfig.APPID, WehatConfig.FILE_NAME));
        return object;
    }

    @RequestMapping(value = "/callback", method = {org.springframework.web.bind.annotation.RequestMethod.POST, org.springframework.web.bind.annotation.RequestMethod.GET})
    public void callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.err.println("回调成功");
        String out_trade_no = PublicConfig.notify(request, response, WehatConfig.PRIVATE_KEY);
        System.out.println(out_trade_no);
    }
}
