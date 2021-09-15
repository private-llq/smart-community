package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarChargeService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarChargeEntity;
import com.jsy.community.mapper.CarChargeMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarChargeQO;
import com.jsy.community.util.TimeUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@DubboService(version = Const.version, group = Const.group_property)
public class CarChargeServiceImpl extends ServiceImpl<CarChargeMapper, CarChargeEntity> implements ICarChargeService {

    @Autowired
    public CarChargeMapper carChargeMapper;



    @Override
    @Transactional
    public Integer SaveCarCharge(CarChargeEntity carChargeEntity,Long communityId) {
        carChargeEntity.setCommunityId(communityId);
        carChargeEntity.setUid(UserUtils.randomUUID());
        carChargeEntity.setOpen(0);//未启用
        int insert = carChargeMapper.insert(carChargeEntity);
        return insert;
    }

    @Override
    @Transactional
    public Integer UpdateCarCharge(CarChargeEntity carChargeEntity) {
        int update = carChargeMapper.update(carChargeEntity, new UpdateWrapper<CarChargeEntity>().eq("uid", carChargeEntity.getUid()));
        return update;
    }

    @Override
    @Transactional
    public Integer DelCarCharge(String uid) {
        CarChargeEntity carChargeEntity = carChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("uid", uid));
        if (carChargeEntity.getOpen()==1){
            throw new PropertyException("启用状态的模板不能被删除，请保证至少启用一个模板！");
        }

