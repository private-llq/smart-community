package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.mapper.PropertyFinanceOrderMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业房间账单
 * @author: Hu
 * @create: 2021-04-20 16:31
 **/
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class PropertyFinanceOrderServiceImpl extends ServiceImpl<PropertyFinanceOrderMapper, PropertyFinanceOrderEntity> implements IPropertyFinanceOrderService {
    @Autowired
    private PropertyFinanceOrderMapper propertyFinanceOrderMapper;

    @Autowired
    private PropertyFeeRuleMapper propertyFeeRuleMapper;



    @Override
    @Transactional
    public void updateDays(){
        List<Long> list=propertyFinanceOrderMapper.communityIdList();
        for (Long id : list) {
            PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("community_id",id));
            if (entity != null) {
            List<HouseEntity> entities = propertyFinanceOrderMapper.selectHouseAll(id);
            for (HouseEntity houseEntity : entities) {
                    if (entity.getPeriod() == 1) {
                        PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
                        orderEntity.setOrderNum(getOrderNum(id+"",entity.getSerialNumber()));
                        orderEntity.setOrderTime(LocalDate.now());
                        orderEntity.setCommunityId(id);
                        orderEntity.setUid(houseEntity.getUid());
                        orderEntity.setHouseId(houseEntity.getId());
                        orderEntity.setPropertyFee(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setPenalSum(new BigDecimal(0));
                        orderEntity.setTotalMoney(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setOrderStatus(0);
                        orderEntity.setId(SnowFlake.nextId());
                        propertyFinanceOrderMapper.insert(orderEntity);
                    }else if (LocalDate.now().getDayOfMonth() == 1) {
                        PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
                        orderEntity.setOrderNum(getOrderNum(id+"",entity.getSerialNumber()));
                        orderEntity.setOrderTime(LocalDate.now());
                        orderEntity.setCommunityId(id);
                        orderEntity.setUid(houseEntity.getUid());
                        orderEntity.setHouseId(houseEntity.getId());
                        orderEntity.setPropertyFee(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setPenalSum(new BigDecimal(0));
                        orderEntity.setTotalMoney(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setOrderStatus(0);
                        orderEntity.setId(SnowFlake.nextId());
                        propertyFinanceOrderMapper.insert(orderEntity);
                    }
                }
            }

        }

    }

    /**
     *@Author: Pipi
     *@Description: 获取上一个月的需要结算和被驳回的账单
     *@Param: communityIdS: 社区ID列表
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     *@Date: 2021/4/22 10:24
     **/
    @Override
    public List<PropertyFinanceOrderEntity> getNeedStatementOrderList(List<Long> communityIdS) {
        return baseMapper.queryNeedStatementOrderListByCommunityIdAndOrderTime(communityIdS);
    }

    public String getOrderNum(String communityId,String serialNumber){
        StringBuilder str=new StringBuilder();
        if (communityId.length()>=4){
            String s = communityId.substring(communityId.length() - 4, communityId.length());
            str.append(s);
        }else {
            if (communityId.length()==3){
                str.append("0"+communityId);
            } else{
                if (communityId.length()==2){
                    str.append("00"+communityId);
                } else {
                    str.append("000"+communityId);
                }
            }
        }
        String substring = serialNumber.substring(serialNumber.length() - 2, serialNumber.length());
        str.append(substring);
        long time = new Date().getTime();
        String s = String.valueOf(time).substring(String.valueOf(time).length() - 10, String.valueOf(time).length());
        str.append(time);
        int s1=(int) (Math.random() * 99);
        str.append(s1);
        return str.toString();
    }

    /**
    * @Description: 根据收款单号批量查询列表
     * @Param: [receiptNums,query]
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    @Override
    public List<PropertyFinanceOrderEntity> queryByReceiptNums(Collection<String> receiptNums, PropertyFinanceOrderEntity query){
        return propertyFinanceOrderMapper.queryByReceiptNums(receiptNums,query);
    }
    
    /**
    * @Description: 账单号模糊查询收款单号列表
     * @Param: [orderNum]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    @Override
    public List<String> queryReceiptNumsListByOrderNumLike(String orderNum){
        return propertyFinanceOrderMapper.queryReceiptNumsListByOrderNumLike(orderNum);
    }
    

}
