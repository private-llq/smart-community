package com.jsy.community.controller;

import cn.hutool.json.JSONUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IWeChatService;
import com.jsy.community.config.PublicConfig;
import com.jsy.community.config.WehatConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.WeChatOrderEntity;
import com.jsy.community.qo.WeChatPayVO;
import com.jsy.community.utils.OrderNoUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import net.sf.json.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@ApiJSYController
public class WeChatController {

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private IWeChatService weChatService;
    @Autowired
    private RabbitTemplate amqpTemplate;

    @PostMapping("/wxPay")
    @Login
    public CommonResult wxPay(@RequestBody WeChatPayVO weChatPayVO) throws Exception {
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

        WeChatOrderEntity msg = new WeChatOrderEntity();
        msg.setOrderNo((String) map.get("out_trade_no"));
        msg.setUid(UserUtils.getUserId());
        msg.setDescription(weChatPayVO.getDescription());
        msg.setTotal(weChatPayVO.getTotal());
        msg.setOrderStatus(1);
        msg.setArriveStatus(1);


        //mq异步保存账单到数据库
        amqpTemplate.convertAndSend("exchange_topics_wechat","queue.wechat",msg);
        //半个小时如果还没支付就自动删除数据库账单
        amqpTemplate.convertAndSend("exchange_delay_wechat", "queue.wechat.delay", map.get("out_trade_no"), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",20000);
                return message;
            }
        });
        //第一步获取prepay_id
        String prepayId = PublicConfig.V3PayGet("v3/pay/transactions/app", wxPayRequestJsonStr, WehatConfig.MCH_ID, WehatConfig.MCH_SERIAL_NO, WehatConfig.FILE_NAME);
        //第二步获取调起支付的参数
        JSONObject object = JSONObject.fromObject(PublicConfig.WxTuneUp(prepayId, WehatConfig.APPID, WehatConfig.FILE_NAME));
        return CommonResult.ok(object);
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
        String out_trade_no = PublicConfig.notify(request, response, WehatConfig.API_V3_KEY);

        System.out.println(out_trade_no);
    }


    /**
     * @Description:
     * @author: Hu
     * @since: 2021/1/26 17:09
     * @Param:
     * @return:
     */
    @GetMapping(value = "/restCallback")
    public WeChatOrderEntity callback(@RequestParam("orderId") String orderId) throws Exception {
        return weChatService.saveOrder(orderId);
    }
}
