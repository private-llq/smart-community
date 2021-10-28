package com.jsy.community.utils;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.jsy.community.utils.signature.ZhsjUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * @program: com.jsy.community
 * @description:  订单上链工具类
 * @author: Hu
 * @create: 2021-10-28 16:08
 **/
public class OrderCochainUtil {

    private static final String url = "http://222.178.212.29:13000/zh-sign/contract-server/community/sign/communityOrderUpLink";

    /**
     * @Description: 支付上链
     * @author: Hu
     * @since: 2021/10/28 17:19
     * @Params: 参数说明
     * transactionName:交易名称
     * currency:交易币种
     * payType:支付类型
     * totalAmount:支付金额
     * orderNum:订单编号
     * payElectronicIdentity:支付方电子身份证
     * payeeElectronicIdentity:收款方电子身份证
     * detailedList:交易详单
     * @return: [com.jsy.community.utils.CochainResponseEntity]
     */
    public static CochainResponseEntity orderCochain(String transactionName, String currency, String payType, BigDecimal totalAmount,String orderNum,String payElectronicIdentity,String payeeElectronicIdentity,String detailedList){
        HashMap<String, Object> map = new HashMap<>();
        map.put("transactionName",transactionName);
        map.put("currency",currency);
        map.put("payType",payType);
        map.put("totalAmount",totalAmount);
        map.put("orderNum",orderNum);
        map.put("payElectronicIdentity",payElectronicIdentity);
        map.put("payeeElectronicIdentity",payeeElectronicIdentity);
        map.put("detailedList",detailedList);
        //内容加密
        String encrypt = ZhsjUtil.postEncrypt(JSONUtil.toJsonStr(map));
        //创建httpclient对象
        HttpClient httpClient = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        //装填参数  charset
        StringEntity s = new StringEntity(encrypt,"UTF-8");
        //设置参数到请求对象中
        httpPost.setEntity(s);
        httpPost.setHeader("Content-type", "application/json");
        //执行请求操作，并拿到结果（同步阻塞）
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            //获取结果实体
            HttpEntity entity = response.getEntity();
            //按指定编码转换结果实体为String类型
            return JSON.parseObject(EntityUtils.toString(entity, "UTF-8"), CochainResponseEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(orderCochain(null, null, null, null, null, null, null, null));
    }

}
