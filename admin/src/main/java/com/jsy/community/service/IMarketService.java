package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyMarketQO;
import com.jsy.community.vo.proprietor.ProprietorMarketVO;

import java.util.Map;

public interface IMarketService extends IService<ProprietorMarketEntity>{
    /**
     * @Description: 删除发布商品
     * @Param: [id]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/11/1-10:18
     **/
    boolean deleteBlacklist(Long id);

    /**
     * @Description: 查询所有用户发布的商品
     * @Param: [baseQO]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: DKS
     * @Date: 2021/11/1-10:44
     **/
    Map<String, Object> selectMarketAllPage(BaseQO<PropertyMarketQO> baseQO);

    /**
     * @Description: 查找单条详细信息
     * @Param: [id]
     * @Return: com.jsy.community.vo.proprietor.ProprietorMarketVO
     * @Author: DKS
     * @Date: 2021/11/1-10:36
     **/
    ProprietorMarketVO findOne(Long id);

    /**
     * @Description: 修改商品是否屏蔽
     * @Param: [id, shield]
     * @Return: boolean
     * @author: DKS
     * @since: 2021/11/1 10:56
     **/
    boolean updateShield(Long id, Integer shield);
    
    /**
     * @Description: 查询黑名单商品
     * @Param: [baseQO]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: DKS
     * @Date: 2021/11/1-11:00
     **/
    Map<String, Object> selectMarketBlacklist(BaseQO baseQO);
}
