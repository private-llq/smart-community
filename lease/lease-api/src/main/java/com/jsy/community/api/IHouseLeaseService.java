package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.vo.lease.HouseLeaseSimpleVO;
import com.jsy.community.vo.lease.HouseLeaseVO;
import com.jsy.community.vo.HouseVo;

import java.util.List;

/**
 * 房屋租售接口提供类
 *
 * @author YuLF
 * @since 2020-12-11 09:21
 */
public interface IHouseLeaseService extends IService<HouseLeaseEntity> {


    /**
     * 【整租】整租新增出租房屋数据
     *
     * @param houseLeaseQo 请求参数接收对象
     * @return 返回新增是否成功
     */
    Boolean addWholeLeaseHouse(HouseLeaseQO houseLeaseQo);


    /**
     * 【单间】单间新增房源
     *
     * @param houseLeaseQo 请求参数
     * @return 返回新增sql影响行数 > 0
     */
    boolean addSingleLeaseHouse(HouseLeaseQO houseLeaseQo);


    /**
     * 【合租】新增房源
     *
     * @param houseLeaseQo 请求参数
     * @return 返回新增sql影响行数 > 0
     */
    boolean addCombineLeaseHouse(HouseLeaseQO houseLeaseQo);


    /**
     * 【整租】按参数对象属性更新房屋出租数据
     *
     * @param houseLeaseQo 参数对象
     * @return 返回更新影响行数
     */
    Boolean updateWholeLease(HouseLeaseQO houseLeaseQo);

    /**
     * 【单间】按参数对象属性更新房屋出租数据
     *
     * @param qo 参数对象
     * @return 返回更新影响行数
     */
    Boolean updateSingleRoom(HouseLeaseQO qo);

    /**
     * 【合租】按参数对象属性更新房屋出租数据
     *
     * @param qo 参数对象
     * @return 返回更新影响行数
     */
    Boolean updateCombineLease(HouseLeaseQO qo);

    /**
     * 根据用户id和 rowGuid 删除出租房源数据
     *
     * @param id     业务主键
     * @param userId 用户id
     * @return 返回删除成功
     */
    boolean delLeaseHouse(Long id, String userId);

    /**
     * 根据参数对象条件查询 出租房屋数据
     *
     * @param houseLeaseQo 查询参数对象
     * @return 返回数据集合
     */
    List<HouseLeaseVO> queryHouseLeaseByList(BaseQO<HouseLeaseQO> houseLeaseQo);


    /**
     * 根据id查询房屋详情单条数据
     *
     * @param houseId 房屋id
     * @param uid     用户id
     * @return 返回这条数据的详情
     */
    HouseLeaseVO queryHouseLeaseOne(Long houseId, String uid);


    /**
     * 按用户id和 社区id查询 房主在当前社区出租的房源
     *
     * @param qo 包含用户id
     * @return 返回业主拥有的房产
     */
    List<HouseLeaseVO> ownerLeaseHouse(BaseQO<HouseLeaseQO> qo);

    /**
     * 通过用户id社区id房屋id验证用户是否存在此处房产
     *
     * @param userId           用户id
     * @param houseCommunityId 社区id
     * @param houseId          房屋id
     * @param operation        操作符 更新操作 不需要检查房屋最大数量
     */
    void checkHouse(String userId, Long houseCommunityId, Long houseId, Operation operation);


    /**
     * 根据用户id 和社区id 查询用户在这个社区的可发布房源
     *
     * @param userId      用户id
     * @param communityId 社区id
     * @return 返回List数据 如果有多条
     */
    List<HouseVo> ownerHouse(String userId, Long communityId);

    /**
     * 按小区名或房屋出租标题或房屋地址模糊搜索匹配接口
     *
     * @param qo 请求参数   包含了 uid 、searchText
     * @return 返回搜索到的列表
     */
    List<HouseLeaseVO> searchLeaseHouse(BaseQO<HouseLeaseQO> qo);

    /**
     * 验证houseId是否已经发布
     *
     * @param houseId 房屋id
     * @return 返回是否发布
     */
    Boolean alreadyPublish(Long houseId);

    /**
     * 按用户id获取所有小区名称
     *
     * @param userId 用户id
     * @param cityId 城市id
     * @return 返回小区名称
     */
    List<CommunityEntity> allCommunity(Long cityId, String userId);

    /**
     * @author: Pipi
     * @description: 按用户id获取所有小区名称
     * @param userId:
     * @return: java.util.List<com.jsy.community.entity.CommunityEntity>
     * @date: 2021/10/13 11:36
     **/
    List<CommunityEntity> allCommunity(String userId);

    /**
     * 按id查询房屋详情数据
     *
     * @param houseId 房屋id
     * @param uid     用户id
     * @return 返回这条数据的详情
     */
    HouseLeaseVO editDetails(Long houseId, String uid);

    /**
     * @Author: Pipi
     * @Description: 查询房屋出租数据单条简略详情
     * @param: houseId: 出租房屋主键
     * @Return: com.jsy.community.vo.lease.HouseLeaseSimpleVO
     * @Date: 2021/3/27 16:25
     **/
    HouseLeaseSimpleVO queryHouseLeaseSimpleDetail(Long houseId);

}
