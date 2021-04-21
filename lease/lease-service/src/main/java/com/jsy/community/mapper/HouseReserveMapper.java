package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseReserveQO;
import com.jsy.community.vo.lease.HouseReserveVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 房屋预约 Mapper 接口
 * @author YuLFHouseReserveEntity
 * @since 2020-12-26
 */
public interface HouseReserveMapper extends BaseMapper<HouseReserveEntity> {



    /**
     * 我的预约信息
     * @param qo        请求参数
     * @param uid       用户id
     * @author YuLF
     * @since  2020/12/28 10:22
     * @return          返回预约信息列表
     */
    List<HouseReserveVO> meReserveHouse(@Param("qo") BaseQO<HouseReserveQO> qo, @Param("uid") String uid);


    /**
     * 预约我的信息
     * @param qo        请求参数
     * @param uid       用户id
     * @author YuLF
     * @since  2020/12/28 10:22
     * @return          返回预约信息列表
     */
    List<HouseReserveVO> reserveMeHouse(@Param("qo") BaseQO<HouseReserveQO> qo, @Param("uid") String uid);


    /**
     * 确认预约信息
     * @param qo          预约信息id参数对象
     * @return            返回确认是否成功修改
     */
    Integer confirm(HouseReserveQO qo);

    /**
     *@Author: Pipi
     *@Description: 删除我预约的看房信息,只能删除已取消或已完成的
     *@param: qo:
     *@Return: java.lang.Integer
     *@Date: 2021/3/30 13:42
     **/
    Integer deleteMyReserve(HouseReserveQO qo);

    /**
     *@Author: Pipi
     *@Description: 删除预约我的看房信息,只能删除不是待看房的
     *@param: qo: 
     *@Return: java.lang.Integer
     *@Date: 2021/3/30 13:55
     **/
    Integer deleteReserveMe(HouseReserveQO qo);

    /**
     *@Author: Pipi
     *@Description: 租房用户确认完成看房
     *@param: qo:
     *@Return: java.lang.Integer
     *@Date: 2021/3/30 15:43
     **/
    Integer completeChecking(HouseReserveQO qo);

    /**
     * 通过预约信息id 查出这个房主的uid
     * @param id        预约信息id
     * @return          返回该预约信息房源的uid
     */
    String getUidByHouseReserveId(@Param("id") Long id);

    /**
     *@Author: Pipi
     *@Description: 定时完成看房
     *@param: :
     *@Return: java.lang.Integer
     *@Date: 2021/3/31 10:45
     **/
    Integer timingCompleteChecking();

    /**
     * 通过房屋id查询房屋是否存在
     * @param houseLeaseId      查询id
     * @return                  返回sql影响行数
     */
    @Select("select count(*) from t_house_lease where id = #{houseLeaseId} and deleted = 0")
    Integer existHouseLeaseId(@Param("houseLeaseId") Long houseLeaseId);

    /**
     * 通过房屋id查询房屋是否存在
     * @param houseLeaseId      查询id
     * @return                  返回sql影响行数
     */
    @Select("select count(*) from t_house_reserve where house_lease_id = #{houseLeaseId} and reserve_uid = #{reserveUid} and TO_DAYS(checking_time) = TO_DAYS(#{checkingTime}) and lease_delete_status = 0 and delete_landlord_status = 0")
    Integer existHouseReserve(@Param("houseLeaseId") Long houseLeaseId,
                              @Param("reserveUid") String reserveUid,
                              @Param("checkingTime") Date checkingTime
    );


    /**
     * 拒绝预约
     * 预约状态 0.已取消，1.预约中 2.预约成功 3.预约已拒绝
     * @param qo   请求参数
     * @return     返回影响行数
     */
    @Update("update t_house_reserve set reserve_status = 3,update_time = now() where reserve_status = 1 and id = #{id}")
    Integer rejectReserve(HouseReserveQO qo);


    /**
     *@Author: Pipi
     *@Description: 取消我预约的
     *@param: qo: 房屋预约参数对象
     *@Return: java.lang.Integer 影响行数
     *@Date: 2021/3/30 10:20
     **/
    Integer cancelMyReserveState(HouseReserveQO qo);

    /**
     *@Author: Pipi
     *@Description: 取消预约我的
     *@param: qo: 房屋预约参数对象
     *@Return: java.lang.Integer 影响行数
     *@Date: 2021/3/30 10:20
     **/
    Integer cancelReserveMeState(HouseReserveQO qo);


    /**
     * 从t_house_lease 表拿到用户的推送信息
     * @param id        预约信息id
     * @author YuLF
     * @since  2021/1/15 15:22
     * @return          返回用户推送id 房源标题
     */
    HouseReserveVO getPushInfo(Long id);


    /**
     * 根据uid拿用户nickname
     * @param reserveUid   用户uid
     * @return             返回用户nickname
     */
    @Select("select nickname from t_user where uid = #{uid} and deleted = 0")
    String selectNicknameById(@Param("uid") String reserveUid);

    /**
     * 插入预约信息
     * @param qo        请求参数
     * @return          返回影响行数
     */
    Integer insertReserve(HouseReserveEntity qo);


}
