package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketLabelEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyMarketQO;
import com.jsy.community.service.IMarketService;
import com.jsy.community.vo.proprietor.ProprietorMarketVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarketServiceImpl extends ServiceImpl<MarketMapper, ProprietorMarketEntity> implements IMarketService {
    //上下架常量
    private final static  Integer STATE_ZERO=0;
    private final static  Integer STATE_ONE=1;

   @Resource
   private MarketMapper marketMapper;

   @Resource
   private MarketLabelMapper labelMapper;

   @Resource
   private MarketCategoryMapper categoryMapper;
    
    @Resource
    private CommunityMapper communityMapper;
    
    @Resource
    private UserMapper userMapper;

    /**
     * @Description: 删除发布的商品
     * @Param: [id]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/11/1-10:18
     **/
    @Override
    public boolean deleteBlacklist(Long id) {
       marketMapper.deleteBlacklist(id);
        return true;
    }
    
    /**
     * @Description: 查询所有用户已发布的商品
     * @Param: [baseQO]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: DKS
     * @Date: 2021/11/1-10:44
     **/
    @Override
    public Map<String, Object> selectMarketAllPage(BaseQO<PropertyMarketQO> baseQO) {
        if (baseQO.getSize() == 0 || baseQO.getSize() == null){
            baseQO.setSize(10L);
        }
        Long page1 = baseQO.getPage() ;
        if (page1 == 0){
            page1++;
        }
        page1 =(baseQO.getPage()-1)*baseQO.getSize();
        PropertyMarketQO query = baseQO.getQuery();
        ArrayList<ProprietorMarketVO> arrayList = new ArrayList<>();

        List<ProprietorMarketEntity> list =  marketMapper.selectMarketAllPage(query,page1,baseQO.getSize());
        for (ProprietorMarketEntity li : list){
            ProprietorMarketVO marketVO = new ProprietorMarketVO();
            BeanUtils.copyProperties(li,marketVO);
            // 补充标价
            marketVO.setPriceName(li.getNegotiable() == 1 ? "面议" : String.valueOf(li.getPrice()));
            // 补充状态名称
            marketVO.setStateName(li.getState() == 1 ? "已上架" : "已下架");
            arrayList.add(marketVO);
        }
        Long total = marketMapper.findTotals(query);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",arrayList);

        marketMapper.findCount();
        return map;
    }

    /**
     * @Description: 查询单条详细信息
     * @Param: [id]
     * @Return: com.jsy.community.vo.proprietor.ProprietorMarketVO
     * @Author: DKS
     * @Date: 2021/11/1-10:36
     **/
    @Override
    public ProprietorMarketVO findOne(Long id) {
        ProprietorMarketEntity marketEntity = marketMapper.selectOne(new QueryWrapper<ProprietorMarketEntity>().eq("id", id));
        ProprietorMarketLabelEntity labelEntity = labelMapper.selectOne(new QueryWrapper<ProprietorMarketLabelEntity>().eq("label_id", marketEntity.getLabelId()));
        ProprietorMarketCategoryEntity categoryEntity = categoryMapper.selectOne(new QueryWrapper<ProprietorMarketCategoryEntity>().eq("category_id", marketEntity.getCategoryId()));
        ProprietorMarketVO marketVO = new ProprietorMarketVO();
        BeanUtils.copyProperties(marketEntity,marketVO);
        marketVO.setLabelName(labelEntity.getLabel());
        marketVO.setCategoryName(categoryEntity.getCategory());
        // 补充标价
        marketVO.setPriceName(marketEntity.getNegotiable() == 1 ? "面议" : String.valueOf(marketEntity.getPrice()));
        // 补充小区名
        CommunityEntity communityEntity = communityMapper.selectById(marketEntity.getCommunityId());
        marketVO.setCommunityName(communityEntity.getName());
        // 补充发布人名
        UserEntity userEntity = userMapper.getUserMobileByUid(marketEntity.getUid());
        marketVO.setRealName(userEntity.getRealName());
        return marketVO;
    }
    
    /**
     * @Description: 修改屏蔽商品
     * @author: DKS
     * @since: 2021/11/1 10:56
     * @Param: [id, shield]
     * @return: boolean
     */
    @Override
    public boolean updateShield(Long id, Integer shield) {
        ProprietorMarketEntity marketEntity = new ProprietorMarketEntity();
        if (shield.equals(STATE_ZERO)) {
            marketEntity.setShield(STATE_ZERO);
        } else {
            marketEntity.setShield(STATE_ONE);
        }
        return marketMapper.update(marketEntity,new UpdateWrapper<ProprietorMarketEntity>().eq("id",id)) == 1;
    }

    /**
     * @Description: 查询黑名单商品
     * @Param: [baseQO]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: DKS
     * @Date: 2021/11/1-11:00
     **/
    @Override
    public Map<String, Object> selectMarketBlacklist(BaseQO baseQO) {
        if (baseQO.getSize() == 0 || baseQO.getSize() == null){
            baseQO.setSize(10L);
        }
        Long page1  = baseQO.getPage() ;
        if (page1 == 0){
            page1++;
        }
        page1 =(baseQO.getPage()-1)*baseQO.getSize();

        ArrayList<ProprietorMarketVO> arrayList = new ArrayList<>();
        List<ProprietorMarketEntity> list =  marketMapper.selectMarketBlacklist(page1,baseQO.getSize());
        for (ProprietorMarketEntity li : list){
            ProprietorMarketVO marketVO = new ProprietorMarketVO();
            BeanUtils.copyProperties(li,marketVO);
            // 补充标价
            marketVO.setPriceName(li.getNegotiable() == 1 ? "面议" : String.valueOf(li.getPrice()));
            arrayList.add(marketVO);
        }
        Long total = marketMapper.findCount();
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",arrayList);
        return map;
    }
}