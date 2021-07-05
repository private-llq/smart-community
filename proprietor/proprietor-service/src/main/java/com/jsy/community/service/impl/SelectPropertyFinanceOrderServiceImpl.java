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


    /**
     * @Description: 查询物业账单
     * @author: Hu
     * @since: 2021/7/5 11:08
     * @Param: [userId]
     * @return: void
     */
    @Override
    public Map<String,List<PropertyFinanceOrderEntity>> list(String userId,Long communityId) {

        //查个人小区物业费账单(多房间)
        List<PropertyFinanceOrderEntity> list = propertyFinanceOrderService.selectByUserList(userId,communityId);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }

//        //最終返回封装数据
//        List<Map<String,List<PropertyFinanceOrderEntity>>> returnList = new ArrayList<>();

        //房间id集合
        Set<Long> ids = new HashSet<>();
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
            ids.add(propertyFinanceOrderEntity.getHouseId());
        }
        //房间数据集合
        Map<String,Map<String,Object>> aaa = houseMapper.getRoomMap(ids);
        //最終返回封装数据
        Map<String,List<PropertyFinanceOrderEntity>> houseMap = new HashMap<>();
        LinkedList<Object> orderList;
        for (PropertyFinanceOrderEntity entity : list) {
            Map<String,Object> houseIdAndNameMap = aaa.get(BigInteger.valueOf(entity.getHouseId()));
            if(houseMap.get(houseIdAndNameMap.get("roomName")) == null){
                houseMap.put(String.valueOf(houseIdAndNameMap.get("roomName")),new ArrayList<>());
            }
            houseMap.get(houseIdAndNameMap.get("roomName")).add(entity);
        }

        return houseMap;



//
//        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
//            String doorName =  houseMap.get(propertyFinanceOrderEntity.getHouseId());
//            map1.put(String.valueOf(propertyFinanceOrderEntity.getOrderTime()),propertyFinanceOrderEntity);
//        }




    }

}
