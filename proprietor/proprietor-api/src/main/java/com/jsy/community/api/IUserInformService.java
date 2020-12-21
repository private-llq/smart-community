package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.entity.sys.SysInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.vo.CommunityVO;
import com.jsy.community.vo.sys.SysInformVO;

import java.util.List;

/**
 * 通知消息接口
 *
 * @author ling
 * @since 2020-11-11 15:47
 */
public interface IUserInformService extends IService<UserInformEntity> {


    /**
     * @author YuLF
     * @since  2020/12/14 18:07
     * @param userId        用户ID
     * @param page          当前页
     * @param size          每次显示的条数
     * @return              返回总消息未读列表数据
     */
    List<CommunityVO> totalCommunityInformList(String userId, Long page, Long size);


   /**
    *  用户系统消息详情查看
    * @author YuLF
    * @since  2020/12/21 14:55
    */
    SysInformEntity totalSysInformList(Long informId, String userId);

    /**
     * 验证系统消息是否存在
     * @author YuLF
     * @since  2020/12/21 16:58
     * @Param  informId     消息id
     */
    boolean sysInformExist(Long informId);

    /**
     * 用户系统消息主页列表查看
     * @author YuLF
     * @since  2020/12/21 17:32
     * @Param  baseQO   分页查询对象
     * @Param  userId   用户ID
     * @return          返回已读未读的系统消息列表
     */
    List<SysInformVO> userSysInformList(BaseQO<SysInformVO> baseQO, String userId);
}
