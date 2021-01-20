package com.jsy.community.service.impl;

import com.jsy.community.api.LeaseException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.mapper.HouseReserveMapper;
import com.jsy.community.utils.CommonUtils;
import com.jsy.community.vo.lease.HouseReserveVO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 租赁 异步方法 执行器
 * @author YuLF
 * @since 2021-01-19 13:25
 */
@Component
public class HouseAsyncActuator {


    @Resource
    private HouseReserveMapper houseReserveMapper;

    /**
     * 预约消息推送
     * @param sourceUid         发起推送的推送人uid
     * @param HouseLeaseId      房屋id
     * @param pushTitle         推送标题
     * @param pushMsg           推送信息
     */
    @Async(BusinessConst.LEASE_ASYNC_POOL)
    public void pushMsg(String sourceUid, Long HouseLeaseId, String pushTitle, String pushMsg){
        //[接受者信息] 通房屋id拿到房源出租标题 和 用户的 推送id
        HouseReserveVO vo = houseReserveMapper.getPushInfo(HouseLeaseId);
        if( vo == null ){
            throw new LeaseException("没有预约相关信息!");
        }
        //[推送者信息]
        String userNickName = houseReserveMapper.selectNicknameById(sourceUid);
        //组装推送消息
        String builder = userNickName +
                "在" +
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                " " + pushMsg + " " +
                "["+vo.getHouseTitle()+"]";
        //向目标推送
        CommonUtils.pushCommunityMSG(1, vo.getPushId(), pushTitle, builder);
    }

}