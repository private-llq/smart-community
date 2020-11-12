package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.VisitorQO;

/**
 * <p>
 * 来访人员 服务类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-11
 */
public interface IVisitorService extends IService<VisitorEntity> {
    
    /**
    * @Description: 访客登记 新增
     * @Param: [visitorEntity]
     * @Return: java.lang.Long(自增ID)
     * @Author: chq45799974
     * @Date: 2020/11/12
    **/
    Long addVisitor(VisitorEntity visitorEntity);
    
    /**
    * @Description: 分页查询
     * @Param: [BaseQO<VisitorQO>]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.VisitorEntity>
     * @Author: chq459799974
     * @Date: 2020/11/11
    **/
    Page<VisitorEntity> queryByPage(BaseQO<VisitorQO> baseQO);
    
}
