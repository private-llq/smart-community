package com.jsy.community.service.impl;

import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.api.ISelectPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.mapper.HouseMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-07-05 10:54
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class SelectPropertyFinanceOrderServiceImpl implements ISelectPropertyFinanceOrderService {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceOrderService propertyFinanceOrderService;

    @Autowired
    private HouseMapper houseMapper;


    @Override
    public PropertyFinanceOrderEntity findOne(String userId, Long orderId) {
        return propertyFinanceOrderService.findOne(userId,orderId);
    }

    /**
     * @Description: 查询物业账单
     * @author: Hu
     * @since: 2021/7/5 11:08
     * @Param: [userId]
     * @return: void
     */
    @Override
    public List<Map<String,Object>> list(PropertyFinanceOrderEntity qo) {

        //查个人小区物业费账单(多房间)
        List<PropertyFinanceOrderEntity> list = propertyFinanceOrderService.selectByUserList(qo);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }

        //最終返回封装数据
        List<Map<String,Object>> returnList = new ArrayList<>();

        //房间id集合
        Set<Long> ids = new HashSet<>();
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
            ids.add(propertyFinanceOrderEntity.getHouseId());
        }
        //房间数据集合
        Map<String,Map<String,Object>> roomMaps = houseMapper.getRoomMap(ids);
        for (PropertyFinanceOrderEntity entity : list) {
            //取相应房间map
            Map<String, Object> roomMap = roomMaps.get(BigInteger.valueOf(entity.getHouseId()));
            //在相应房间节点下添加物业费数据
            List dataList = (List) roomMap.get("list");
            if(dataList == null){
                roomMap.put("list",new ArrayList<>());
                dataList = (List) roomMap.get("list");
            }
            dataList.add(entity);
            //在相应房间节点下累加总金额
            BigDecimal totalAmount = (BigDecimal) roomMap.get("totalAmount");
            if(totalAmount == null){
                roomMap.put("totalAmount",new BigDecimal("0.00"));
                totalAmount = (BigDecimal) roomMap.get("totalAmount");
            }
            roomMap.put("totalAmount",totalAmount.add(entity.getTotalMoney()));
        }
        returnList.addAll(roomMaps.values());
        return returnList;



//
//        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
//            String doorName =  houseMap.get(propertyFinanceOrderEntity.getHouseId());
//            map1.put(String.valueOf(propertyFinanceOrderEntity.getOrderTime()),propertyFinanceOrderEntity);
//        }




    }

}
