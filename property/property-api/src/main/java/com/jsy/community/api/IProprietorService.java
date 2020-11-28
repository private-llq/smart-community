package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.vo.CommonResult;

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
     * @param userEntity   更新业主信息参数
     * @return              返回是否更新成功
     */
    Boolean update(UserEntity userEntity);
}
