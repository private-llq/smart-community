package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.vo.CommunityVO;

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


}
