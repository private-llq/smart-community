package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;

import java.util.List;

/**
 *  服务类
 * @author YuLF
 * @since 2020-11-16
 */
public interface ICommunityInformService extends IService<PushInformEntity> {

    /**
     * 分页查询社区消息服务提供接口
     * @param qo                参数实体对象
     * @return                  返回查询分页结果
     */
    List<PushInformEntity> queryCommunityInform(BaseQO<PushInformQO> qo);






    /**
     * 社区主页 当前轮播消息 查询最近的  initialInformCount 条数量
     * @param initialInformCount     初始轮播消息条数
     * @param communityId            社区id
     * @return                       返回消息列表
     */
    List<PushInformEntity> rotationCommunityInform(Integer initialInformCount, Long communityId);


    /**
     * 社区推送消息详情查看
     * @param acctId        推送账号id、可能是社区ID 可能是系统消息ID 可能是其他第三方推送号ID
     * @param informId      推送消息ID
     * @param userId        用户ID
     * @return              返回这条推送消息的详情
     */
    PushInformEntity detailsCommunityInform(Long acctId, Long informId ,String userId);

    /**
     * 用户消息列表 左滑动 删除推送号(屏蔽)
     * @param acctId    推送号ID
     * @param userId    用户id
     */
    void delPushInformAcct(Long acctId, String userId);

    /**
     * 通过用户传上来的 推送账号id 标记用户已读
     * @param acctIds       推送账号id列表
     * @param uid           用户id
     */
    void clearUnreadInform(List<Long> acctIds, String uid);
}
