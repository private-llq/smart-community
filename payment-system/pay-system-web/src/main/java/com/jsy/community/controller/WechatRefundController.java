package com.jsy.community.controller;

import cn.hutool.json.JSONUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.ICompanyPayConfigService;
import com.jsy.community.api.IWeChatService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.entity.payment.WeChatOrderEntity;
import com.jsy.community.qo.payment.WechatRefundQO;
import com.jsy.community.untils.OrderNoUtil;
import com.jsy.community.untils.wechat.MyHttpClient;
import com.jsy.community.untils.wechat.PublicConfig;
import com.jsy.community.untils.wechat.WechatConfig;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @program: com.jsy.community
 * @description: 微信退款
 * @author: Hu
 * @create: 2021-09-18 14:29
 **/
@RestController
// @ApiJSYController
@Slf4j
public class WechatRefundController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICompanyPayConfigService companyPayConfigService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommunityService communityService;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private IWeChatService weChatService;

    /**
     * @Description: app退款
     * @author: Hu
     * @since: 2021/2/25 14:28
     * @Param:
     * @return:
     */
    @PostMapping("/refund")
    // @Permit("community:payment:refund")
    public CommonResult refund(@RequestBody WechatRefundQO wechatRefundQO) throws Exception {
        String body = null;
        CloseableHttpResponse execute=null;
        WeChatOrderEntity entity = null;
        if (wechatRefundQO.getOrderNum()!=null){
            entity = weChatService.getOrderOne(wechatRefundQO.getOrderNum());
            if (entity!=null){
                CompanyPayConfigEntity serviceConfig = companyPayConfigService.getCompanyConfig(entity.getCompanyId(),1);
                WechatConfig.setConfig(serviceConfig);
            } else {
                return CommonResult.error("当前订单不存在，请检查订单是否正确！");
            }
        } else {
            entity = weChatService.getOrderByQuery(wechatRefundQO);
            if (entity!=null){
                CompanyPayConfigEntity serviceConfig = companyPayConfigService.getCompanyConfig(entity.getCompanyId(),1);
                WechatConfig.setConfig(serviceConfig);
            } else {
                return CommonResult.error("当前订单不存在，请检查订单是否正确！");
            }

        }

        //封装微信支付下单请求参数
        Map hashMap = new LinkedHashMap();
        Map<Object, Object> map = new LinkedHashMap<>();
        //支付单号
        map.put("out_trade_no", entity.getId());
        //退款单号
        map.put("out_refund_no", OrderNoUtil.getOrder());
        //回调地址
        map.put("notify_url","http://zhsj.free.svipss.top/api/v1/payment/refund/callback/"+entity.getCompanyId());
        log.info("回调地址:{}", map.get("notify_url"));
        map.put("amount",hashMap);
        //退款金额
        hashMap.put("refund",1);
        //原交易金额
        hashMap.put("total",1);
        hashMap.put("currency","CNY");

        //封装请求
        HttpPost httpPost = new HttpPost(WechatConfig.WXPAY_REFUND);
        StringEntity s = new StringEntity(JSONUtil.toJsonStr(map),"UTF-8");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(s);

        //执行请求
        try {
            execute = MyHttpClient.createHttpClient().execute(httpPost);
        } catch (Exception e){
            e.printStackTrace();
        }

        //解析参数
        try {
            body = EntityUtils.toString(execute.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return CommonResult.ok(body);
    }


    /**
     * @Description: 退款成功回调地址
     * @author: Hu
     * @since: 2021/1/26 17:08
     * @Param:
     * @return:
     */
    @LoginIgnore
    @RequestMapping(value = "/refund/callback/{companyId}", method = {RequestMethod.POST,RequestMethod.GET})
    // @Permit("community:payment:refund:callback")
    public void callback(HttpServletRequest request, HttpServletResponse response, @PathVariable("companyId") Long companyId) throws Exception {
        log.info("退款回调成功");
        log.info(String.valueOf(companyId));
        CompanyPayConfigEntity configEntity = companyPayConfigService.getCompanyConfig(companyId,1);
        if (Objects.nonNull(configEntity)){
            WechatConfig.setConfig(configEntity);
        }
        log.info("配置参数："+configEntity);
        //回调验证
        Map<String, String> map = PublicConfig.refundNotify(request ,response, WechatConfig.API_V3_KEY);
        if (map.get("refund_status").equals("SUCCESS")){
            weChatService.orderRefundStatus(map);
        } else {
            log.info("退款失败");
            log.info(map.get("refund_status"));
        }

    }

}
