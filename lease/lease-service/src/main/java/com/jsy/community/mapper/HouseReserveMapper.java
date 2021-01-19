package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseReserveQO;
import com.jsy.community.vo.lease.HouseReserveVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 房屋预约 Mapper 接口
 * @author YuLF
 * @since 2020-12-26
 */
public interface HouseReserveMapper extends BaseMapper<HouseReserveEntity> {



    /**
     * 我的预约信息
     * @author YuLF
     * @since  2020/12/28 10:22
     */
    List<HouseReserveVO> meReserveHouse(@Param("qo") BaseQO<HouseReserveQO> qo, @Param("uid") String uid);


    /**
     * 预约我的信息
     * @author YuLF
     * @since  2020/12/28 10:22
     */
    List<HouseReserveVO> reserveMeHouse(@Param("qo") BaseQO<HouseReserveQO> qo, @Param("uid") String uid);


    /**
     * 确认预约信息
     * @param qo          预约信息id参数对象
     * @return            返回确认是否成功修改
     */
    Integer confirm(HouseReserveQO qo);


    /**
     * 通过预约信息id 查出这个房主的uid
     * @param id        预约信息id
     * @return          返回该预约信息房源的uid
     */
    @Select("select l.uid from t_house_reserve as r left join t_house_lease as l on r.house_lease_id = l.id where r.deleted = 0 and r.deleted = 0 and r.id = #{id}")
    String getUidByHouseReserveId(@Param("id") Long id);

    /**
     * 通过房屋id查询房屋是否存在
     * @param houseLeaseId      查询id
     */
    @Select("select count(*) from t_house_lease where id = #{houseLeaseId}")
    Integer existHouseLeaseId(@Param("houseLeaseId") Long houseLeaseId);


    /**
     * 拒绝预约
     * 预约状态 0.已取消，1.预约中 2.预约成功 3.预约已拒绝
     * @param qo   请求参数
     */
    @Update("update t_house_reserve set reserve_status = 3 where reserve_status = 1 and id = #{id}")
    Integer rejectReserve(HouseReserveQO qo);


    /**
     * 取消预约
     * 预约状态 0.已取消，1.预约中 2.预约成功 3.预约已拒绝
     * @param qo   请求参数
     */
    @Update("update t_house_reserve set reserve_status = 0 where reserve_status = 1 or reserve_status = 2 and id = #{id}")
    Integer cancelReserveState(HouseReserveQO qo);

    Integer updateReserveState(HouseReserveQO qo);

    /**
     * 从t_house_lease 表拿到用户的推送信息
     * @author YuLF
     * @since  2021/1/15 15:22
     * @Param  id       房源id
     * @return          返回用户推送id 房源标题
     */
    @Select("select l.house_title,u.reg_id as pushId from t_house_reserve as r LEFT JOIN t_user as u on r.reserve_uid = u.uid join t_house_lease as l on r.house_lease_id = l.id where u.deleted = 0 and r.id = #{id} ")
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
