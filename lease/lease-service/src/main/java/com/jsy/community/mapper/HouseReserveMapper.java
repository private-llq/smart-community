package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseReserveQO;
import com.jsy.community.vo.lease.HouseReserveVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房屋预约 Mapper 接口
 * @author YuLF
 * @since 2020-12-26
 */
public interface HouseReserveMapper extends BaseMapper<HouseReserveEntity> {


    /**
     * 取消预约信息
     * @param qo   取消预约 接收 参数 对象
     * @return      返回取消是否成功修改
     */
    Integer cancel(HouseReserveQO qo);


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
    Integer confirm(@Param("qo") HouseReserveQO qo);


    /**
     * 通过预约信息id 查出这个房主的uid
     * @param id        预约信息id
     * @return          返回该预约信息房源的uid
     */
    @Select("select l.uid from t_house_reserve as r left join t_house_lease as l on r.house_lease_id = l.id where r.id = #{id}")
    String getUidByHouseReserveId(@Param("id") Long id);

    /**
     * 通过房屋id查询房屋是否存在
     * @param houseLeaseId      查询id
     */
    @Select("select count(*) from t_house_lease where id = #{houseLeaseId}")
    Integer existHouseLeaseId(@Param("houseLeaseId") Long houseLeaseId);
}
