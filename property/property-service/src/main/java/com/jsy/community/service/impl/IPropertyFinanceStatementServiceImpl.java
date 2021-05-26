package com.jsy.community.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceStatementService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.*;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.StatementQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.StatementOrderVO;
import com.jsy.community.vo.StatementVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.MapUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Author: Pipi
 * @Description: 物业财务-结算单服务实现
 * @Date: 2021/4/22 16:56
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class IPropertyFinanceStatementServiceImpl extends ServiceImpl<PropertyFinanceStatementMapper, PropertyFinanceStatementEntity> implements IPropertyFinanceStatementService {

    @Autowired
    private PropertyFinanceCycleMapper cycleMapper;

    @Autowired
    private PropertyFinanceOrderMapper orderMapper;

    @Autowired
    private PropertyAccountBankMapper propertyAccountBankMapper;
    
    @Autowired
    private PropertyFinanceStatementMapper propertyFinanceStatementMapper;

    @Autowired
    private PropertyFinanceStatementRecordMapper recordMapper;

    /**
     *@Author: Pipi
     *@Description: 定时产生结算单
     *@Param: :
     *@Return: void
     *@Date: 2021/4/22 16:59
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void timingStatement() {
        // 获取今天的号数
        Integer dayOfMonth = LocalDateTimeUtil.now().getDayOfMonth();
        // 需要在当天结算的社区
        List<PropertyFinanceCycleEntity> cycleEntityList = cycleMapper.queryCommunityIdByStartDate(dayOfMonth);
        HashMap<Long, PropertyFinanceCycleEntity> cycleEntityMap = new HashMap<>();
        ArrayList<Long> communityIdS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cycleEntityList)) {
            cycleEntityList.forEach(cycleEntity -> {
                // 将物业财务结算周期实体塞进map,后面方便获取结算周期信息
                cycleEntityMap.put(cycleEntity.getCommunityId(), cycleEntity);
                // 将需要结算的社区主键放进列表
                communityIdS.add(cycleEntity.getCommunityId());
            });
            // 1获取这些社区的上个月的已收款的未结算的账单和被驳回的账单
            List<PropertyFinanceOrderEntity> needStatementOrderList = orderMapper.queryNeedStatementOrderListByCommunityIdAndOrderTime(communityIdS);
            // 将账单拆成需要结算的账单和被驳回的账单
            if (!CollectionUtils.isEmpty(needStatementOrderList)) {
                // 需要结算的账单
                HashMap<Long, List<PropertyFinanceOrderEntity>> statementOrderMap = new HashMap<>();
                // 被驳回的账单
                HashMap<Long, List<PropertyFinanceOrderEntity>> rejectOrderMap = new HashMap<>();
                needStatementOrderList.forEach(orderEntity -> {
                    if (orderEntity.getStatementStatus() == 0) {
                        // 未结算
                        if (!MapUtils.isEmpty(statementOrderMap) && statementOrderMap.get(orderEntity.getCommunityId()) != null) {
                            statementOrderMap.get(orderEntity.getCommunityId()).add(orderEntity);
                        } else {
                            List<PropertyFinanceOrderEntity> orderEntities = new ArrayList<>();
                            orderEntities.add(orderEntity);
                            statementOrderMap.put(orderEntity.getCommunityId(), orderEntities);
                        }
                    } else {
                        // 被驳回
                        if (!MapUtils.isEmpty(rejectOrderMap) && rejectOrderMap.get(orderEntity.getCommunityId()) != null) {
                            rejectOrderMap.get(orderEntity.getCommunityId()).add(orderEntity);
                        } else {
                            List<PropertyFinanceOrderEntity> orderEntities = new ArrayList<>();
                            orderEntities.add(orderEntity);
                            rejectOrderMap.put(orderEntity.getCommunityId(), orderEntities);
                        }
                    }

                });
                // 操作记录实体
                List<PropertyFinanceStatementRecordEntity> insertRecordEntities = new ArrayList<>();
                List<PropertyFinanceStatementRecordEntity> updateRecordEntities = new ArrayList<>();
                // 2.1将已收款的未结算的账单生成结算单
                List<PropertyFinanceStatementEntity> statementEntities = new ArrayList<>();
                HashMap<String, List<Long>> statementOrderUpdateMap = new HashMap<>();
                if (!MapUtils.isEmpty(statementOrderMap)) {
                    statementOrderMap.keySet().forEach(communityId -> {
                        List<Long> statementOrderNumS = new ArrayList<>();
                        // 根据社区ID查询社区对公账户信息
                        PropertyAccountBankEntity propertyAccountBankEntity = propertyAccountBankMapper.selectOne(new QueryWrapper<PropertyAccountBankEntity>().eq("community_id", communityId));
                        if (propertyAccountBankEntity == null) {
                            log.info("结算出错,找不到响应的对公账户!");
                            return;
                        }
                        // 生成结算单号
                        String statementId = generateStatementId(communityId);
                        // 计算结算金额
                        final BigDecimal[] statementAmount = {new BigDecimal(0)};
                        statementOrderMap.get(communityId).forEach(orderEntity -> {
                            statementAmount[0] = statementAmount[0].add(orderEntity.getTotalMoney());
                            statementOrderNumS.add(orderEntity.getId());
                        });
                        PropertyFinanceStatementEntity statementEntity = new PropertyFinanceStatementEntity();
                        statementEntity.setCommunityId(communityId);
                        statementEntity.setStatementNum(statementId);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.set(Calendar.DAY_OF_MONTH, cycleEntityMap.get(communityId).getStartDate());
                        statementEntity.setStartDate(LocalDateTimeUtil.of(calendar.getTime()));
                        calendar.set(Calendar.DAY_OF_MONTH, cycleEntityMap.get(communityId).getEndDate());
                        statementEntity.setEndDate(LocalDateTimeUtil.of(calendar.getTime()));
                        statementEntity.setStatementStatus(1);
                        statementEntity.setTotalMoney(statementAmount[0]);
                        statementEntity.setReceiptAccount(propertyAccountBankEntity.getId());
                        statementEntity.setAccountName(propertyAccountBankEntity.getAccountName());
                        statementEntity.setBankName(propertyAccountBankEntity.getBankName());
                        statementEntity.setBankCity(propertyAccountBankEntity.getBankCity());
                        statementEntity.setBankBranchName(propertyAccountBankEntity.getBankBranchName());
                        statementEntity.setBankNo(propertyAccountBankEntity.getBankNo());
                        statementEntity.setId(SnowFlake.nextId());
                        statementOrderUpdateMap.put(statementId, statementOrderNumS);
                        statementEntities.add(statementEntity);
                        // 操作记录
                        PropertyFinanceStatementRecordEntity recordEntity = new PropertyFinanceStatementRecordEntity();
                        recordEntity.setStatementNum(statementId);
                        recordEntity.setOperationType(1);
                        recordEntity.setRemake("生成结算单");
                        recordEntity.setOperatorId(0L);
                        recordEntity.setId(SnowFlake.nextId());
                        insertRecordEntities.add(recordEntity);
                    });
                }
                // 执行新增结算单
                 batchInsertStatement(statementEntities, statementOrderUpdateMap, insertRecordEntities);
                // 2.2将被驳回的账单的状态更新为待结算
                // 需要更新的账单列表
                ArrayList<Long> orderNumS = new ArrayList<>();
                // 需要更新的结算单集合
                HashSet<String> statementNumSet = new HashSet<>();
                if (!MapUtils.isEmpty(rejectOrderMap)) {
                    rejectOrderMap.keySet().forEach(communityId -> {
                        rejectOrderMap.get(communityId).forEach(financeOrderEntity -> {
                            orderNumS.add(financeOrderEntity.getId());
                            statementNumSet.add(financeOrderEntity.getStatementNum());
                        });
                    });
                }
                // 更新操作记录
                if (!CollectionUtils.isEmpty(statementNumSet)) {
                    statementNumSet.forEach(statementNum -> {
                        PropertyFinanceStatementRecordEntity recordEntity = new PropertyFinanceStatementRecordEntity();
                        recordEntity.setStatementNum(statementNum);
                        recordEntity.setOperationType(1);
                        recordEntity.setRemake("重新进入审核流程");
                        recordEntity.setOperatorId(0L);
                        recordEntity.setId(SnowFlake.nextId());
                        updateRecordEntities.add(recordEntity);
                    });
                }
                batchUpdateStatementStatus(orderNumS, statementNumSet, updateRecordEntities);
            } else {
                log.info("社区ID为:{}的社区{}日上月没有需要结算的账单", communityIdS.toString(), dayOfMonth);
            }
        } else {
            log.info("{}日没有上月需要结算的社区", dayOfMonth);
        }
    }

    /**
     *@Author: Pipi
     *@Description: 批量新增结算单
     *@Param: statementEntities:
     *@Return: void
     *@Date: 2021/4/22 17:21
     **/
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    protected void batchInsertStatement(List<PropertyFinanceStatementEntity> statementEntities,
                                        HashMap<String, List<Long>> statementOrderUpdateMap,
                                        List<PropertyFinanceStatementRecordEntity> recordEntities
    ) {
        if (!CollectionUtils.isEmpty(statementEntities)) {
            boolean b = this.saveBatch(statementEntities);
        }
        if (!CollectionUtils.isEmpty(statementOrderUpdateMap)) {
            orderMapper.updateStatementStatusByIdS(statementOrderUpdateMap);
        }
        if (!CollectionUtils.isEmpty(recordEntities)) {
            recordMapper.batchInsert(recordEntities);
        }
    }

    /**
     *@Author: Pipi
     *@Description: 批量更新被驳回的结算单状态
     *@Param: orderNumS: 账单ID列表
	 *@Param: statementNumSet: 结算单号集合
     *@Return: void
     *@Date: 2021/4/22 17:45
     **/
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    protected void batchUpdateStatementStatus(ArrayList<Long> orderNumS,
                                              HashSet<String> statementNumSet,
                                              List<PropertyFinanceStatementRecordEntity> recordEntities) {
        if (!CollectionUtils.isEmpty(orderNumS)) {
            orderMapper.updateRejectStatementStatusByIdS(orderNumS);
        }
        if (!CollectionUtils.isEmpty(statementNumSet)) {
            baseMapper.batchUpdateStatementStatusByStatementNum(statementNumSet);
        }
        if (!CollectionUtils.isEmpty(recordEntities)) {
            recordMapper.batchInsert(recordEntities);
        }
    }

    /**
     *@Author: Pipi
     *@Description: 生成结算单号
     *@Param: communityId: 社区ID
     *@Return: java.lang.String
     *@Date: 2021/4/22 15:18
     **/
    private String generateStatementId(Long communityId) {
        StringBuffer statementId = new StringBuffer();
        // 拼接社区主键后4位,如果不足4位用0在前面补齐
        String communityIdString = String.valueOf(communityId);
        if (communityIdString.length() < 4) {
            for (int i = 0; i < 4 - communityIdString.length(); i++) {
                statementId.append("0");
            }
            statementId.append(communityIdString);
        } else {
            String substring = communityIdString.substring(communityIdString.length() - 4, communityIdString.length());
            statementId.append(substring);
        }
        // 拼接结算方式编号后2位,现在只有一种结算方式,指定为01
        statementId.append("01");
        // 拼接Unix时间戳10位
        String dateString = String.valueOf(System.currentTimeMillis());
        String substring = dateString.substring(dateString.length() - 10, dateString.length());
        statementId.append(substring);
        // 拼接2位随机数
        String randomString = String.valueOf((int) (Math.random() * 99));
        if (randomString.length() == 1) {
            statementId.append("0").append(randomString);
        } else {
            statementId.append(randomString);
        }
        return statementId.toString();
    }
    
    /**
    * @Description: 结算单号批量查 单号-结算单数据 映射
     * @Param: [nums]
     * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.property.PropertyFinanceStatementEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    @Override
    public Map<String,PropertyFinanceStatementEntity> queryByStatementNumBatch(Collection<String> nums){
        if(CollectionUtils.isEmpty(nums) || (nums.size() == 1 && nums.contains(null))){
            return new HashMap<>(1);
        }
        return propertyFinanceStatementMapper.queryByStatementNumBatch(nums);
    }

    /**
     * @Description: 条件查询批量结算单号
     * @Param: [query]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/23
     **/
    @Override
    public List<String> queryStatementNumsByCondition(PropertyFinanceStatementEntity query){
        return propertyFinanceStatementMapper.queryStatementNumsByCondition(query);
    }


    /**
     *@Author: Pipi
     *@Description: 物业财务-结算单列表
     *@Param: statementQO:
     *@Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.vo.StatementVO>
     *@Date: 2021/4/23 17:00
     **/
    @Override
    public PageInfo<PropertyFinanceStatementEntity> getStatementList(BaseQO<StatementQO> statementQO) {
        QueryWrapper<PropertyFinanceStatementEntity> queryWrapper = new QueryWrapper<>();
        StatementQO query = statementQO.getQuery();
        Page<PropertyFinanceStatementEntity> page = new Page<>(statementQO.getPage(), statementQO.getSize());
        queryWrapper.select("*, '对公账户' as receiptAccountType");
        buildQueryMapper(queryWrapper, query);
        queryWrapper.last("ORDER BY create_time desc");
        Page<PropertyFinanceStatementEntity> pageData = baseMapper.selectPage(page, queryWrapper);
        PageInfo<PropertyFinanceStatementEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        //金额统计数据(结算单)
        BigDecimal notStatement = new BigDecimal(0);//1.待结算
        BigDecimal statementing = new BigDecimal(0);//2.结算中
        BigDecimal statemented = new BigDecimal(0);//3.已结算
        BigDecimal statementReject = new BigDecimal(0);//4.驳回
        // 金额统计
        // 需要创建新的queryMapper
        QueryWrapper<PropertyFinanceStatementEntity> queryAmountWrapper = new QueryWrapper<>();
        queryAmountWrapper.select("statement_status, sum(total_money) as total_money");
        buildQueryMapper(queryAmountWrapper, query);
        queryAmountWrapper.groupBy("statement_status");
        List<PropertyFinanceStatementEntity> amountResult = baseMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(amountResult)) {
            for (PropertyFinanceStatementEntity entity : amountResult) {
                switch (entity.getStatementStatus()) {
                    case 1:
                        notStatement = entity.getTotalMoney();
                        break;
                    case 2:
                        statementing = entity.getTotalMoney();
                        break;
                    case 3:
                        statemented = entity.getTotalMoney();
                        break;
                    case 4:
                        statementReject = entity.getTotalMoney();
                        break;
                    default:
                        break;
                }
            }
        }
        Map<String,Object> extra = new HashMap<>(4);
        extra.put("notStatement",notStatement);
        extra.put("statementing",statementing);
        extra.put("statemented",statemented);
        extra.put("statementReject",statementReject);
        pageInfo.setExtra(extra);
        return pageInfo;
    }

    /**
     * @Author: Pipi
     * @Description: 查询导出结算单数据
     * @Param: :
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceStatementEntity>
     * @Date: 2021/4/24 15:23
     */
    @Override
    public List<StatementVO> getDownloadStatementList(StatementQO statementQO) {
        List<StatementVO> statementVOS = new ArrayList<>();
        QueryWrapper<PropertyFinanceStatementEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*, '对公账户' as receiptAccountType");
        buildQueryMapper(queryWrapper, statementQO);
        queryWrapper.last("ORDER BY create_time desc");
        List<PropertyFinanceStatementEntity> propertyFinanceStatementEntities = propertyFinanceStatementMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(propertyFinanceStatementEntities)) {
            propertyFinanceStatementEntities.forEach(propertyFinanceStatementEntity -> {
                StatementVO statementVO = new StatementVO();
                BeanUtils.copyProperties(propertyFinanceStatementEntity, statementVO);
                statementVOS.add(statementVO);
            });
        }
        if (statementQO.getExportType() == 2 && !CollectionUtils.isEmpty(statementVOS)) {
            // 导出主从数据时,需要查询账单数据
            Set<String> statementNums = new HashSet<>();
            if (!CollectionUtils.isEmpty(propertyFinanceStatementEntities)) {
                propertyFinanceStatementEntities.forEach(entity ->{
                    statementNums.add(entity.getStatementNum());
                });
            }
            if (!CollectionUtils.isEmpty(statementNums)) {
                List<StatementOrderVO> statementOrderVOS = orderMapper.queryOrderByStatementNum(statementNums);
                if (!CollectionUtils.isEmpty(statementOrderVOS)) {
                    HashMap<String, List<StatementOrderVO>> statementOrderVOMap = new HashMap<>();
                    statementOrderVOS.forEach(statementOrderVO -> {
                        if (statementOrderVOMap != null && statementOrderVOMap.containsKey(statementOrderVO.getStatementNum())) {
                            statementOrderVOMap.get(statementOrderVO.getStatementNum()).add(statementOrderVO);
                        } else {
                            List<StatementOrderVO> statementOrderVOList = new ArrayList<>();
                            statementOrderVOList.add(statementOrderVO);
                            statementOrderVOMap.put(statementOrderVO.getStatementNum(), statementOrderVOList);
                        }
                    });
                    if (!CollectionUtils.isEmpty(statementOrderVOMap)) {
                        statementVOS.forEach(statementVO -> {
                            statementVO.setOrderVOList(statementOrderVOMap.get(statementVO.getStatementNum()));
                        });
                    }
                }
            }
        }
        return statementVOS;
    }

    /**
     *@Author: Pipi
     *@Description: 抽出构建queryWrapper的公共代码
     *@Param: queryWrapper:
     *@Param: query:
     *@Return: void
     *@Date: 2021/4/25 18:04
     **/
    private void buildQueryMapper(QueryWrapper<PropertyFinanceStatementEntity> queryWrapper, StatementQO query) {
        queryWrapper.eq("community_id", query.getCommunityId());
        queryWrapper.eq("deleted", 0);
        if (query.getStatementStatus() != null) {
            queryWrapper.eq("statement_status", query.getStatementStatus());
        }
        if (query.getStatementStartDate() != null) {
            queryWrapper.ge("DATE(start_date)", query.getStatementStartDate());
        }
        if (query.getStatementEndDate() != null) {
            queryWrapper.le("DATE(start_date)", query.getStatementEndDate());
        }
        if (query.getCreateStartTime() != null) {
            queryWrapper.ge("DATE(create_time)", query.getCreateStartTime());
        }
        if (query.getCreateEndTime() != null) {
            queryWrapper.le("DATE(create_time)", query.getCreateEndTime());
        }
        if (!StringUtils.isEmpty(query.getStatementNum())) {
            queryWrapper.like("statement_num", query.getStatementNum());
        }
        // 如果有账单号条件,先查相关的结算单号列表
        if (!StringUtils.isEmpty(query.getOrderNum())) {
            List<String> statementNumS = new ArrayList<>();
            statementNumS = orderMapper.queryStatementNumLikeOrderNum(query.getOrderNum());
            if (CollectionUtils.isEmpty(statementNumS)) {
                statementNumS.add("0");
            }
            queryWrapper.in("statement_num", statementNumS);
        }
    }
}
