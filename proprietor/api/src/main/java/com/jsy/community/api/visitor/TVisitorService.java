package com.jsy.community.api.visitor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.visitor.TVisitorEntity;
import com.jsy.community.qo.visitor.TVisitorQO;

/**
 * <p>
 * 来访人员 服务类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-11
 */
public interface TVisitorService extends IService<TVisitorEntity> {
    /**
    * @Description: 分页查询
     * @Param: [tVisitorQO]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.visitor.TVisitorEntity>
     * @Author: chq459799974
     * @Date: 2020/11/11
    **/
    Page<TVisitorEntity> queryByPage(TVisitorQO tVisitorQO);
}
