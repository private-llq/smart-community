package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.CebBankService;
import com.jsy.community.api.UserLivingExpensesOrderService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.qo.unionpay.HttpResponseModel;
import com.jsy.community.untils.cebbank.CebBankContributionUtil;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CebCallbackVO;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.cebbank.test.HttpRequestModel;
import com.zhsj.baseweb.annotation.LoginIgnore;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费控制器
 * @Date: 2021/11/27 14:30
 * @Version: 1.0
 **/
@RestController
// @ApiJSYController
@RequestMapping("/cebBank")
@Slf4j
public class CebBankController {
    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private CebBankService cebBankService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private UserLivingExpensesOrderService livingExpensesOrderService;

    /**
     * @param categoryQO:
     * @author: Pipi
     * @description: 查询城市下缴费类别
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/11/17 17:47
     **/
    @PostMapping("/v2/cityContributionCategory")
    public CommonResult<?> queryCityContributionCategory(@RequestBody CebQueryCityContributionCategoryQO categoryQO) {
        ValidatorUtils.validateEntity(categoryQO);
        categoryQO.setSessionId(cebBankService.getCebBankSessionId(UserUtils.getUserInfo().getMobile(), categoryQO.getDeviceType()));
        return CommonResult.ok(cebBankService.queryCityContributionCategory(categoryQO));
    }

    /**
     * @param projectQO:
     * @author: Pipi
     * @description: 查询缴费项目
     * @return: {@link CommonResult}
     * @date: 2021/11/23 15:33
     **/
    @PostMapping("/v2/queryContributionProject")
    public CommonResult<?> queryContributionProject(@RequestBody CebQueryContributionProjectQO projectQO) {
        ValidatorUtils.validateEntity(projectQO);
        projectQO.setSessionId(cebBankService.getCebBankSessionId(UserUtils.getUserInfo().getMobile(), projectQO.getDeviceType()));
        return CommonResult.ok(cebBankService.queryContributionProject(projectQO));
    }

    /**
     * @author: Pipi
     * @description: 查询城市
     * @param deviceType: 1-PC个人电脑2-手机终端3-微信公众号4-支付宝5-微信小程序
     * @return: {@link CommonResult<?>}
     * @date: 2021/12/10 14:05
     **/
    @GetMapping("/v2/queryCity")
    public CommonResult<?> queryCity(@RequestParam("deviceType") String deviceType) {
        if (StringUtil.isBlank(deviceType)) {
            throw new JSYException(JSYError.REQUEST_PARAM);
        }
        CebQueryCityQO cebQueryCityQO = new CebQueryCityQO();
        cebQueryCityQO.setSessionId(cebBankService.getCebBankSessionId(UserUtils.getUserInfo().getMobile(), deviceType));
        cebQueryCityQO.setCityRange("SOME");
        cebQueryCityQO.setDeviceType(deviceType);
        return CommonResult.ok(cebBankService.queryCity(cebQueryCityQO));
    }

    /**
     * @param billInfoQO:
     * @author: Pipi
     * @description: 非直缴业务直接查询账单
     * @return: {@link CommonResult<?>}
     * @date: 2022/1/4 10:56
     **/
    @PostMapping("/v2/queryBill")
    public CommonResult<?> queryBill(@RequestBody CebQueryBillInfoQO billInfoQO) {
        ValidatorUtils.validateEntity(billInfoQO);
        billInfoQO.setSessionId(cebBankService.getCebBankSessionId(UserUtils.getUserInfo().getMobile(), billInfoQO.getDeviceType()));
        billInfoQO.setFlag("1");
        billInfoQO.setPollingTimes("1");
        return CommonResult.ok(cebBankService.queryBillInfo(billInfoQO));
    }

    /**
     * @author: Pipi
     * @description: 光大云缴费支付回调
     * @param httpRequestModel:
     * @return:
     * @date: 2021/11/24 9:45
     **/
    @PostMapping(value = "/v2/cebCallback")
    @LoginIgnore
    public String cebBankPayCallback(HttpRequestModel httpRequestModel) {
        log.info("光大支付回调响应respData:{}", httpRequestModel.toString());
        HashMap<String, String> map = new HashMap<>();
        try {
            if (CebBankContributionUtil.verifyhttpResonse(httpRequestModel)) {
                // 验签通过
                // 将结果转换成对象
                byte[] decodeBase64 = Base64.decodeBase64(httpRequestModel.getReqdata());
                String respData_json = new String(decodeBase64);
                CebCallbackVO cebCallbackVO = JSON.parseObject(respData_json, CebCallbackVO.class);
                log.info("cebCallbackVO:{}", cebCallbackVO);
                if (cebCallbackVO != null
                        && (BusinessEnum.CebbankOrderStatusEnum.SUCCESSFUL_PAYMENT.getCode().equals(cebCallbackVO.getOrder_status())
                        || BusinessEnum.CebbankOrderStatusEnum.SUCCESSFUL_CANCELLATION.getCode().equals(cebCallbackVO.getOrder_status())
                        || BusinessEnum.CebbankOrderStatusEnum.CANCELLATION_FAILURE.getCode().equals(cebCallbackVO.getOrder_status())
                    )
                ) {
                    // 调用光大云缴费订单服务修改订单状态完成订单
                    if (livingExpensesOrderService.completeCebOrder(cebCallbackVO)) {
                        log.info("光大支付回调流程完成");
                        map.put("orderDate", String.valueOf(LocalDate.now()));
                        map.put("transacNo", String.valueOf(SnowFlake.nextId()));
                        map.put("order_status", "OK");
                    } else {
                        log.info("光大支付回调订单状态修改不成功");
                        map.put("orderDate", String.valueOf(LocalDate.now()));
                        map.put("transacNo", String.valueOf(SnowFlake.nextId()));
                        map.put("order_status", "error");
                    }
                } else {
                    log.info("光大支付回调支付状态不成功,支付状态值为:{}", cebCallbackVO.getOrder_status());
                    map.put("orderDate", String.valueOf(LocalDate.now()));
                    map.put("transacNo", String.valueOf(SnowFlake.nextId()));
                    map.put("order_status", "error");
                }
            } else {
                log.info("光大支付回调验签未通过");
                map.put("orderDate", String.valueOf(LocalDate.now()));
                map.put("transacNo", String.valueOf(SnowFlake.nextId()));
                map.put("order_status", "error");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            log.info("光大支付回调验签发生异常");
            map.put("orderDate", String.valueOf(LocalDate.now()));
            map.put("transacNo", String.valueOf(SnowFlake.nextId()));
            map.put("order_status", "error");
        }
        return JSON.toJSONString(map);
    }
}
