package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.ProprietorVO;

import java.util.List;

/**
 * <p>
 * 业主 服务类
 * </p>
 *
 * @author YuLF
 * @since 2020-11-25
 */
public interface IProprietorService extends IService<UserEntity> {

    /**
     * 解绑这个房屋Id关联的业主
     * @param hid    房屋id
     * @param cid    社区ID
     * @return       返回删除影响行数
     */
    Boolean unbindHouse(Long hid, Long cid);

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
    List<ProprietorVO> query(BaseQO<ProprietorQO> query);

    /**
     * 录入业主信息业主房屋绑定信息至数据库
     * @param userEntityList    用户信息
     * @param communityId       社区id
     */
    void saveUserBatch(List<UserEntity> userEntityList, Long communityId);

    /**
     * 通过当前社区id查出的当前社区所有已登记的房屋
     * @param communityId   社区id
     * @author YuLF
     * @since  2020/12/25 11:10
     * @return          返回当前社区已经被登记的所有房屋信息
     */
    List<HouseVo> queryHouseByCommunityId(long communityId);


    /**
     * [excel] 导入业主家属信息
     * @param userEntityList    用户信息实体
     * @param communityId       社区id
     * @author YuLF
     * @since  2020/12/25 14:47
     * @return              返回影响行数
     */
    Integer saveUserMemberBatch(List<UserEntity> userEntityList, long communityId);


    /**
     * [物业]添加业主信息
     * @param qo            请求参数
     * @param adminUid      物业操作用户uid
     */
    void addUser(ProprietorQO qo, String adminUid);
}
