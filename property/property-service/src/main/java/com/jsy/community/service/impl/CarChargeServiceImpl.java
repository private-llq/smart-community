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
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@DubboService(version = Const.version, group = Const.group)
public class CarChargeServiceImpl extends ServiceImpl<CarChargeMapper, CarChargeEntity> implements ICarChargeService {

    @Autowired
    public CarChargeMapper carChargeMapper;


    @Override
    @Transactional
    public Integer SaveCarCharge(CarChargeEntity carChargeEntity,Long communityId) {
        carChargeEntity.setCommunityId(communityId);
        carChargeEntity.setUid(UserUtils.randomUUID());
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
        int del = carChargeMapper.delete(new QueryWrapper<CarChargeEntity>().eq("uid", uid));
        return del;
    }

    @Override
    public List<CarChargeEntity> selectCharge(Integer type,Long communityId) {
        List<CarChargeEntity> list = carChargeMapper.selectList(new QueryWrapper<CarChargeEntity>()
                .eq("type", type)
                .eq("community_id",communityId)
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
    public BigDecimal testCharge(CarChargeQO carChargeQO) {
        CarChargeEntity carChargeEntity = carChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("uid", carChargeQO.getUuid()));
        //封顶费用 单位/元
        BigDecimal cappingFee = carChargeEntity.getCappingFee();
        //免费时间 单位/分钟
        Integer freeTime = carChargeEntity.getFreeTime();
        //收费价格 元/时
        BigDecimal chargePrice = carChargeEntity.getChargePrice();

        /**
         * 出场时间-（入场时间+免费时间）
         */
        LocalDateTime insertTime = carChargeQO.getInTime().plusMinutes(freeTime);//入场时间+免费时间
        Long hours = Duration.between(insertTime,carChargeQO.getReTime()).toHours();//相差的小时数

        Long minutes = Duration.between(insertTime,carChargeQO.getReTime()).toMinutes();//相差的分钟

        /**
         * 超过小时整点 自动加一小时
         */
        if (minutes%60>0){
           hours+=1;
           throw new PropertyException();
        }
        /**
         * 乘 收费价格
         */
        BigDecimal pic=new BigDecimal(hours).multiply(chargePrice);

        /**
         * 停车不足一个小时 按一个小时收费
         */
        if (hours<1L){
           pic= new BigDecimal(1).multiply(chargePrice);
        }

        /**
         * 不超过封顶费用 超过按封顶费用计算
         */
        if (cappingFee.compareTo(pic)==-1){
            return cappingFee;
        }
        return pic;
    }


    /**
     * 查询包月车辆所有收费设置标准
     * @param adminCommunityId
     * @return
     */
    @Override
    public List<CarChargeEntity> ListCharge(Long adminCommunityId) {
        List<CarChargeEntity> chargeEntityList = carChargeMapper.selectList(new QueryWrapper<CarChargeEntity>().eq("community_id", adminCommunityId));
        return chargeEntityList;
    }

    /**
     * 查询包月车辆单个收费设置标准
     * @param uid
     * @param adminCommunityId
     * @return
     */
    @Override
    public CarChargeEntity selectOneCharge(String uid, Long adminCommunityId) {
        CarChargeEntity carChargeEntity = carChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("uid", uid).eq("community_id", adminCommunityId));
        return carChargeEntity;
    }


}






