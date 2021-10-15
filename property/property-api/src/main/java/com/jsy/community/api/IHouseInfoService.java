package com.jsy.community.api;

import com.jsy.community.entity.HouseInfoEntity;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 房间推送消息
 * @author: Hu
 * @create: 2021-10-13 16:31
 **/
public interface IHouseInfoService {
    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/10/13 16:38
     * @Param:
     * @return:
     */
    void saveOne(HouseInfoEntity houseInfoEntity);

    /**
     * @Description: 查询注册用户所有需要推送的消息
     * @author: Hu
     * @since: 2021/10/13 17:35
     * @Param:
     * @return:
     */
    List<HouseInfoEntity> selectList(String mobile);

    /**
     * @Description: 查询房屋推送消息详情
     * @author: Hu
     * @since: 2021/10/14 14:02
     * @Param:
     * @return:
     */
    HouseInfoEntity getByPushInfo(Long id);

    /**
     * @Description: 用户确定关系后添加到成员表
     * @author: Hu
     * @since: 2021/10/14 14:05
     * @Param:
     * @return:
     */
    void relationSave(HouseInfoEntity houseInfoEntity);
}
