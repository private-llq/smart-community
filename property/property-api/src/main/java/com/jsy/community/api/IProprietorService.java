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
     * 根据ID删除 业主的信息、关联房屋信息、关联车辆信息、关联房屋认证信息
     * @param id    业主ID
     */
    void del(Long id);

    /**
     * 通过传入的参数更新业主信息
     * @param proprietorQO   更新业主信息参数
     * @return              返回是否更新成功
     */
    Boolean update(ProprietorQO proprietorQO);

    /**
     * 通过分页参数查询 业主信息
     * @param query     查询参数
     * @return          返回查询的业主信息
     */
    List<ProprietorVO> query(BaseQO<ProprietorQO> query);

    /**
     * 录入业主信息业主房屋绑定信息至数据库
     * @author YuLF
     * @since  2020/12/23 17:35
     * @Param  userEntityList           业主信息参数列表
     * @Param  communityId              社区id
     */
    void saveUserBatch(List<UserEntity> userEntityList, Long communityId);

    /**
     * 通过当前社区id查出的当前社区所有已登记的房屋
     * @author YuLF
     * @since  2020/12/25 11:10
     * @Param
     * @return          返回当前社区已经被登记的所有房屋信息
     */
    List<HouseVo> queryHouseByCommunityId(long communityId);


    /**
     * [excel] 导入业主家属信息
     * @author YuLF
     * @since  2020/12/25 14:47
     * @Param    userEntityList     用户家属信息
     */
    Integer saveUserMemberBatch(List<UserEntity> userEntityList, long communityId);
}
