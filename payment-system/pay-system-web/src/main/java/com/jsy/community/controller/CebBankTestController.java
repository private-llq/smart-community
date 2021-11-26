package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.CebBankService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.untils.cebbank.CebBankContributionUtil;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.cebbank.*;
import com.zhsj.baseweb.annotation.LoginIgnore;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费控制器
 * @Date: 2021/11/15 17:26
 * @Version: 1.0
 **/
@RestController
@ApiJSYController
@RequestMapping("/cebBankTest")
@Slf4j
public class CebBankTestController {
    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private CebBankService cebBankService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @author: Pipi
     * @description: 查询可缴费的城市
     * @param cebQueryCityQO:
     * @return: {@link CommonResult}
     * @date: 2021/11/23 15:12
     **/
    @LoginIgnore
    @PostMapping("/v2/queryCity")
    public CommonResult queryCity(@RequestBody CebQueryCityQO cebQueryCityQO) {
        String sessionId = getCebBankSessionId();
        cebQueryCityQO.setSessionId(sessionId);
        cebQueryCityQO.setCityRange("ALL");
        cebQueryCityQO.setDeviceType("1");
        return CommonResult.ok(cebBankService.queryCity(cebQueryCityQO));
    }

    /**
     * @author: Pipi
     * @description: 查询城市下缴费类别
     * @param categoryQO:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/11/17 17:47
     **/
    @PostMapping("/v2/cityContributionCategory")
    @LoginIgnore
    public CommonResult queryCityContributionCategory(@RequestBody CebQueryCityContributionCategoryQO categoryQO) {
        String sessionId = getCebBankSessionId();
        categoryQO.setCityName("北京市");
        categoryQO.setSessionId(sessionId);
        categoryQO.setDeviceType("1");
        return CommonResult.ok(cebBankService.queryCityContributionCategory(categoryQO));
    }

    /**
     * @author: Pipi
     * @description: 查询缴费项目
     * @param projectQO:
     * @return: {@link CommonResult}
     * @date: 2021/11/23 15:33
     **/
    @LoginIgnore
    @PostMapping("/v2/queryContributionProject")
    public CommonResult queryContributionProject(@RequestBody CebQueryContributionProjectQO projectQO) {
        String sessionId = getCebBankSessionId();
        projectQO.setSessionId(sessionId);
        projectQO.setCityName("北京市");
        projectQO.setType("20");
        projectQO.setDeviceType("1");
        return CommonResult.ok(cebBankService.queryContributionProject(projectQO));
    }

    /**
     * @author: Pipi
     * @description: 查询缴费账单信息(作用同查询手机充值缴费账单,但是使用于生活缴费,不适用于手机话费)
     * @param billInfoQO:
     * @return: {@link CommonResult}
     * @date: 2021/11/23 17:14
     **/
    @LoginIgnore
    @PostMapping("/v2/queryBillInfo")
    public CommonResult queryBillInfo(@RequestBody CebQueryBillInfoQO billInfoQO) {
        String sessionId = getCebBankSessionId();
        billInfoQO.setSessionId(sessionId);
        billInfoQO.setItemCode("172805");
        billInfoQO.setBillKey("051245000023");
//        billInfoQO.setFlag("1");
        billInfoQO.setDeviceType("1");
        return CommonResult.ok(cebBankService.queryBillInfo(billInfoQO));
    }

    /***
     * @author: Pipi
     * @description: 查询手机充值缴费账单(作用同查询缴费账单信息,但是使用于手机话费,不适用于生活缴费)
     * @param cebQueryMobileBillQO:
     * @return: {@link CommonResult}
     * @date: 2021/11/23 18:05
     **/
    @LoginIgnore
    @PostMapping("/v2/queryMobileBill")
    public CommonResult queryMobileBill(@RequestBody CebQueryMobileBillQO cebQueryMobileBillQO) {
        String sessionId = getCebBankSessionId();
        cebQueryMobileBillQO.setSessionId(sessionId);
        cebQueryMobileBillQO.setCategoryType("4");
        cebQueryMobileBillQO.setMobile("13581778060");
        cebQueryMobileBillQO.setDeviceType("1");
        return CommonResult.ok(cebBankService.queryMobileBill(cebQueryMobileBillQO));
    }

