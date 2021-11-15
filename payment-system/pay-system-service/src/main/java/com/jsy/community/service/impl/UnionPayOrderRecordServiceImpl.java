package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.PaymentException;
import com.jsy.community.api.UnionPayOrderRecordService;
import com.jsy.community.api.UnionPayService;
import com.jsy.community.config.UnionPayConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.payment.UnionPayOrderRecordEntity;
import com.jsy.community.entity.payment.UnionPayWalletEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.UnionPayOrderRecordMapper;
import com.jsy.community.mapper.UnionPayWalletMapper;
import com.jsy.community.qo.unionpay.ConsumeApplyOrderNotifyQO;
import com.jsy.community.qo.unionpay.GenerateOrderQO;
import com.jsy.community.qo.unionpay.QueryBillInfoQO;
import com.jsy.community.qo.unionpay.QueryTransListQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.unionpay.OpenApiResponseVO;
import com.jsy.community.vo.unionpay.QueryBillInfoListVO;
import com.jsy.community.vo.unionpay.UnionPayOrderVO;
import com.jsy.community.vo.unionpay.UnionPayTransListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @Author: Pipi
 * @Description: 银联支付订单服务实现
 * @Date: 2021/4/26 16:39
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class UnionPayOrderRecordServiceImpl extends ServiceImpl<UnionPayOrderRecordMapper, UnionPayOrderRecordEntity> implements UnionPayOrderRecordService {

//    @Autowired
//    private UnionPayUtils UnionPayUtils;

    @Autowired
    private UnionPayWalletMapper unionPayWalletMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false, timeout = 1200000)
    private UnionPayService unionPayService;

    /**
     *@Author: Pipi
     *@Description: 银联消费下单
     *@Param: unionPayOrderRecordEntity:
     *@Return: com.jsy.community.vo.unionpay.UnionPayOrderVO
     *@Date: 2021/4/26 16:56
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UnionPayOrderVO generateOrder(UnionPayOrderRecordEntity unionPayOrderRecordEntity) {
        UnionPayOrderVO unionPayOrderVO;
        // 查询用户钱包ID
        QueryWrapper<UnionPayWalletEntity> walletEntityQueryWrapper = new QueryWrapper<>();
        walletEntityQueryWrapper.eq("uid", unionPayOrderRecordEntity.getUid());
        UnionPayWalletEntity unionPayWalletEntity = unionPayWalletMapper.selectOne(walletEntityQueryWrapper);
        if (unionPayWalletEntity == null) {
            throw new PaymentException("没有查询到用户钱包信息,请用户先进行钱包开户!");
        }
        // 添加银联支付订单数据
        // unionPayOrderRecordEntity.setWalletId(unionPayWalletEntity.getWalletId());
        // todo 测试时使用银联给的测试钱包ID
        unionPayOrderRecordEntity.setWalletId("2060508000214890811");
        unionPayOrderRecordEntity.setMerWalletId(UnionPayConfig.MER_WALLET_ID);
        unionPayOrderRecordEntity.setMerName(UnionPayConfig.MER_NAME);
        unionPayOrderRecordEntity.setTradeName(unionPayOrderRecordEntity.getTradeName());
        // 指定订单为待支付
        unionPayOrderRecordEntity.setTradeStatus(1);
        unionPayOrderRecordEntity.setUid(unionPayOrderRecordEntity.getUid());
        unionPayOrderRecordEntity.setId(SnowFlake.nextId());
        // 应用缓存,具体目的不记得了
        redisTemplate.opsForValue().set("union_pay_order:" + unionPayOrderRecordEntity.getId() + unionPayOrderRecordEntity.getServiceOrderNo(), unionPayOrderRecordEntity.toString());
        // 银联参数对象
        GenerateOrderQO generateOrderQO = new GenerateOrderQO();
        generateOrderQO.setOutTradeNo(unionPayOrderRecordEntity.getServiceOrderNo());
        // generateOrderQO.setWalletId(unionPayWalletEntity.getWalletId());
        // todo 测试时使用银联给的测试钱包ID
        generateOrderQO.setWalletId("2060508000214890811");
        generateOrderQO.setSubject(unionPayOrderRecordEntity.getSubject());
        generateOrderQO.setOrderAmt(unionPayOrderRecordEntity.getOrderAmt().multiply(new BigDecimal("100")).setScale(0).toString());
        generateOrderQO.setMerWalletId(UnionPayConfig.MER_WALLET_ID);
        // 设置过期时间为30分钟
        generateOrderQO.setTimeoutExpress("30m");
        generateOrderQO.setMerName(UnionPayConfig.MER_NAME);
        generateOrderQO.setNotifyUrl(UnionPayConfig.TEST_NOTIFY_URL);
        // 调用接口请求
        OpenApiResponseVO response = unionPayService.generateConsumeOrder(generateOrderQO);
        // 判断业务
        if (response.getResponse() == null || !UnionPayConfig.SUCCESS_CODE.equals(response.getCode())) {
            log.info("银联下单失败!");
            throw new JSYException("银联下单失败!");
        }
        unionPayOrderVO = JSON.parseObject(response.getResponse().getMsgBody(), UnionPayOrderVO.class);
        if (unionPayOrderVO == null || !UnionPayConfig.SUCCESS_CODE.equals(unionPayOrderVO.getRspCode())) {
            throw new PaymentException("银联下单失败!");
        }
        log.info("银联下单失败!{}", unionPayOrderVO.getRspResult());
        unionPayOrderRecordEntity.setMctOrderNo(unionPayOrderVO.getMctOrderNo());
        unionPayOrderRecordEntity.setPayH5Url(unionPayOrderVO.getPayH5Url());
        baseMapper.insert(unionPayOrderRecordEntity);
        // 全部成功,清楚缓存
        redisTemplate.delete("union_pay_order:" + unionPayOrderRecordEntity.getId() + unionPayOrderRecordEntity.getServiceOrderNo());
        return unionPayOrderVO;
    }

    /**
     *@Author: Pipi
     *@Description: 支付完成后的订单逻辑
     *@Param: notifyQO:
     *@Return: void
     *@Date: 2021/5/6 14:48
     **/
    @Override
    public void updateOrderStatus(ConsumeApplyOrderNotifyQO notifyQO) {
        // 根据响应参数验证订单是否匹配
        QueryWrapper<UnionPayOrderRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_order_no", notifyQO.getOutTradeNo());
        UnionPayOrderRecordEntity recordEntity = baseMapper.selectOne(queryWrapper);
        if (recordEntity != null) {
            // 回调的订单金额,单位是分,所以需要除以100
            BigDecimal orderAmt = new BigDecimal(notifyQO.getOrderAmt()).divide(new BigDecimal("100"));
            // 判断金额是否一致
            if (recordEntity.getOrderAmt().compareTo(orderAmt) == 0) {
                // 金额一致,修改订单状态,信息
                recordEntity.setTransOrderNo(notifyQO.getTransOrderNo());
                recordEntity.setTradeStatus(2);
                UpdateWrapper<UnionPayOrderRecordEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("service_order_no", notifyQO.getOutTradeNo());
                baseMapper.update(recordEntity, updateWrapper);
                log.info("订单:{}完成支付", notifyQO.getOutTradeNo());
            } else {
                log.info("订单:{}的订单金额不一致,不能完成支付", notifyQO.getOutTradeNo());
            }
        } else {
            log.info("订单:{}未在数据库中找到", notifyQO.getOutTradeNo());
        }
    }

    /**
     * @Author: Pipi
     * @Description: 查询交易明细
     * @Param: queryTransListQO:
     * @Return: com.jsy.community.vo.unionpay.UnionPayTransListVO
     * @Date: 2021/5/12 10:08
     */
    @Override
    public UnionPayTransListVO queryTransList(QueryTransListQO queryTransListQO) {
        UnionPayTransListVO unionPayTransListVO = new UnionPayTransListVO();
        OpenApiResponseVO response = unionPayService.queryTransList(queryTransListQO);
        // 判断业务
        if (response.getResponse() == null || !UnionPayConfig.SUCCESS_CODE.equals(response.getCode())) {
            log.info("查询交易明细失败!");
            return unionPayTransListVO;
        }
        return JSON.parseObject(response.getResponse().getMsgBody(), UnionPayTransListVO.class);
    }

    /**
     * @Author: Pipi
     * @Description: 账单查询
     * @Param: queryBillInfoQO:
     * @Return: com.jsy.community.vo.unionpay.QueryBillInfoListVO
     * @Date: 2021/5/12 11:27
     */
    @Override
    public QueryBillInfoListVO queryBillInfo(QueryBillInfoQO queryBillInfoQO) {
        queryBillInfoQO.setStartDate(queryBillInfoQO.getLocalDateStartDate().toString());
        queryBillInfoQO.setEndDate(queryBillInfoQO.getLocalDateEndDate().toString());
        if (queryBillInfoQO.getMinTransAmt() != null) {
            BigDecimal minTransAmt = new BigDecimal(queryBillInfoQO.getMinTransAmt()).multiply(new BigDecimal(100).setScale(0));
            queryBillInfoQO.setMinTransAmt(minTransAmt.toString());
        }
        if (queryBillInfoQO.getMaxTransAmt() != null) {
            BigDecimal maxTransAmt = new BigDecimal(queryBillInfoQO.getMaxTransAmt()).multiply(new BigDecimal(100).setScale(0));
            queryBillInfoQO.setMaxTransAmt(maxTransAmt.toString());
        }
        QueryBillInfoListVO queryBillInfoListVO = new QueryBillInfoListVO();
        OpenApiResponseVO response = unionPayService.queryBillInfo(queryBillInfoQO);
        // 判断业务
        if (response.getResponse() == null || !UnionPayConfig.SUCCESS_CODE.equals(response.getCode())) {
            log.info("账单查询失败!");
            return queryBillInfoListVO;
        }
        return JSON.parseObject(response.getResponse().getMsgBody(), QueryBillInfoListVO.class);
    }
}
