package com.jsy.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.qo.lease.HouseReserveQO;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.lease.HouseLeaseVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

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
}
