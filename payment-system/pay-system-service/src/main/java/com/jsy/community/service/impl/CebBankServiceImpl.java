package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.jsy.community.api.CebBankService;
import com.jsy.community.api.PaymentException;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.qo.unionpay.HttpResponseModel;
import com.jsy.community.untils.cebbank.CebBankContributionUtil;
import com.jsy.community.vo.cebbank.CebLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费服务实现
 * @Date: 2021/11/15 17:20
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class CebBankServiceImpl implements CebBankService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @author: Pipi
     * @description: 光大银行用户注册
     * @param cebLoginQO:
     * @return: void
     * @date: 2021/11/15 17:25
     **/
    public String login(CebLoginQO cebLoginQO) {
        log.info("请求进来了");
        HttpResponseModel responseModel = CebBankContributionUtil.login(cebLoginQO);
        if (!"1000".equals(responseModel.getRespCode())) {
            throw new PaymentException("光大云缴费注册失败,请稍候重试");
        }
        CebLoginVO cebLoginVO = JSON.parseObject(responseModel.getRespData(), CebLoginVO.class);
//        redisTemplate.opsForValue().set("");
        return cebLoginVO.getSessionId();
    }

    /**
     * @author: Pipi
     * @description: 查询城市
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:31
     **/
    public String queryCity() {
        return null;
//        String queryCity = CebBankContributionUtil.queryCity(new CebQueryCityQO());
//        return queryCity;
    }

    /**
     * @author: Pipi
     * @description: 查询城市下缴费类别 
     * @param : 
     * @return: java.lang.String
     * @date: 2021/11/17 10:33
     **/
    @Override
    public String queryCityContributionCategory(CebQueryCityContributionCategoryQO categoryQO) {
//        HttpResonseModel contributionCategory = CebBankContributionUtil.queryCityContributionCategory(new CebQueryCityContributionCategoryQO());
        return null;
    }

    /**
     * @author: Pipi
     * @description: 查询缴费类别下缴费项目
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:36
     **/
    public String queryContributionProject() {
        return null;
//        String project = CebBankContributionUtil.queryContributionProject(new CebQueryContributionProjectQO());
//        return project;
    }

    /**
     * @author: Pipi
     * @description: 查询缴费账单信息
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:37
     **/
    public String queryBillInfo() {
        return null;
//        String billInfo = CebBankContributionUtil.queryBillInfo(new CebQueryBillInfoQO());
//        return billInfo;
    }

    /**
     * @author: Pipi
     * @description: 查询手机充值缴费账单
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:37
     **/
    public String queryMobileBill() {
        return null;
//        String mobileBill = CebBankContributionUtil.queryMobileBill(new CebQueryMobileBillQO());
//        return mobileBill;
    }

    /**
     * @author: Pipi
     * @description: 查询缴费记录
     * @param :
     * @return: java.lang.String
     * @date: 2021/11/17 10:39
     **/
    public String queryContributionRecord() {
        return null;
//        String contributionRecord = CebBankContributionUtil.queryContributionRecord(new CebQueryContributionRecordQO());
//        return contributionRecord;
    }


}
