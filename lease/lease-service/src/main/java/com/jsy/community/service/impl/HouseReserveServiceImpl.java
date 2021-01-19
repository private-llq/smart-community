package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.mapper.HouseReserveMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseReserveQO;
import com.jsy.community.util.HouseHelper;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.lease.HouseReserveVO;
import com.jsy.community.api.IHouseConstService;
import com.jsy.community.api.IHouseReserveService;
import com.jsy.community.api.LeaseException;
import com.jsy.community.mapper.HouseLeaseMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-26 14:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class HouseReserveServiceImpl extends ServiceImpl<HouseReserveMapper, HouseReserveEntity> implements IHouseReserveService {

    @Resource
    private HouseReserveMapper houseReserveMapper;

    @Resource
    private HouseLeaseMapper houseLeaseMapper;

    @Resource
    private HouseAsyncActuator houseAsyncActuator;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseConstService houseConstService;


    /**
     * 提交预约信息
     *
     * @return 返回是否预约信息提交成功
     * @author YuLF
     * @Param qo   请求参数对象
     * @since 2020/12/26 16:25
     */
    @Transactional(rollbackFor=Exception.class)
    @Override
    public Boolean add(HouseReserveEntity qo) {
        Integer integer = houseReserveMapper.existHouseLeaseId(qo.getHouseLeaseId());
        if( integer < 1){
            throw new LeaseException("房屋信息不存在!");
        }
        qo.setId(SnowFlake.nextId());
        qo.setReserveStatus(1);
        int insert = houseReserveMapper.insertReserve(qo);
        //推送
        if( insert > 0 ){
            houseAsyncActuator.pushMsg(qo.getReserveUid(), qo.getId(), "预约请求", "预约了您的房子");
            return true;
        }
        return false;
    }





    /**
     * 取消预约信息
     *
     * @param qo 取消预约 接收 参数 对象
     * @return 返回取消是否成功修改
     */
    @Override
    public Boolean cancel(HouseReserveQO qo) {
        //0代表着状态预约已取消
        qo.setReserveStatus(0);
        Integer integer = houseReserveMapper.cancelReserveState(qo);
        //推送
        if( integer > 0 ){
            houseAsyncActuator.pushMsg(qo.getReserveUid(), qo.getId(), "取消预约", "取消了预约");
            return true;
        }
        return false;
    }


    /**
     * 全部预约信息
     *
     * @author YuLF
     * @since 2020/12/28 10:22
     */
    @Override
    public List<HouseReserveVO> whole(BaseQO<HouseReserveQO> qo, String uid) {
        qo.setPage((qo.getPage() - 1) * qo.getSize());
        //1.查出我的预约信息
        List<HouseReserveVO> meReserveVos = houseReserveMapper.meReserveHouse(qo, uid);
        //2.查出预约我的信息
        List<HouseReserveVO> reserveMeVos = houseReserveMapper.reserveMeHouse(qo, uid);
        //返回VO
        List<HouseReserveVO> reserveVos = new ArrayList<>(meReserveVos.size() + reserveMeVos.size());
        reserveVos.addAll(meReserveVos);
        reserveVos.addAll(reserveMeVos);
        reserveVos.forEach(r -> {
            //1.从缓存通过id和类型取出 中文Name
            //整租还是合租
            r.setHouseLeaseMode(houseConstService.getConstNameByConstTypeCode(Long.parseLong(r.getHouseLeaseMode()), 11L));
            //押金方式
            r.setHouseLeaseDeposit(houseConstService.getConstNameByConstTypeCode(Long.parseLong(r.getHouseLeaseDeposit()), 1L));
            //房型结构：三室一厅、四室一厅...
            r.setHouseType(HouseHelper.parseHouseType(r.getHouseType()));
            //平方米
            r.setHouseSquareMeter(r.getHouseSquareMeter() + "m²");
            //房屋朝向
            r.setHouseDirection(BusinessEnum.HouseDirectionEnum.getDirectionName(r.getHouseDirection()));
            //2. 第一张图片地址
            r.setHouseImageUrl(houseLeaseMapper.queryHouseImgById(r.getHouseImageId(), r.getHouseLeaseId()));
        });
        return reserveVos;
    }


    /**
     * 确认预约信息
     * @param qo          预约信息id参数对象
     * @return            返回确认是否成功修改
     */
    @Override
    public Boolean confirm(HouseReserveQO qo, String uid) {
        //验证当前提交请求的用户是否有权利修改这条预约信息 房子是他自己发布的 他才有权利确认
        String databasesUid = houseReserveMapper.getUidByHouseReserveId(qo.getId());
        if(!uid.equals(databasesUid)){
            throw new LeaseException("您无权对此预约信息进行确认!");
        }
        Integer row = houseReserveMapper.confirm(qo);
        return row > 0;
    }

    @Override
    public Boolean reject(HouseReserveQO qo) {
        Integer integer = houseReserveMapper.rejectReserve(qo);
        if( integer > 0 ){
            houseAsyncActuator.pushMsg(qo.getReserveUid(), qo.getId(), "拒绝预约", "不方便预约");
            return true;
        }
        return false;
    }
}
