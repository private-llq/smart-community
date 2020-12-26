package com.jsy.lease.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.qo.lease.HouseReserveQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.lease.api.IHouseReserveService;
import com.jsy.lease.mapper.HouseReserveMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author YuLF
 * @since 2020-12-26 14:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class IHouseReserveServiceImpl extends ServiceImpl<HouseReserveMapper, HouseReserveEntity> implements IHouseReserveService {

    @Resource
    private HouseReserveMapper houseReserveMapper;


    /**
     * 提交预约信息 
     * @author YuLF
     * @since  2020/12/26 16:25
     * @Param  qo   请求参数对象
     * @return      返回是否预约信息提交成功
     */
    @Transactional
    @Override
    public Boolean add(HouseReserveEntity qo) {
        qo.setId(SnowFlake.nextId());
        qo.setReserveStatus(1);
        int insert = houseReserveMapper.insert(qo);
        //TODO: 使用业主 房屋 id 查出业主uid 并给业主uid推送当前数据，告诉他已经有人在什么时候预约了您的房子
        return insert > 0;
    }


    /**
     * 取消预约信息
     * @param qo   取消预约 接收 参数 对象
     * @return      返回取消是否成功修改
     */
    @Override
    public Boolean cancel(HouseReserveQO qo) {
        //TODO: 取消预约后 推送消息至业主 告诉他 某某取消了预约
        return houseReserveMapper.cancel(qo)  > 0;
    }
}
