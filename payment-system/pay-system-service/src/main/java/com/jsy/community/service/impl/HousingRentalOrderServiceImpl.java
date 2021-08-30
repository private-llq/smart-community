package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.HousingRentalOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.signature.SignResult;
import com.jsy.community.exception.JSYError;
import com.jsy.community.utils.AESUtil;
import com.jsy.community.utils.MD5Util;
import com.jsy.community.utils.MyHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 房屋租赁订单服务实现
 * @Date: 2021/8/16 17:00
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class HousingRentalOrderServiceImpl implements HousingRentalOrderService {

    private static final String PROTOCOL_TYPE = "http://";
    private static final String HOST = "222.178.212.29";
    private static final String PORT = "13000";
    // 修改租房订单支付状态
    private static final String MODIFY_ORDER_PAY_STATUS = "/zh-sign/contract-server/houseLeaseContract/updateHouseLeaseContractPay";

    /**
     * @author: Pipi
     * @description: 支付完成之后修改租赁端订单支付状态
     * @param orderNo: 支付系统订单编号
     * @param housingContractOderNo: 租赁系统合同编号
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @date: 2021/8/16 17:05
     **/
    @Override
    public Map<String, Object> completeLeasingOrder(String orderNo, String housingContractOderNo) {
        Map<String, Object> returnMap = new HashMap<>();
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("leaseContractUuid", housingContractOderNo);
        bodyMap.put("isPayment", true);
        bodyMap.put("orderUuid", orderNo);
        //url
        String url = PROTOCOL_TYPE + HOST + ":" + PORT + MODIFY_ORDER_PAY_STATUS;
        // 加密参数
        String bodyString = paramEncryption(bodyMap);
        //组装http请求
        HttpPost httpPost = MyHttpUtils.httpPostWithoutParams(url, bodyString);
        //设置header
        MyHttpUtils.setDefaultHeader(httpPost);
        //设置默认配置
        MyHttpUtils.setRequestConfig(httpPost);
        //执行
        String httpResult;
        JSONObject result = null;
        try {
            //执行请求，解析结果
            httpResult = (String)MyHttpUtils.exec(httpPost, MyHttpUtils.ANALYZE_TYPE_STR);
            result = JSON.parseObject(httpResult);
            if(0 == result.getIntValue("code")){
                returnMap.put("code",0);
                log.info("租房订单状态修改完成");
            }else if(-1 == result.getIntValue("code")){
                returnMap.put("code",-1);
                returnMap.put("msg",result.getString("message"));
                log.error("租房订单状态修改失败，订单不存在");
            }else{
                returnMap.put("code",JSYError.INTERNAL.getCode());
                returnMap.put("msg","订单出错");
                log.error("租房订单状态修改 - 远程服务出错，返回码：" + result.getIntValue("code") + " ，错误信息：" + result.getString("message"));
            }
            return returnMap;
        } catch (Exception e) {
            log.error("租房订单状态修改 - http执行或解析异常，json解析结果" + result);
            log.error(e.getMessage());
            returnMap.put("code", JSYError.INTERNAL.getCode());
            returnMap.put("msg","订单出错");
            return returnMap;
        }
    }

    /**
     * @author: Pipi
     * @description: 参数加密
     * @param o:
     * @return: java.lang.String
     * @date: 2021/8/16 17:27
     **/
    public String paramEncryption(Object o) {
        String data = JSON.toJSONString(o);
        String encrypt = AESUtil.encrypt(data, "?b@R~@Js6yH`aFal=LAHg?l~K|ExYJd;", "1E}@+?f-voEy;_?r");
        System.out.println(AESUtil.decrypt(encrypt,"?b@R~@Js6yH`aFal=LAHg?l~K|ExYJd;", "1E}@+?f-voEy;_?r"));
        SignResult<String> success = SignResult.success(encrypt);
        Map map = JSON.parseObject(JSON.toJSONString(success), Map.class);
        map.put("secret", "巴拉啦小魔仙");
        map.put("time", success.getTime());
        String signStr = MD5Util.signStr(map);
        String md5Str = MD5Util.getMd5Str(signStr);
        success.setSign(md5Str);
        return md5Str;
    }
}