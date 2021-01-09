package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;

import java.util.List;

/**
 * 服务类
 * @author YuLF
 * @since 2020-11-16
 */
public interface IAdminCommunityInformService extends IService<PushInformEntity> {

    /**
     * 新增
     * @param qo  参数实体
     */
    Boolean addPushInform(PushInformQO qo);

    /**
     * 根据id删除社区推送通知消息
     * @param id            推送消息id
     * @return              返回删除是否成功
     */
    Boolean deletePushInform(Long id);


    /**
     * 分页查询社区消息服务提供接口
     * @param qo                参数实体对象
     * @return                  返回查询分页结果
     */
    List<PushInformEntity> queryCommunityInform(BaseQO<PushInformQO> qo);

}
