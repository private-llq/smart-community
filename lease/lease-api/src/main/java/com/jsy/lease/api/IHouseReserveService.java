package com.jsy.lease.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.qo.lease.HouseReserveQO;

import java.util.List;
import java.util.Map;

/**
 * 房屋预约接口提供类
 * @author YuLF
 * @since 2020-12-26 09:21
 */
public interface IHouseReserveService extends IService<HouseReserveEntity> {


    /**
     * 提交预约信息
     * @author YuLF
     * @since  2020/12/26 16:25
     * @Param  qo   请求参数对象
     * @return      返回是否预约信息提交成功
     */
    Boolean add(HouseReserveEntity qo);

    /**
     * 取消预约信息
     * @param qo   取消预约 接收 参数 对象
     * @return      返回取消是否成功修改
     */
    Boolean cancel(HouseReserveQO qo);
}
