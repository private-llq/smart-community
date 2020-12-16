package com.jsy.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.vo.HouseLeaseVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房屋租售 Mapper 接口
 * @author YuLF
 * @since 2020-12-10
 */
public interface HouseLeaseMapper extends BaseMapper<HouseLeaseEntity> {





    /**
     * 插入房源数据
     * @param houseLeaseQO      参数对象
     * @return                  返回影响行数
     */
    int insertHouseLease(HouseLeaseQO houseLeaseQO);


    /**
     * 向中间表插入房屋优势标签ID数组
     * @param houseLeaseQO        参数
     */
    void insertHouseAdvantage(@Param("houseLeaseQO") HouseLeaseQO houseLeaseQO);

    /**
     * 插入房屋图片至中间库
     * @param houseLeaseQO  参数对象
     */
    void insertHouseImages(@Param("houseLeaseQO") HouseLeaseQO houseLeaseQO);

    /**
     * 删除用户中间表关联的相关信息
     * @param id             t_house_lease数据唯一标识
     * @return               影响行数
     */
    @Delete("delete from t_house_leasesale_const where hid = #{id}")
    int delUserMiddleInfo(@Param("id") Long id);


    /**
     * 根据 rowGuid 删除t_house_lease 表中的数据
     * @param guid_id   数据唯一标识业务主键 暂定
     * @return          返回影响行数
     */
    int delHouseLeaseInfo(@Param("id") Long guid_id);


    /**
     * 根据参数对象条件查询 出租房屋数据
     * @param houseLeaseQO      查询参数对象
     * @return                  返回数据集合
     */
    List<HouseLeaseVO> queryHouseLeaseByList(BaseQO<HouseLeaseQO> houseLeaseQO);


    /**
     * 查询房屋常量 通过id列表
     * @param houseAvatarsIds       id列表
     * @return                      返回常量id集合
     */
    List<Integer> queryHouseLeaseConstByIds(List<String> houseAvatarsIds);
}
