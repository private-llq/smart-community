package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.jsy.community.api.CebBankService;
import com.jsy.community.api.PaymentException;
import com.jsy.community.config.service.CebBankEntity;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.qo.unionpay.HttpResponseModel;
import com.jsy.community.untils.cebbank.CebBankContributionUtil;
import com.jsy.community.vo.CebCashierDeskVO;
import com.jsy.community.vo.cebbank.*;
import com.jsy.community.vo.cebbank.CebCityPagingModelVO;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费服务实现
 * @Date: 2021/11/15 17:20
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class CebBankServiceImpl implements CebBankService {

    /**
     * 光大云缴费支付回调地址
     */
    @Value("${cebBankCallbackUrl}")
    private String cebBankCallbackUrl;

    /**
     * 光大云缴费退款通知地址
     */
    @Value("${cebBankRefundUrl}")
    private String cebBankRefundUrl;

    /**
     * 光大云缴费支付完成用于前端页面跳转地址
     */
    @Value("${cebBankRedirectUrl}")
    private String cebBankRedirectUrl;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
            log.info("光大云缴费注册失败,请稍候重试");
            return null;
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        CebLoginVO cebLoginVO = JSON.parseObject(respData, CebLoginVO.class);
        // 存入缓存,有效期2小时,银行那边建议是实时申请
        redisTemplate.opsForValue().set("cebBank-sessionId:" + cebLoginQO.getUserPhone(), cebLoginVO.getSessionId(), 2L, TimeUnit.HOURS);
        return cebLoginVO.getSessionId();
    }

    /**
     * @author: Pipi
     * @description: 获取云缴费sessionId
     * @param mobile: 手机号
     * @param deviceType: // 1-PC个人电脑2-手机终端3-微信公众号4-支付宝5-微信小程序-部分接口必填
     * @return: {@link String}
     * @date: 2021/12/6 9:56
     **/
    @Override
    public String getCebBankSessionId(String mobile, String deviceType) {
        mobile = StringUtil.isBlank(mobile) ? "18996226451" : mobile;
        String sessionId = redisTemplate.opsForValue().get("cebBank-sessionId:" + mobile);
        if (sessionId == null) {
            CebLoginQO cebLoginQO = new CebLoginQO();
            cebLoginQO.setUserPhone(mobile);
            cebLoginQO.setDeviceType(deviceType);
            sessionId = login(cebLoginQO);
        }
        return sessionId;
    }

    /**
     * @param cebQueryCityQO :
     * @author: Pipi
     * @description: 查询城市
     * @return: {@link CebCityModelListVO}
     * @date: 2021/12/10 10:35
     **/
    @Override
    public CebCityModelListVO queryCity(CebQueryCityQO cebQueryCityQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.queryCity(cebQueryCityQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            log.info("查询城市失败");
            return null;
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
            throw new PaymentException(JSYError.THIRD_FAILED);
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        CebQueryCityContributionCategoryVO cebQueryCityContributionCategoryVO = JSON.parseObject(respData, CebQueryCityContributionCategoryVO.class);
        String costIcon = redisTemplate.opsForValue().get("costIcon");
        Map<Integer, String> map = JSON.parseObject(costIcon, Map.class);
        if (cebQueryCityContributionCategoryVO != null
                && !CollectionUtils.isEmpty(cebQueryCityContributionCategoryVO.getCebPaymentCategoriesList())
                && !CollectionUtils.isEmpty(map)
        ) {
            for (CebCategoryVO cebCategoryVO : cebQueryCityContributionCategoryVO.getCebPaymentCategoriesList()) {
                String picUrl = map.get(Integer.valueOf(cebCategoryVO.getType()));
                cebCategoryVO.setPicUrlClient(StringUtil.isNotBlank(picUrl) ? picUrl : cebCategoryVO.getPicUrlClient());
            }
        }
        return cebQueryCityContributionCategoryVO;
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
            log.info("查询缴费类别下缴费项目失败");
            return null;
//            throw new PaymentException("查询缴费类别下缴费项目失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        CebQueryContributionProjectVO projectVO = JSON.parseObject(respData, CebQueryContributionProjectVO.class);
        if (projectVO != null && projectVO.getPaymentItemPagingModel() != null && !CollectionUtils.isEmpty(projectVO.getPaymentItemPagingModel().getPaymentItemModelList())) {
            for (CebPaymentItemModelVO cebPaymentItemModelVO : projectVO.getPaymentItemPagingModel().getPaymentItemModelList()) {
                redisTemplate.opsForValue().set("cebBankParam:" + cebPaymentItemModelVO.getPaymentItemCode(), JSON.toJSONString(cebPaymentItemModelVO.getQueryPaymentBillParamModelList()));
            }
        }
        return projectVO;
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
        // 获取查询账单的参数条件
        List<CebQueryPaymentBillParamModelVO> cebQueryPaymentBillParamModelVOS = new ArrayList<>();
        // 从缓存取数据
        String cebBankParamString = redisTemplate.opsForValue().get("cebBankParam:" + billInfoQO.getItemId());
        if (StringUtil.isNotBlank(cebBankParamString)) {
            // 加载缓存数据
            cebQueryPaymentBillParamModelVOS = JSON.parseArray(cebBankParamString, CebQueryPaymentBillParamModelVO.class);
        } else {
            // 没有缓存,调用第三方接口获取查询条件
            CebQueryContributionProjectQO projectQO = new CebQueryContributionProjectQO();
            projectQO.setSessionId(billInfoQO.getSessionId());
            projectQO.setCityName(billInfoQO.getCityName());
            projectQO.setType(billInfoQO.getType());
            projectQO.setCanal(billInfoQO.getCanal());
            projectQO.setDeviceType(billInfoQO.getDeviceType());
            CebQueryContributionProjectVO cebQueryContributionProjectVO = queryContributionProject(projectQO);
            if (cebQueryContributionProjectVO != null && cebQueryContributionProjectVO.getPaymentItemPagingModel() != null) {
                for (CebPaymentItemModelVO cebPaymentItemModelVO : cebQueryContributionProjectVO.getPaymentItemPagingModel().getPaymentItemModelList()) {
                    if (billInfoQO.getItemId().equals(cebPaymentItemModelVO.getPaymentItemCode())) {
                        cebQueryPaymentBillParamModelVOS = cebPaymentItemModelVO.getQueryPaymentBillParamModelList();
                    }
                }
            }
        }
        // 设置查询账单的参数
        if (!CollectionUtils.isEmpty(cebQueryPaymentBillParamModelVOS)) {
            for (int i = 1; i <= cebQueryPaymentBillParamModelVOS.size(); i++) {
                CebQueryPaymentBillParamModelVO paramModelVO = cebQueryPaymentBillParamModelVOS.get(i - 1);
                if ("2".equals(paramModelVO.getPriorLevel())) {
                    // 需要上送的field属性
                    switch (paramModelVO.getFiledNum()) {
                        case "1":
                            if (StringUtil.isBlank(billInfoQO.getFiled1())) {
                                if ("1".equals(paramModelVO.getFiledType())) {
                                    // 下拉框
                                    String[] optionsList = paramModelVO.getListBoxOptions().split("\\|");
                                    String[] options = optionsList[0].split("=");
                                    // 为了测试的特殊处理,水费的账期,在测试时要取第二个
                                    if ("153505".equals(billInfoQO.getItemCode())) {
                                        options = optionsList[2].split("=");
                                    }
                                    billInfoQO.setFiled1(options[0]);
                                } else {
                                    // 文本框,没传给默认值
                                    billInfoQO.setFiled1("200");
                                }
                            }
                            if ("0".equals(paramModelVO.getInputType())) {
                                // 将上传的乘以100
                                billInfoQO.setFiled1(new BigDecimal(billInfoQO.getFiled1()).multiply(new BigDecimal("100")).setScale(0).toString());
                            }
                            break;
                        case "2":
                            if (StringUtil.isBlank(billInfoQO.getFiled2())) {
                                if ("1".equals(paramModelVO.getFiledType())) {
                                    // 下拉框
                                    String[] optionsList = paramModelVO.getListBoxOptions().split("\\|");
                                    String[] options = optionsList[0].split("=");
                                    billInfoQO.setFiled2(options[0]);
                                } else {
                                    // 文本框,没传给默认值
                                    billInfoQO.setFiled2("200");
                                }
                            }
                            if ("0".equals(paramModelVO.getInputType())) {
                                // 将上传的乘以100
                                billInfoQO.setFiled2(new BigDecimal(billInfoQO.getFiled2()).multiply(new BigDecimal("100")).setScale(0).toString());
                            }
                            break;
                        case "3":
                            if (StringUtil.isBlank(billInfoQO.getFiled3())) {
                                if ("1".equals(paramModelVO.getFiledType())) {
                                    // 下拉框
                                    String[] optionsList = paramModelVO.getListBoxOptions().split("\\|");
                                    String[] options = optionsList[0].split("=");
                                    billInfoQO.setFiled3(options[0]);
                                } else {
                                    // 文本框,没传给默认值
                                    billInfoQO.setFiled3("200");
                                }
                            }
                            if ("0".equals(paramModelVO.getInputType())) {
                                // 将上传的乘以100
                                billInfoQO.setFiled3(new BigDecimal(billInfoQO.getFiled3()).multiply(new BigDecimal("100")).setScale(0).toString());
                            }
                            break;
                        case "4":
                            if (StringUtil.isBlank(billInfoQO.getFiled4())) {
                                if ("1".equals(paramModelVO.getFiledType())) {
                                    // 下拉框
                                    String[] optionsList = paramModelVO.getListBoxOptions().split("\\|");
                                    String[] options = optionsList[0].split("=");
                                    billInfoQO.setFiled4(options[0]);
                                } else {
                                    // 文本框,没传给默认值
                                    billInfoQO.setFiled4("200");
                                }
                            }
                            if ("0".equals(paramModelVO.getInputType())) {
                                // 将上传的乘以100
                                billInfoQO.setFiled4(new BigDecimal(billInfoQO.getFiled4()).multiply(new BigDecimal("100")).setScale(0).toString());
                            }
                            break;
                        case "5":
                            if (StringUtil.isBlank(billInfoQO.getFiled5())) {
                                if ("1".equals(paramModelVO.getFiledType())) {
                                    // 下拉框
                                    String[] optionsList = paramModelVO.getListBoxOptions().split("\\|");
                                    String[] options = optionsList[0].split("=");
                                    billInfoQO.setFiled5(options[0]);
                                } else {
                                    // 文本框,没传给默认值
                                    billInfoQO.setFiled5("200");
                                }
                            }
                            if ("0".equals(paramModelVO.getInputType())) {
                                // 将上传的乘以100
                                billInfoQO.setFiled5(new BigDecimal(billInfoQO.getFiled5()).multiply(new BigDecimal("100")).setScale(0).toString());
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        for (Integer i = 1; i <= 5; i++) {
            if (i > 1) {
                billInfoQO.setPollingTimes(String.valueOf((i - 1)));
                billInfoQO.setFlag("2");
                responseModel = CebBankContributionUtil.queryBillInfo(billInfoQO);
                /*if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
                    // 第二次查询失败,丢给延迟队列去执行3次
                    rabbitTemplate.convertAndSend(CebBankExConfig.CEB_BANK_DELAYED_EXCHANGE,
                            CebBankExConfig.CEB_BANK_DELAYED_QUEUE,
                            billInfoQO.toString(),
                            new MessagePostProcessor() {
                                @Override
                                public Message postProcessMessage(Message message) throws AmqpException {
                                    message.getMessageProperties().setHeader("x-delay", 2 * 1000);
                                    return message;
                                }
                            }
                    );
                    return null;
                }*/
            } else {
                responseModel = CebBankContributionUtil.queryBillInfo(billInfoQO);
            }
            if (responseModel != null) {
                if ("1002".equals(responseModel.getRespCode()) && i == 1) {
                    String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
                    CebQueryBillInfoVO cebQueryBillInfoVO = JSON.parseObject(respData, CebQueryBillInfoVO.class);
                    billInfoQO.setQryAcqSsn(cebQueryBillInfoVO.getQryAcqSsn());
                }
                if (CebBankEntity.successCode.equals(responseModel.getRespCode()) || ("1002".equals(responseModel.getRespCode()) && i > 1)) {
                    // 获取到账单信息,跳出循环
                    break;
                }
            }
        }
        if (responseModel == null || (!CebBankEntity.successCode.equals(responseModel.getRespCode()) && !"1002".equals(responseModel.getRespCode()))) {
            throw new PaymentException(JSYError.THIRD_QUERY_FAILED);
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
            throw new PaymentException(JSYError.THIRD_QUERY_FAILED);
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
    /*@Override
    public CebQueryContributionRecordVO queryContributionRecord(CebQueryContributionRecordQO recordQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.queryContributionRecord(recordQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("查询缴费记录失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebQueryContributionRecordVO.class);
    }*/

    /**
     * @author: Pipi
     * @description: 查询缴费记录详情
     * @param infoQO :
     * @return: {@link CebContributionRecordDetailVO}
     * @date: 2021/11/23 18:14
     **/
    /*@Override
    public CebContributionRecordDetailVO queryContributionRecordInfo(CebQueryContributionRecordInfoQO infoQO) {
        HttpResponseModel responseModel = CebBankContributionUtil.queryContributionRecordInfo(infoQO);
        if (responseModel == null || !CebBankEntity.successCode.equals(responseModel.getRespCode())) {
            throw new PaymentException("查询缴费记录详情失败");
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebContributionRecordDetailVO.class);
    }*/

    /***
     * @author: Pipi
     * @description: 创建收银台
     * @param deskQO :
     * @return: {@link CebCashierDeskVO}
     * @date: 2021/11/23 18:21
     **/
    @Override
    public CebCashierDeskVO createCashierDesk(CebCreateCashierDeskQO deskQO) {
        deskQO.setNotifyUrl(cebBankCallbackUrl);
        deskQO.setRedirectUrl(cebBankRedirectUrl);
        deskQO.setRefundUrl(cebBankRefundUrl);
        HttpResponseModel responseModel = CebBankContributionUtil.createCashierDesk(deskQO);
        if (responseModel == null || !"200".equals(responseModel.getRespCode())) {
            throw new PaymentException(JSYError.DESK_CREATE_ERROR);
        }
        String respData = new String(Base64.decodeBase64(responseModel.getRespData()));
        return JSON.parseObject(respData, CebCashierDeskVO.class);
    }
}
