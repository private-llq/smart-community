package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
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
}
