package com.jsy.lease.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.vo.lease.HouseLeaseVO;
import com.jsy.community.vo.HouseVo;

import java.util.List;

/**
 * 房屋租售接口提供类
 * @author YuLF
 * @since 2020-12-11 09:21
 */
public interface IHouseLeaseService extends IService<HouseLeaseEntity> {


    /**
     * 新增出租房屋数据
     * @param houseLeaseQO   请求参数接收对象
     * @return               返回新增是否成功
     */
    Boolean addLeaseSaleHouse(HouseLeaseQO houseLeaseQO);



    /**
     * 根据用户id和 rowGuid 删除出租房源数据
     * @param id            业务主键
     * @param userId        用户id
     */
    boolean delLeaseHouse(Long id, String userId);

    /**
     * 根据参数对象条件查询 出租房屋数据
     * @param houseLeaseQO      查询参数对象
     * @return                  返回数据集合
     */
    List<HouseLeaseVO> queryHouseLeaseByList(BaseQO<HouseLeaseQO> houseLeaseQO);


    /**
     * 根据id查询房屋详情单条数据
     * @param houseId       房屋id
     * @return              返回这条数据的详情
     */
    HouseLeaseVO queryHouseLeaseOne(Long houseId);

    /**
     * 按参数对象属性更新房屋出租数据
     * @param houseLeaseQO          参数对象
     * @return                      返回更新影响行数
     */
    Boolean updateHouseLease(HouseLeaseQO houseLeaseQO);

    /**
     * 按用户id和 社区id查询 房主在当前社区出租的房源
     * @param userId            用户id
     * @param communityId       社区id
     * @return                  返回业主拥有的房产
     */
    List<HouseLeaseVO> ownerLeaseHouse(String userId, Long communityId);

    /**
     * 通过用户id社区id房屋id验证用户是否存在此处房产
     * @param userId                用户id
     * @param houseCommunityId      社区id
     * @param houseId               房屋id
     * @return                      返回是否存在结果
     */
    boolean isExistUserHouse(String userId, Integer houseCommunityId, Integer houseId);


    /**
     * 根据用户id 和社区id 查询用户在这个社区的可发布房源
     * @param userId            用户id
     * @param communityId       社区id
     * @return                  返回List数据 如果有多条
     */
    List<HouseVo> ownerHouse(String userId, Long communityId);

    /**
     * 按小区名或房屋出租标题或房屋地址模糊搜索匹配接口
     * @param text          文本
     * @return              返回搜索到的列表
     */
    List<HouseLeaseVO> searchLeaseHouse(String text);
}
