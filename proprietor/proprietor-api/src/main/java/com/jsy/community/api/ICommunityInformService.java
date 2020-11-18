package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.qo.BaseQO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author YuLF
 * @since 2020-11-16
 */
public interface ICommunityInformService extends IService<CommunityInformEntity> {

    /**
     * 分页查询社区消息服务提供接口
     * @param communityEntity  参数实体对象
     * @return                  返回查询分页结果
     */
    Page<CommunityInformEntity> queryCommunityInform(BaseQO<CommunityInformEntity> communityEntity);

    /**
     * 添加社区消息
     * @param communityInformEntity  参数实体
     * @return                       返回添加是否成功
     */
    Boolean addCommunityInform(CommunityInformEntity communityInformEntity);

    /**
     * 更新社区消息
     * @param communityEntity       参数实体
     * @return                      返回更新是否成功
     */
    Boolean updateCommunityInform(CommunityInformEntity communityEntity);

    /**
     * 根据id逻辑删除社区消息
     * @param id            社区消息id
     * @return              返回删除是否成功
     */
    Boolean delCommunityInform(Long id);
}
