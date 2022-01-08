package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.jsy.community.api.CebBankTestService;
import com.jsy.community.api.PaymentException;
import com.jsy.community.config.service.CebBankEntity;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.qo.unionpay.HttpResponseModel;
import com.jsy.community.untils.cebbank.CebBankContributionUtil;
import com.jsy.community.vo.cebbank.test.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费服务实现
 * @Date: 2021/11/15 17:20
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class CebBankTestServiceImpl implements CebBankTestService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @author: Pipi
     * @description: 光大银行用户注册
     * @param cebLoginQO:
     * @return: void
     * @date: 2021/11/15 17:25
     **/
    @Override
    public String login(CebLoginQO cebLoginQO) {
        log.info("光大云缴费注册");
        HttpResponseModel responseModel = CebBankContributionUtil.login(cebLoginQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("光大云缴费注册失败,请稍候重试");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        CebLoginVO cebLoginVO = JSON.parseObject(respData, CebLoginVO.class);
        // 存入缓存,有效期2小时
        redisTemplate.opsForValue().set("cebBank-sessionId:" + cebLoginQO.getUserPhone(), cebLoginVO.getSessionId(), 2L, TimeUnit.HOURS);
        return cebLoginVO.getSessionId();
    }

    /**
     * @author: Pipi
     * @description: 查询城市
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:31
     **/
    @Override
    public CebCityModelListVO queryCity(CebQueryCityQO cebQueryCityQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.queryCity(cebQueryCityQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("查询城市失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        CebCityPagingModelVO cebCityPagingModelVO = JSON.parseObject(respData, CebCityPagingModelVO.class);
        return cebCityPagingModelVO.getCityPagingModel();
    }

    /**
     * @author: Pipi
     * @description: 查询城市下缴费类别 
     * @param : 
     * @return: java.lang.String
     * @date: 2021/11/17 10:33
     **/
    @Override
    public CebQueryCityContributionCategoryVO queryCityContributionCategory(CebQueryCityContributionCategoryQO categoryQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.queryCityContributionCategory(categoryQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("查询城市下缴费类别失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebQueryCityContributionCategoryVO.class);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费类别下缴费项目
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:36
     **/
    @Override
    public CebQueryContributionProjectVO queryContributionProject(CebQueryContributionProjectQO projectQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.queryContributionProject(projectQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("查询缴费类别下缴费项目失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebQueryContributionProjectVO.class);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费账单信息
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:37
     **/
    @Override
    public CebQueryBillInfoVO queryBillInfo(CebQueryBillInfoQO billInfoQO) {
        HttpResponseModel responseModel = new HttpResponseModel();
        billInfoQO.setPollingTimes("1");
        billInfoQO.setFlag("2");
        billInfoQO.setQryAcqSsn("n20211230142040-m9DU5K");
        responseModel = CebBankContributionUtil.queryBillInfo(billInfoQO);
        /*for (Integer i = 1; i <= 5; i++) {
            billInfoQO.setPollingTimes(i.toString());
            try {
                if (i > 1) {
                    Thread.sleep(i * 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            responseModel = CebBankContributionUtil.queryBillInfo(billInfoQO);
            if (responseModel != null && CebBankEntity.successCode.equals(responseModel.getRespCode())) {
                break;
            }
        }*/
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("查询缴费账单信息失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebQueryBillInfoVO.class);
    }



    /**
     * @author: Pipi
     * @description: 查询手机充值缴费账单
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:37
     **/
    @Override
    public CebQueryMobileBillVO queryMobileBill(CebQueryMobileBillQO cebQueryMobileBillQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.queryMobileBill(cebQueryMobileBillQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("查询手机充值缴费账单失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebQueryMobileBillVO.class);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费记录(包含生活缴费和话费充值)
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:39
     **/
    @Override
    public CebQueryContributionRecordVO queryContributionRecord(CebQueryContributionRecordQO recordQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.queryContributionRecord(recordQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("查询缴费记录失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebQueryContributionRecordVO.class);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费记录详情
     * @param infoQO :
     * @return: {@link CebContributionRecordDetailVO}
     * @date: 2021/11/23 18:14
     **/
    @Override
    public CebContributionRecordDetailVO queryContributionRecordInfo(CebQueryContributionRecordInfoQO infoQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.queryContributionRecordInfo(infoQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("查询缴费记录详情失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebContributionRecordDetailVO.class);
    }

    /***
     * @author: Pipi
     * @description: 创建收银台
     * @param deskQO :
     * @return: {@link CebCashierDeskVO}
     * @date: 2021/11/23 18:21
     **/
    @Override
    public CebCashierDeskVO createCashierDesk(CebCreateCashierDeskQO deskQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.createCashierDesk(deskQO);
        if (responseModel == null || !"200".equals(responseModel.getRespCode())) {
            throw new PaymentException("创建收银台失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebCashierDeskVO.class);
    }
}
