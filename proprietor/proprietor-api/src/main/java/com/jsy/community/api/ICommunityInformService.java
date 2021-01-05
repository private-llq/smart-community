package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CommunityInformQO;

import java.util.List;

/**
 *  服务类
 * @author YuLF
 * @since 2020-11-16
 */
public interface ICommunityInformService extends IService<CommunityInformEntity> {

    /**
     * 分页查询社区消息服务提供接口
     * @param communityEntity  参数实体对象
     * @return                  返回查询分页结果
     */
    List<CommunityInformEntity> queryCommunityInform(BaseQO<CommunityInformEntity> communityEntity);

    /**
     * 添加社区消息
     * @param communityInformEntity  参数实体
     */
    void addCommunityInform(CommunityInformEntity communityInformEntity);

    /**
     * 更新社区消息
     * @param communityInformQO     参数实体
     * @return                      返回更新是否成功
     */
    Boolean updateCommunityInform(CommunityInformQO communityInformQO);

    /**
     * 根据id逻辑删除社区消息
     * @param id            社区消息id
     * @return              返回删除是否成功
     */
    Boolean delCommunityInform(Long id);

    /**
     * 社区主页 当前轮播消息 查询最近的  initialInformCount 条数量
     * @param initialInformCount     初始轮播消息条数
     * @return                       返回消息列表
     */
    List<CommunityInformEntity> rotationCommunityInform(Integer initialInformCount, Long communityId);


    /**
     *  用户社区消息详情查看
     */
    CommunityInformEntity detailsCommunityInform(Long communityId, Long informId ,String userId);


    /**
     * 验证社区消息是否存在
     * @author YuLF
     * @since  2020/12/21 17:02
     */
    boolean informExist(Long communityId, Long informId);
}
