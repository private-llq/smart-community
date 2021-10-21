package com.jsy.community.service.impl;

import com.jsy.community.api.ICarPositionService;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.api.ISelectPropertyFinanceOrderService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.mapper.AppCarOrderMapper;
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
    private AppCarOrderMapper carOrderMapper;
    
    @Autowired
    private ICarPositionService carPositionService;

    @Autowired
    private UserMapper userMapper;


    @Override
    public List<PropertyFinanceOrderEntity> findOne(String orderId,Integer orderStatus) {
        List<PropertyFinanceOrderEntity> list = null;
        if (orderStatus==0){
            list= new LinkedList<>();
            PropertyFinanceOrderEntity propertyFinanceOrderEntity = propertyFinanceOrderService.findOne(Long.parseLong(orderId));
            if(propertyFinanceOrderEntity.getAssociatedType()==2){
                CarPositionEntity entity = carPositionService.selectOne(propertyFinanceOrderEntity.getTargetId());
                HouseEntity houseEntity = houseMapper.selectById(entity.getHouseId());
                propertyFinanceOrderEntity.setAddress(houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor());
                propertyFinanceOrderEntity.setFeeRuleName(BusinessEnum.FeeRuleNameEnum.getName(propertyFinanceOrderEntity.getType()));
            } else {
                HouseEntity houseEntity = houseMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                propertyFinanceOrderEntity.setAddress(houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor());
                propertyFinanceOrderEntity.setFeeRuleName(BusinessEnum.FeeRuleNameEnum.getName(propertyFinanceOrderEntity.getType()));
            }
            if (propertyFinanceOrderEntity.getPenalSum()!=null||!propertyFinanceOrderEntity.getPenalSum().equals(0.00)){

                propertyFinanceOrderEntity.setTotalMoney(propertyFinanceOrderEntity.getPropertyFee().add(propertyFinanceOrderEntity.getPenalSum()));
            }else {
                propertyFinanceOrderEntity.setTotalMoney(propertyFinanceOrderEntity.getPropertyFee());
            }
            list.add(propertyFinanceOrderEntity);
            return list;
        } else {
            LinkedList<Map<String,Object>> objects = new LinkedList<>();
            Map houseMap = new HashMap();
            Map<String,List<PropertyFinanceOrderEntity>> map = new HashMap();
            Set<Long> houseIds = new HashSet();
            list = propertyFinanceOrderService.findOrder(orderId);
            for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
                if (propertyFinanceOrderEntity.getAssociatedType()==1){
                    houseIds.add(propertyFinanceOrderEntity.getTargetId());
                }
            }
            if (houseIds.size()!=0){
                List<HouseEntity> houseEntities = houseMapper.selectBatchIds(houseIds);
                for (HouseEntity entity : houseEntities) {
                    houseMap.put(entity.getId(),entity.getBuilding()+entity.getUnit()+entity.getDoor());
                }
                for (PropertyFinanceOrderEntity entity : list) {
                    entity.setTotalMoney(entity.getPropertyFee().add(entity.getPenalSum()));
                    entity.setAddress((String) houseMap.get(entity.getTargetId()));
                    entity.setFeeRuleName(BusinessEnum.FeeRuleNameEnum.getName(entity.getType()));
                }
            }
            return list;
        }
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
                if (roomMap.get("list") == null) {
                    roomMap.put("list", new ArrayList<>());
                }
                List dataList = (List) roomMap.get("list");
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

    /**
     * @Description: 查询物业账单
     * @author: Hu
     * @since: 2021/7/5 11:08
     * @Param: [userId]
     * @return: void
     */
    public List<Map<String,Object>> listV2(PropertyFinanceOrderEntity qo) {
        HashMap<Long, String> houseMap = new HashMap<>();
        Map<String,Object> map = null;
        LinkedList<PropertyFinanceOrderEntity> orderEntities = null;
        BigDecimal totalAmount = null;

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
        List<HouseEntity> houseEntities = houseMapper.selectBatchIds(ids);
        for (HouseEntity houseEntity : houseEntities) {
            houseMap.put(houseEntity.getId(),houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor());
        }

        //根据房间封装返回账单
        for (HouseEntity houseEntity : houseEntities) {
            map = new HashMap<>();
            totalAmount = new BigDecimal(0.00);
            orderEntities = new LinkedList<>();
            for (PropertyFinanceOrderEntity orderEntity : list) {
                if (houseEntity.getId().equals(orderEntity.getTargetId())){
                    orderEntities.add(orderEntity);
                    if (!orderEntity.getPenalSum().equals(0.00)){
                        totalAmount = totalAmount.add(orderEntity.getPropertyFee());
                        orderEntity.setTotalMoney(orderEntity.getPropertyFee());
                    } else {
                        orderEntity.setTotalMoney(orderEntity.getPropertyFee().add(orderEntity.getPenalSum()));
                        totalAmount = totalAmount.add(orderEntity.getPropertyFee().add(orderEntity.getPenalSum()));
                    }
                }
            }
            map.put("totalAmount",totalAmount);
            map.put("list",orderEntities);
            map.put("roomName",houseMap.get(houseEntity.getId()));
            map.put("id",houseEntity.getId());
            returnList.add(map);
        }
        return returnList;
    }

}
