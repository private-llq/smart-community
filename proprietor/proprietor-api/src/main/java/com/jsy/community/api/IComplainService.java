package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.qo.proprietor.PropertyComplainQO;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 11:12
 **/
public interface IComplainService extends IService<ComplainEntity> {
    /**
     * @Description: 查询用户所有的投诉建议
     * @author: Hu
     * @since: 2020/12/23 11:30
     * @Param:
     * @return:
     */
    List<ComplainEntity> selectUserIdComplain(String userId);

    /**
     * @Description: 用户投诉接口
     * @author: Hu
     * @since: 2020/12/23 11:30
     * @Param:
     * @return:
     */
    void addComplain(ComplainEntity complainEntity);

    /**
     * @Description: 物业投诉接口
     * @author: Hu
     * @since: 2021/3/17 14:41
     * @Param:
     * @return:
     */
    void propertyComplain(PropertyComplainQO propertyComplainQO);
}
