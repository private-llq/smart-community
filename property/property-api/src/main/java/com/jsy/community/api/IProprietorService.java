package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.vo.HouseTypeVo;
import com.jsy.community.vo.property.ProprietorVO;

import java.util.List;

/**
 * <p>
 * 业主 服务类
 * </p>
 *
 * @author YuLF
 * @since 2020-11-25
 */
public interface IProprietorService extends IService<ProprietorEntity> {

    /**
     * 解绑这个房屋Id关联的业主
     * @param id     业主信息数据id
     * @return       返回删除影响行数
     */
    Boolean unbindHouse(Long id);

    /**
     * 通过传入的参数更新业主信息
     * @param proprietorQo   更新业主信息参数
     * @param adminUid       管理员Uid
     * @return              返回是否更新成功
     */
    Boolean update(ProprietorQO proprietorQo, String adminUid);

    /**
     * 通过分页参数查询 业主信息
     * @param query     查询参数
     * @return          返回查询的业主信息
     */
    Page<ProprietorVO> query(BaseQO<ProprietorQO> query);

    /**
     * 录入业主信息业主房屋绑定信息至数据库
     * @param userEntityList    用户信息
     * @param communityId       社区id
     * @return                  返回影响行数
     */
    Integer saveUserBatch(List<ProprietorEntity> userEntityList, Long communityId);

//    /**
//     * 通过当前社区id查出的当前社区所有已登记的房屋
//     * @param communityId   社区id
//     * @author YuLF
//     * @since  2020/12/25 11:10
//     * @return          返回当前社区已经被登记的所有房屋信息
//     */
//    List<HouseVo> queryHouseByCommunityId(long communityId);


//    /**
//     * [excel] 导入业主家属信息
//     * @param userEntityList    用户信息实体
//     * @param communityId       社区id
//     * @author YuLF
//     * @since  2020/12/25 14:47
//     * @return              返回影响行数
//     */
//    Integer saveUserMemberBatch(List<UserEntity> userEntityList, long communityId);


    /**
     * [物业]添加业主信息
     * @param qo            请求参数
     * @param adminUid      物业操作用户uid
     */
    void addUser(ProprietorQO qo, String adminUid);


    /**
     * 通过管理员uid 获取管理员真实名称
     * @param adminUid      管理员uid
     * @return               真实名称
     */
    String getAdminRealName(String adminUid);

    /**
     * @author: Pipi
     * @description: 查询未绑定房屋列表 
     * @param: baseQO:  
     * @return: java.util.List<com.jsy.community.vo.HouseTypeVo>
     * @date: 2021/6/12 14:39
     **/
    List<HouseTypeVo> getUnboundHouseList(BaseQO<RelationListQO> baseQO);
    
    /**
     * @Description: 根据手机号查询绑定房屋的id
     * @Param: [mobile]
     * @Return:
     * @Author: DKS
     * @Date: 2021/08/16
     **/
    List<Long> queryBindHouseByMobile(String mobile, Long communityId);
}
