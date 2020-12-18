package com.jsy.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.vo.HouseLeaseVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;
import java.util.Map;

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
     * 插入房屋图片至中间库
     * @param houseLeaseQO  参数对象
     */
    void insertHouseImages(@Param("houseLeaseQO") HouseLeaseQO houseLeaseQO);

    /**
     * 删除用户中间表关联的相关信息
     * @param id             t_house_lease数据唯一标识
     * @return               影响行数
     */
    @Delete("delete from t_house_image where hid = #{id}")
    int delUserMiddleInfo(@Param("id") Long id);


    /**
     * 根据 rowGuid 删除t_house_lease 表中的数据
     * @param guid_id   数据唯一标识业务主键 暂定
     * @return          返回影响行数
     */
    int delHouseLeaseInfo(@Param("id") Long guid_id);


    /**
     * 根据参数对象条件查询 出租房屋数据
     * @param baseQO            查询参数对象
     * @return                  返回数据集合
     */
    List<HouseLeaseVO> queryHouseLeaseByList(BaseQO<HouseLeaseQO> baseQO);



    /**
     * 通过 常量id集合 查询 常量名称
     * @param constIdList       常量id集合
     * @param type              表示当前常量id集合 为什么类型
     * @return                  返回id，name
     */
    List<Map<String, Object>> queryHouseConstIdName(List<Long> constIdList, Long type);

    /**
     * 通过imgId 和 数据标识行ID 查出 该数据最早插入的一张图片
     * @param houseImageId          图片Id
     * @param rowGuid               房屋数据行唯一标识
     * @return                      返回图片
     */
    @Select("select img_url from t_house_image where hid = #{hid} and field_id = #{image_id} ORDER BY create_time LIMIT 1")
    List<String> queryHouseImgById(@Param("image_id") String houseImageId, @Param("hid") Long rowGuid);


    /**
     * 通过常量Id 查询出常量名称
     * @param ConstId       常量ID
     * @return              返回常量名称
     */
    @Select("select house_const_name from t_house_const where house_const_code = #{constId} and house_const_type = #{type}")
    String queryHouseModeByConstId(@Param("constId") Long ConstId, @Param("type")Long type);

    /**
     * 用家具ID 查出所有 家具名称
     * @param furnitureId       家具ID列表
     * @param type              常量类型
     * @return                  返回名称集合
     */
    List<String> queryHouseConstNameByFurnitureId(List<Long> furnitureId, long type);


    /**
     * 根据id查询房屋详情单条数据
     * @param houseId       房屋id
     * @return              返回这条数据的详情
     */
    @Select("select " +
            "house_contact,house_image_id,house_price,house_unit,house_leasedeposit_id,house_leasemode_id,house_title,house_type_id,house_square_meter,house_floor,house_direction,house_advantage_id,house_introduce,house_furniture_id,house_lat,house_lon " +
            "from t_house_lease where id = #{houseId}")
    HouseLeaseVO queryHouseLeaseOne(@Param("houseId") Long houseId);


    /**
     * 按图片id查出所有图片
     * @param houseImageId      图片id
     * @return                  返回List数据
     */
    @Select("select img_url from t_house_image where field_id = #{houseImageId}")
    List<String> queryHouseAllImgById(@Param("houseImageId") String houseImageId);
}
