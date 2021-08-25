package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketLabelEntity;

import java.util.List;

public interface IProprietorMarketLabelService extends IService<ProprietorMarketLabelEntity> {
    /**
     * @Description: 新增社区集市商品标签
     * @Param: [labelEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:40
     **/
    boolean addMarketLabel(ProprietorMarketLabelEntity labelEntity);

    /**
     * @Description: 修改社区集市商品标签
     * @Param: [labelEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    boolean updateMarketLabel(ProprietorMarketLabelEntity labelEntity);

    /**
     * @Description: 删除社区集市商品标签
     * @Param: [labelEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    boolean deleteMarketLabel(Long id);

    /**
     * @Description: 查询社区集市商品标签
     * @Param: [labelEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    List<ProprietorMarketLabelEntity> selectMarketLabel();
}