    /***
     * @author: Pipi
     * @description: 查询缴费记录(查询用户的缴费记录信息)
     * @param recordQO:
     * @return: {@link CommonResult}
     * @date: 2021/11/23 18:10
     **/
    @LoginIgnore
    @PostMapping("/v2/queryContributionRecord")
    public CommonResult queryContributionRecord(@RequestBody CebQueryContributionRecordQO recordQO) {
        String sessionId = getCebBankSessionId();
        recordQO.setSessionId(sessionId);
        recordQO.setStatus("-1");
        recordQO.setDeviceType("1");
        recordQO.setCurPage("1");
        recordQO.setPageSize("10");
        return CommonResult.ok(cebBankService.queryContributionRecord(recordQO));
    }

    /**
     * @author: Pipi
     * @description: 查询缴费记录详情
     * @param infoQO:
     * @return: {@link CommonResult}
     * @date: 2021/11/23 18:16
     **/
    @LoginIgnore
    @PostMapping("/v2/queryContributionRecordInfo")
    public CommonResult queryContributionRecordInfo(@RequestBody CebQueryContributionRecordInfoQO infoQO) {
        String sessionId = getCebBankSessionId();
        infoQO.setSessionId(sessionId);
        infoQO.setOrderNo("1202111253521477");
        infoQO.setTranDate("2021-11-25");
        infoQO.setDeviceType("1");
        return CommonResult.ok(cebBankService.queryContributionRecordInfo(infoQO));
    }

    /**
     * @author: Pipi
     * @description: 创建收银台
     * @param deskQO:
     * @return: {@link CommonResult}
     * @date: 2021/11/23 18:26
     **/
    @LoginIgnore
    @PostMapping("/v2/createCashierDesk")
    public CommonResult createCashierDesk(@RequestBody CebCreateCashierDeskQO deskQO) {
        String sessionId = getCebBankSessionId();
        deskQO.setMerOrderNo(String.valueOf(SnowFlake.nextId()));
        deskQO.setMerOrderDate(String.valueOf(LocalDate.now()).replaceAll("-", ""));
        deskQO.setPayAmount(new BigDecimal("221"));
        deskQO.setPaymentItemCode("470191419");
        deskQO.setPaymentItemId("172805");
        deskQO.setBillKey("051245000023");
        deskQO.setSessionId(sessionId);
        deskQO.setBillAmount(new BigDecimal("221"));
        deskQO.setQueryAcqSsn("n20211125180148-885Kz0");
        deskQO.setCustomerName("**");
        deskQO.setContractNo("051245000023");
//        deskQO.setFiled1("2013");
//        deskQO.setFiled3("采暖");
//        deskQO.setFiled4("居民面积");
        deskQO.setAppName("E到家");
        deskQO.setAppVersion("1.0.0");
        deskQO.setRedirectUrl("http://zhsj.free.svipss.top");
//        deskQO.setNotifyUrl("http://222.178.212.29:8090/zhsj/community/payment/api/v1/payment/cebBank/v2/cebCallback");
//        deskQO.setRefundUrl("http://222.178.212.29:8090/zhsj/community/payment/api/v1/payment/cebBank/v2/refund");
        deskQO.setNotifyUrl("http://zhsj.free.svipss.top/api/v1/payment/cebBankTest/v2/cebCallback");
        deskQO.setRefundUrl("http://zhsj.free.svipss.top/api/v1/payment/cebBankTest/v2/refund");
        CebBillQueryResultDataModelQO resultDataModelQO = new CebBillQueryResultDataModelQO();
        resultDataModelQO.setContractNo("051245000023");
        resultDataModelQO.setCustomerName("**");
//        resultDataModelQO.setOriginalCustomerName("杨波");
        resultDataModelQO.setBalance("0");
        resultDataModelQO.setPayAmount("221");
        resultDataModelQO.setBeginDate("");
        resultDataModelQO.setEndDate("");
        resultDataModelQO.setFiled1("");
        resultDataModelQO.setFiled2("");
        resultDataModelQO.setFiled3("");
        resultDataModelQO.setFiled4("");
        resultDataModelQO.setFiled5("");
        resultDataModelQO.setPayBeginDate(null);
        resultDataModelQO.setPayEndDate(null);
        resultDataModelQO.setSerialNumber(null);
        resultDataModelQO.setAccount(null);
        deskQO.setBillQueryResultDataModel(JSON.toJSONString(resultDataModelQO));
        deskQO.setType("20");
        deskQO.setDeviceType("1");


        /*deskQO.setPayAmount(new BigDecimal("50"));
        deskQO.setPaymentItemCode("956084211");
        deskQO.setPaymentItemId("231106");
        deskQO.setBillKey("13581778060");
        deskQO.setSessionId(sessionId);
//        deskQO.setBillAmount(new BigDecimal("50"));
//        deskQO.setContractNo("5010053");
        deskQO.setAppName("E到家");
        deskQO.setAppVersion("1.0.0");
        deskQO.setRedirectUrl("http://zhsj.free.svipss.top");
        deskQO.setNotifyUrl("http://zhsj.free.svipss.top/api/v1/payment/cebBankTest/v2/cebCallback");
        deskQO.setRefundUrl("http://zhsj.free.svipss.top/api/v1/payment/cebBankTest/v2/refund");
        deskQO.setType("1");
        deskQO.setDeviceType("1");*/
        return CommonResult.ok(cebBankService.createCashierDesk(deskQO));
    }

