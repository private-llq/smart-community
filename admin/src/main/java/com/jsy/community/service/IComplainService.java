package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.ComplainQO;
import com.jsy.community.utils.PageInfo;

/**
 * @program: com.jsy.community
 * @description:  物业意见反馈
 * @author: DKS
 * @create: 2021-10-27 15:58
 **/
public interface IComplainService extends IService<ComplainEntity> {
    /**
     * @Description: 意见反馈条件查询
     * @Param: []
     * @Return: java.util.List<com.jsy.community.entity.ComplainEntity>
     * @Author: DKS
     * @Date: 2021/10/27
     **/
    PageInfo<ComplainEntity> queryComplain(BaseQO<ComplainQO> baseQO);
    
    /**
     * @Description: 意见反馈详情查询
     * @author: DKS
     * @since: 2021/10/27 16:00
     * @Param: java.util.Long
     * @return: com.jsy.community.entity.ComplainEntity
     */
    ComplainEntity getDetailComplain(Long id);
    
    /**
     * @Description: 意见反馈删除
     * @author: DKS
     * @since: 2021/10/27 16:10
     * @Param: id
     * @return: boolean
     */
    boolean deleteComplain(Long id);
}
