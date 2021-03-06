package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseReserveQO;
import com.jsy.community.vo.lease.HouseReserveVO;

import java.util.List;

/**
 * 房屋预约接口提供类
 *
 * @author YuLF
 * @since 2020-12-26 09:21
 */
public interface IHouseReserveService extends IService<HouseReserveEntity> {


    /**
     * 提交预约信息
     *
     * @param qo 请求参数
     * @return 返回是否预约信息提交成功
     * @author YuLF
     * @since 2020/12/26 16:25
     */
    Boolean add(HouseReserveEntity qo);

    /**
     * 取消预约信息
     *
     * @param qo 取消预约 接收 参数 对象
     * @return 返回取消是否成功修改
     */
    Boolean cancel(HouseReserveQO qo);

    /**
     * 全部预约信息
     *
     * @param qo  请求参数
     * @param uid 用户id
     * @return 返回用户的全部预约信息
     * @author YuLF
     * @since 2020/12/28 10:22
     */
    List<HouseReserveVO> whole(BaseQO<HouseReserveQO> qo, String uid);

    /**
     * 确认预约信息
     *
     * @param qo  确认预约 请求参数对象
     * @param uid 用户id
     * @return 返回确认是否成功修改
     */
    Boolean confirm(HouseReserveQO qo, String uid);

    /**
     * 拒绝预约信息
     *
     * @param qo 请求参数对象
     * @return 返回是否成功修改
     */
    Boolean reject(HouseReserveQO qo);

    /**
     * @Author: Pipi
     * @Description: 删除预约消息
     * @param: qo:
     * @Return: java.lang.Boolean
     * @Date: 2021/3/30 11:35
     **/
    Boolean delete(HouseReserveQO qo);

    /**
     * @Author: Pipi
     * @Description: 租房用户确认完成看房
     * @param: qo:
     * @Return: java.lang.Boolean
     * @Date: 2021/3/30 15:25
     **/
    Boolean completeChecking(HouseReserveQO qo);

    /**
     * @Author: Pipi
     * @Description: 定时完成看房
     * @param: :
     * @Return: java.lang.Integer
     * @Date: 2021/3/31 10:44
     **/
    Integer timingCompleteChecking();
}
