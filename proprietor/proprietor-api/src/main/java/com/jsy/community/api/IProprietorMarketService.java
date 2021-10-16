package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.ProprietorMarketQO;
import com.jsy.community.vo.proprietor.ProprietorMarketVO;

import java.util.Map;

public interface IProprietorMarketService extends IService<ProprietorMarketEntity>{
    /**
     * @Description: 发布商品信息
     * @Param: [marketQO, userId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/20-17:34
     **/
    boolean addMarket(ProprietorMarketQO marketQO, String userId);

    /**
     * @Description: 修改发布商品
     * @Param: [marketQO, userId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-11:43
     **/
    boolean updateMarket(ProprietorMarketQO marketQO, String userId);

    /**
     * @Description: 删除发布商品
     * @Param: [id]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-11:53
     **/
    boolean deleteMarket(Long id);

    /**
     * @Description: 修改商品上下架
     * @Param: [id, state]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-15:57
     **/
    boolean updateState(Long id, Integer state);

    /**
     * @Description:查询当前用户已发布商品
     * @Param: [baseQO, userId]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Tian
     * @Date: 2021/8/21-16:26
     **/
    Map<String, Object> selectMarketPage(BaseQO<ProprietorMarketEntity> baseQO, String userId);
    /**
     * @Description: 查询所有用户发布的商品
     * @Param: [baseQO]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Tian
     * @Date: 2021/8/23-10:44
     **/
    Map<String, Object> selectMarketAllPage(BaseQO<ProprietorMarketQO> baseQO);

    /**
     * @Description: 查找单条详细信息
     * @Param: [id]
     * @Return: com.jsy.community.vo.proprietor.ProprietorMarketVO
     * @Author: Tian
     * @Date: 2021/8/23-14:06
     **/
    ProprietorMarketEntity findOne(Long id);
    /**
     * @Description: 热门商品
     * @Param: [baseQO]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Tian
     * @Date: 2021/8/26-14:30
     **/
    Map<String, Object> selectMarketLikePage(BaseQO<ProprietorMarketQO> baseQO);

}
