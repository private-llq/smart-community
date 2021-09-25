package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.api.ISelectPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.UserMapper;
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
    @Autowired
    private UserMapper userMapper;


    @Override
    public HashMap<String, Object> findOne(Long orderId) {
        PropertyFinanceOrderEntity propertyFinanceOrderEntity = propertyFinanceOrderService.findOne(orderId);
        propertyFinanceOrderEntity.setTotalMoney(propertyFinanceOrderEntity.getPropertyFee().add(propertyFinanceOrderEntity.getPenalSum()));
        HouseEntity entity = houseMapper.selectById(propertyFinanceOrderEntity.getTargetId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("roomName",entity.getBuilding()+entity.getUnit()+entity.getFloor()+entity.getDoor());
        map.put("entity",propertyFinanceOrderEntity);
        if (propertyFinanceOrderEntity.getPayType()!=null&&propertyFinanceOrderEntity.getTripartiteOrder()!=null&&propertyFinanceOrderEntity.getPayTime()!=null){
            UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", propertyFinanceOrderEntity.getUid()));
            propertyFinanceOrderEntity.setRealName(userEntity.getRealName());
        }
        return map;
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
            ids.add(propertyFinanceOrderEntity.getTargetId());
        }
        //房间数据集合
        Map<String,Map<String,Object>> roomMaps = houseMapper.getRoomMap(ids);
        for (PropertyFinanceOrderEntity entity : list) {
            if (entity.getTargetId() != null) {
                //取相应房间map
                Map<String, Object> roomMap = roomMaps.get(BigInteger.valueOf(entity.getTargetId()));
                //在相应房间节点下添加物业费数据
                List dataList = (List) roomMap.get("list");
                if(dataList == null){
                    roomMap.put("list",new ArrayList<>());
                    dataList = (List) roomMap.get("list");
                }
                dataList.add(entity);
                //在相应房间节点下累加总金额
                BigDecimal totalAmount = entity.getPropertyFee().add(entity.getPenalSum());
                if(totalAmount == null){
                    roomMap.put("totalAmount",new BigDecimal("0.00"));
                    totalAmount = (BigDecimal) roomMap.get("totalAmount");
                }
                entity.setTotalMoney(entity.getPropertyFee().add(entity.getPenalSum()));
                roomMap.put("totalAmount",totalAmount);
            }
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
