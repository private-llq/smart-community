package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.qo.proprietor.ComplainQO;
import com.jsy.community.vo.proprietor.ComplainVO;

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
     * @Description: 新投诉建议接口
     * @Param: [complainQO]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/20-11:45
     **/
    boolean appendComplain(ComplainQO complainQO);

    /**
     * @Description: 新查询投诉建议
     * @Param: [userId]
     * @Return: java.util.List<com.jsy.community.vo.proprietor.ComplainVO>
     * @Author: Tian
     * @Date: 2021/8/20-11:44
     **/
    List<ComplainVO> selectComplain(String userId);
}