    /**
     * @author: Pipi
     * @description: 光大云缴费支付回调
     * @param request:
     * @return: {@link CommonResult}
     * @date: 2021/11/24 9:45
     **/
    @LoginIgnore
    @PostMapping(value = "/v2/cebCallback")
    public CommonResult cebBankPayCallback(HttpServletRequest request) {
        // 获取回调参数
        StringBuilder data = new StringBuilder();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            while (null != (line = reader.readLine())){
                data.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String result = new String(data);
        HashMap<String, String> map = new HashMap<>();
        if (StringUtil.isNotBlank(result)) {
            try {
                log.info("光大支付回调响应:{}", result);
                result = "{\"" + result + "\"}";
                result = result.replaceAll("=", "\":\"").replaceAll("&", "\",\"");
                Gson gson = new Gson();
                HttpRequestModel httpRequestModel = gson.fromJson(result, HttpRequestModel.class);
                if (CebBankContributionUtil.verifyhttpResonse(httpRequestModel)) {
                    // 验签通过
                    // 将结果转换成对象
                    byte[] decodeBase64 = Base64.decodeBase64(httpRequestModel.getReqdata().replaceAll("%3D", ""));
                    String respData_json = new String(decodeBase64);
                    log.info("光大支付回调响应respData:{}", respData_json);
                    CebCallbackVO cebCallbackVO = JSON.parseObject(respData_json, CebCallbackVO.class);
                    log.info("cebCallbackVO:{}", cebCallbackVO);
                    switch (cebCallbackVO.getOrder_status()) {
                        case 0:
                            // 订单创建成功
                            break;
                        case 1:
                            // 支付成功
                            break;
                        case 2:
                            // 支付失败
                            break;
                        case 3:
                            // 销账成功
                            break;
                        case 4:
                            // 销账失败
                            break;
                        case 5:
                            // 未知状态
                            break;
                        case 8:
                            // 实施退款
                            break;
                        default:
                            break;
                    }
                } else {
                    log.info("光大支付回调验签未通过");
                    map.put("orderDate", String.valueOf(LocalDate.now()));
                    map.put("transacNo", String.valueOf(SnowFlake.nextId()));
                    map.put("order_status", "error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                map.put("orderDate", String.valueOf(LocalDate.now()));
                map.put("transacNo", String.valueOf(SnowFlake.nextId()));
                map.put("order_status", "error");
            }
            map.put("orderDate", String.valueOf(LocalDate.now()));
            map.put("transacNo", String.valueOf(SnowFlake.nextId()));
            map.put("order_status", "ok");
        } else {
            map.put("orderDate", String.valueOf(LocalDate.now()));
            map.put("transacNo", String.valueOf(SnowFlake.nextId()));
            map.put("order_status", "error");
        }
        return CommonResult.error(JSON.toJSONString(map));
    }

    /**
     * @author: Pipi
     * @description: 获取cebBankSession
     * @param :
     * @return: {@link String}
     * @date: 2021/11/23 14:59
     **/
    @LoginIgnore
    private String getCebBankSessionId() {
        String sessionId = redisTemplate.opsForValue().get("cebBank-sessionId:" + "18996226451");
        if (sessionId == null) {
            CebLoginQO cebLoginQO = new CebLoginQO();
            cebLoginQO.setUserPhone("18996226451");
            cebLoginQO.setDeviceType("1");
            sessionId = cebBankService.login(cebLoginQO);
        }
        return sessionId;
    }
}
