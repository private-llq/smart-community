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
    public List<Map<String,Object>> list(String userId,Long communityId) {

        //查个人小区物业费账单(多房间)
        List<PropertyFinanceOrderEntity> list = propertyFinanceOrderService.selectByUserList(userId,communityId);
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
        Map<String,Map<String,Object>> roomMap = houseMapper.getRoomMap(ids);
        
        for (PropertyFinanceOrderEntity entity : list) {
            if(roomMap.get(BigInteger.valueOf(entity.getHouseId())).get("list") == null){
                roomMap.get(BigInteger.valueOf(entity.getHouseId())).put("list",new ArrayList<>());
            }
            List dataList = (List) roomMap.get(BigInteger.valueOf(entity.getHouseId())).get("list");
            dataList.add(entity);
        }
        returnList.addAll(roomMap.values());
        return returnList;



//
//        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
//            String doorName =  houseMap.get(propertyFinanceOrderEntity.getHouseId());
//            map1.put(String.valueOf(propertyFinanceOrderEntity.getOrderTime()),propertyFinanceOrderEntity);
//        }




    }

}
