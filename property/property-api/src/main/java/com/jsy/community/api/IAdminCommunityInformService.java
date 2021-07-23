package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;
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
     * @return    返回新增 insert 行数 > 0 的结果
     */
    Integer addPushInform(PushInformQO qo);

    /**
     *@Author: Pipi
     *@Description: 更新信息
     *@Param: qo:
     *@Return: java.lang.Boolean
     *@Date: 2021/4/20 16:38
     **/
    Boolean updatePushInform(OldPushInformQO qo);

    /**
     * 根据id删除社区推送通知消息
     * @param id            推送消息id
     * @return              返回删除是否成功
     */
    Boolean deletePushInform(Long id, String updateAdminId);

    /**
     *@Author: Pipi
     *@Description: (物业端)更新置顶状态
     *@Param: qo:
     *@Return: java.lang.Boolean
     *@Date: 2021/4/20 15:15
     **/
    Boolean updateTopState(OldPushInformQO qo);

    /**
     *@Author: Pipi
     *@Description: 更新发布状态
     *@Param: qo:
     *@Return: java.lang.Boolean
     *@Date: 2021/4/20 15:57
     **/
    Boolean updatePushState(OldPushInformQO qo);

    /**
     * 分页查询社区消息服务提供接口
     * @param qo                参数实体对象
     * @return                  返回查询分页结果
     */
    List<PushInformEntity> queryCommunityInform(BaseQO<OldPushInformQO> qo);

    /**
     *@Author: Pipi
     *@Description: (物业端)查询公告列表
     *@Param: qo:
     *@Return: java.util.List<com.jsy.community.entity.PushInformEntity>
     *@Date: 2021/4/20 13:53
     **/
    Page<PushInformEntity> queryInformList(BaseQO<OldPushInformQO> qo);

    /**
     *@Author: Pipi
     *@Description: (物业端)获取单条消息详情
     *@Param: id: 消息ID
     *@Return: com.jsy.community.entity.PushInformEntity
     *@Date: 2021/4/20 16:23
     **/
    PushInformEntity getDetail(Long id);

}