        int del = carChargeMapper.delete(new QueryWrapper<CarChargeEntity>().eq("uid", uid));
        return del;
    }


    /**
     * 查询所有模板
     * @param type
     * @param communityId
     * @return
     */
    @Override
    public List<CarChargeEntity> selectCharge(Integer type,Long communityId) {
        List<CarChargeEntity> list = carChargeMapper.selectList(new QueryWrapper<CarChargeEntity>()
                .eq("type", type)
                .eq("community_id",communityId)
                //.eq("open",1)//
        );
        return list;
    }




    /**
     * 根据类型查找并分页
     * @param baseQO
     * @return
     */
    @Override
    public PageInfo listCarChargePage(BaseQO baseQO,Long communityId) {
        Page<CarChargeEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
        PageInfo<CarChargeEntity> pageInfo = new PageInfo<>();
        Page<CarChargeEntity> selectPage = carChargeMapper.selectPage(page,
                new QueryWrapper<CarChargeEntity>()
                        .eq(StringUtils.isNoneBlank((CharSequence) baseQO.getQuery()),"type", baseQO.getQuery())
                        .eq("community_id",communityId)
        );
        pageInfo.setRecords(selectPage.getRecords());
        pageInfo.setTotal(selectPage.getTotal());
        pageInfo.setCurrent(selectPage.getCurrent());
        pageInfo.setSize(selectPage.getSize());
        return pageInfo;
    }


    /**
     * 临时停车收费设置
     * @param carChargeEntity
     * @param adminCommunityId
     * @return
     */
    @Override
    @Transactional
    public Integer temporaryParkingSet(CarChargeEntity carChargeEntity, Long adminCommunityId) {
        carChargeEntity.setCommunityId(adminCommunityId);
        carChargeEntity.setUid(UserUtils.randomUUID());
        int insert = carChargeMapper.insert(carChargeEntity);
        return insert;
    }


    /**
     * @Description: 按社区查询一条缴费规则
     * @author: Hu
     * @since: 2021/8/26 14:47
     * @Param: [communityId]
     * @return: com.jsy.community.entity.property.CarChargeEntity
     */
    @Override
    public CarChargeEntity selectOne(Long communityId) {
        return carChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("community_id",communityId).eq("position","地上").eq("type",0));
    }

    /**
     * 计算该项设置的收费 Test charge
     */
    @Override
    @Transactional
    public BigDecimal testCharge(CarChargeQO carChargeQO) {
        CarChargeEntity carChargeEntity = carChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("uid", carChargeQO.getUuid()));
        //封顶费用 单位/元
        BigDecimal cappingFee = carChargeEntity.getCappingFee();
        //免费时间 单位/分钟
        Integer freeTime = carChargeEntity.getFreeTime();
        //收费价格 元/时
        BigDecimal chargePrice = carChargeEntity.getChargePrice();


        /**
         * 出场时间-入场时间
         */
        LocalDateTime insertTime = carChargeQO.getInTime();//入场时间
        LocalDateTime reTime = carChargeQO.getReTime();//出场时间
        Long hours = Duration.between(insertTime,reTime).toHours();//相差的小时数
        Long minutes = Duration.between(insertTime,reTime).toMinutes();//相差的分钟


        /**
         * 未超过免费时间不收费
         */
        if (minutes<=freeTime){
            return new BigDecimal(0);
        }

        /**
         * 停车不足1个小时 按1个小时收费
         */
        if (minutes<60){
            BigDecimal pic= new BigDecimal(1).multiply(chargePrice);
            return pic;
        }


        /**
         * 停车超过一小时，小于24小时
         */
        if (minutes>=60&&minutes<24*60){

            BigDecimal pic=new BigDecimal(hours).multiply(chargePrice);
            /**
             * 超过小时整点 自动加一小时费用
             */
            if (minutes%60>0){
                pic=pic.add(chargePrice);
                return pic;
            }
            return pic;
        }


        /**
         * 停车超过24小时 按一天的封顶费用计算，剩余的分钟不足一小时 按一小时计算
         */
        if (minutes>=24*60){
            HashMap<String, Long> datePoor = TimeUtils.getDatePoor(insertTime, reTime);
            Long day = datePoor.get("day");
            Long hour = datePoor.get("hour");
            Long min = datePoor.get("min");
            BigDecimal pic= cappingFee.multiply(BigDecimal.valueOf(day));//天数按封顶费用算
            pic=pic.add(chargePrice.multiply(BigDecimal.valueOf(hour)));//小时按临时费用计算
            if (min>0){
                pic=pic.add(chargePrice);//不足一小时按1小时计算

                return pic;
            }
            return pic;
        }
        return null;
    }

    /**
     * 计算临时车下单的费用 
     * @param carChargeQO {"社区id,车牌颜色,入场时间,结算时间"}
     *                    如果结算时间到出闸时间超过5分钟（物业设置）,就拿到要出闸的时间和下次结算时间作为一个新订单
     */
    @Override
    @Transactional
    public BigDecimal charge(CarChargeQO carChargeQO) {

        LocalDateTime inTime = carChargeQO.getInTime();//入场时间
        LocalDateTime reTime = LocalDateTime.now();//结算时间(不出外部拿时间，直接获取当前时间)
        String carColor = carChargeQO.getCarColor();//车牌颜色
        String communityId = carChargeQO.getCommunityId();//社区id

        Integer plateType;//默认其他车牌类型
        if ( StringUtils.containsAny(carColor,"黄色","黄牌","黄")){
            plateType=0;//黄牌
        }else {
            plateType=1;//其他车牌
        }

        CarChargeEntity carChargeEntity = carChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>()
                .eq("community_id",communityId)
                .eq("type",1)
                .eq("plate_type",plateType)
                .eq("open",1)
        );
        if (Objects.isNull(carChargeEntity)){
            throw new PropertyException("请先在收费设置临时里添加一个已启用的收费模板！");
        }

        //封顶费用 单位/元
        BigDecimal cappingFee = carChargeEntity.getCappingFee();
        //免费时间 单位/分钟
        Integer freeTime = carChargeEntity.getFreeTime();
        //收费价格 元/时
        BigDecimal chargePrice = carChargeEntity.getChargePrice();


        /**
         * 出场时间-入场时间
         */
        Long hours = Duration.between(inTime,reTime).toHours();//相差的小时数
        Long minutes = Duration.between(inTime,reTime).toMinutes();//相差的分钟


        /**
         * 未超过免费时间不收费
         */
        if (minutes<=freeTime){
            return new BigDecimal(0);
        }

        /**
         * 停车不足1个小时 按1个小时收费
         */
        if (minutes<60){
            BigDecimal pic= new BigDecimal(1).multiply(chargePrice);
            return pic;
        }


        /**
         * 停车超过一小时，小于24小时
         */
        if (minutes>=60&&minutes<24*60){

            BigDecimal pic=new BigDecimal(hours).multiply(chargePrice);
            /**
             * 超过小时整点 自动加一小时费用
             */
            if (minutes%60>0){
                pic=pic.add(chargePrice);
                return pic;
            }
            return pic;
        }


        /**
         * 停车超过24小时 按一天的封顶费用计算，剩余的分钟不足一小时 按一小时计算
         */
        if (minutes>=24*60){
            HashMap<String, Long> datePoor = TimeUtils.getDatePoor(inTime, reTime);
            Long day = datePoor.get("day");
            Long hour = datePoor.get("hour");
            Long min = datePoor.get("min");
            BigDecimal pic= cappingFee.multiply(BigDecimal.valueOf(day));//天数按封顶费用算
            pic=pic.add(chargePrice.multiply(BigDecimal.valueOf(hour)));//小时按临时费用计算
            if (min>0){
                pic=pic.add(chargePrice);//不足一小时按1小时计算

                return pic;
            }
            return pic;
        }
        return null;
    }



    /**
     * 查询启用的包月车辆所有收费设置标准
     * @param adminCommunityId
     * @return
     */
    @Override
    public List<CarChargeEntity> ListCharge(Long adminCommunityId) {
        List<CarChargeEntity> chargeEntityList = carChargeMapper.selectList(new QueryWrapper<CarChargeEntity>()
                .eq("community_id", adminCommunityId)
                .eq("type",0)
                .eq("open",1)
        );
        return chargeEntityList;
    }


    /**
     * 查询临时停车收费标准
     */

    @Override
    public List<CarChargeEntity> ListCharge2(Long adminCommunityId) {
        List<CarChargeEntity> chargeEntityList = carChargeMapper.selectList(new QueryWrapper<CarChargeEntity>()
                .eq("community_id", adminCommunityId)
                .eq("type", 1)
                .eq("open",1)
        );
        return chargeEntityList;
    }


    /**
     * 启用
     * @param uid
     */
    @Override
    @Transactional
    public void openCarCharge(String uid,Integer type,Long adminCommunityId) {

        CarChargeEntity entityBase = carChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("uid", uid));
        if (type==0){//月租
            Integer position = entityBase.getPosition();
            if (position==0){//地上

                //查询之前地上的改为0
                CarChargeEntity entity = new CarChargeEntity();
                entity.setOpen(0);
                carChargeMapper.update(entity,new QueryWrapper<CarChargeEntity>().eq("position",0).eq("community_id", adminCommunityId).eq("open",1));


                //修改新启用状态为1
                CarChargeEntity carChargeEntity = new CarChargeEntity();
                carChargeEntity.setOpen(1);//已启用
                carChargeMapper.update(carChargeEntity,new QueryWrapper<CarChargeEntity>().eq("uid",uid));


            }
            if (position==1){//地下

                //查询之前地上的改为0
                CarChargeEntity entity = new CarChargeEntity();
                entity.setOpen(0);
                carChargeMapper.update(entity,new QueryWrapper<CarChargeEntity>().eq("position",1).eq("community_id", adminCommunityId).eq("open",1));


                //修改新启用状态为1
                CarChargeEntity carChargeEntity = new CarChargeEntity();
                carChargeEntity.setOpen(1);//已启用
                carChargeMapper.update(carChargeEntity,new QueryWrapper<CarChargeEntity>().eq("uid",uid));
            }

        }
        if (type==1){//临时
            Integer plateType = entityBase.getPlateType();
            if (plateType==0){//黄牌
                //查询之前地上的改为0
                CarChargeEntity entity = new CarChargeEntity();
                entity.setOpen(0);
                carChargeMapper.update(entity,new QueryWrapper<CarChargeEntity>().eq("plate_type",0).eq("community_id", adminCommunityId).eq("open",1));


                //修改新启用状态为1
                CarChargeEntity carChargeEntity = new CarChargeEntity();
                carChargeEntity.setOpen(1);//已启用
                carChargeMapper.update(carChargeEntity,new QueryWrapper<CarChargeEntity>().eq("uid",uid));
            }
            if (plateType==1){//其他车牌

                //查询之前地上的改为0
                CarChargeEntity entity = new CarChargeEntity();
                entity.setOpen(0);
                carChargeMapper.update(entity,new QueryWrapper<CarChargeEntity>().eq("plate_type",1).eq("community_id", adminCommunityId).eq("open",1));


                //修改新启用状态为1
                CarChargeEntity carChargeEntity = new CarChargeEntity();
                carChargeEntity.setOpen(1);//已启用
                carChargeMapper.update(carChargeEntity,new QueryWrapper<CarChargeEntity>().eq("uid",uid));
            }

        }

    }


    /**
     * 查询包月车辆单个收费设置标准
     * @param uid
     * @param adminCommunityId
     * @return
     */
    @Override
    public CarChargeEntity selectOneCharge(String uid, Long adminCommunityId) {
        CarChargeEntity carChargeEntity = carChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>()
                .eq("uid", uid)
                .eq("community_id", adminCommunityId)
                .eq("open",1)
        );
        return carChargeEntity;
    }




}






