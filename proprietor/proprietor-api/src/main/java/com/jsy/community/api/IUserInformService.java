package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.vo.InformListVO;

import java.util.List;

/**
 * 通知消息接口
 *
 * @author ling
 * @since 2020-11-11 15:47
 */
public interface IUserInformService extends IService<UserInformEntity> {


    /**
     * 查询用户总消息列表
     * @author YuLF
     * @since  2020/12/14 18:07
     * @param userId        用户ID
     * @return              返回总消息未读列表数据
     */
    List<InformListVO> totalCommunityInformList(String userId);

    /**
     * 定期清理 推送消息内容
     * @param beforeTime    为当前时间 - 后台配置的过期清理天数 得到的时间字符串
     * @return              返回影响行数
     */
    Integer regularCleaning(String beforeTime);
}
