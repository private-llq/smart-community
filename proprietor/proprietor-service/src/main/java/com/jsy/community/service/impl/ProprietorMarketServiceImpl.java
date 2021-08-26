package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IProprietorMarketService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketLabelEntity;
import com.jsy.community.mapper.ProprietorMarketCategoryMapper;
import com.jsy.community.mapper.ProprietorMarketLabelMapper;
import com.jsy.community.mapper.ProprietorMarketMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.ProprietorMarketQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.proprietor.ProprietorMarketVO;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@DubboService(version = Const.version, group = Const.group_proprietor)
public class ProprietorMarketServiceImpl extends ServiceImpl<ProprietorMarketMapper, ProprietorMarketEntity> implements IProprietorMarketService {
    //上下架常量
    private final static  Integer STATE_ZERO=0;
    private final static  Integer STATE_ONE=1;

    private  final static Integer NEGOTIABLE_ZERO=0;

   @Autowired
   private ProprietorMarketMapper marketMapper;

   @Autowired
   private ProprietorMarketLabelMapper labelMapper;

   @Autowired
   private ProprietorMarketCategoryMapper categoryMapper;

    /**
     * @Description: 发布商品信息
     * @Param: [marketQO, userId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/20-17:34
     **/
    @Override
    public boolean addMarket(ProprietorMarketQO marketQO, String userId) {
        ProprietorMarketEntity marketEntity = new ProprietorMarketEntity();
        BeanUtils.copyProperties(marketQO,marketEntity);
        marketEntity.setUid(userId);
        marketEntity.setId(SnowFlake.nextId());

        //没有价格 面议（默认面议）
        if (marketEntity.getPrice()!=null){
            marketEntity.setNegotiable(NEGOTIABLE_ZERO);
        }
        //默认下架
        marketEntity.setState(STATE_ZERO);
        return  marketMapper.insert(marketEntity)==1;

        /*List list = Arrays.asList(marketQO.getImages().split(","));*/
        /*marketEntity.setImages(list);*/
    }

    /**
     * @Description: 修改商品
     * @Param: [marketQO, userId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-15:58
     **/
    @Override
    public boolean updateMarket(ProprietorMarketQO marketQO, String userId) {
        ProprietorMarketEntity marketEntity = new ProprietorMarketEntity();
        BeanUtils.copyProperties(marketQO,marketEntity);
        //没有价格 面议（默认面议）
        if (marketEntity.getPrice()!=null){
            marketEntity.setNegotiable(NEGOTIABLE_ZERO);
        }
        return marketMapper.update(marketEntity,new UpdateWrapper<ProprietorMarketEntity>().eq("id",marketQO.getId())) == 1;
    }

    /**
     * @Description:删除发布的商品
     * @Param: [id]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-15:58
     **/
    @Override
    public boolean deleteMarket(Long id) {
        return marketMapper.delete(new QueryWrapper<ProprietorMarketEntity>().eq("id",id)) == 1;
    }


    /**
     * @Description: 修改商品上下架  0下架 1上架   默认新发布为下架
     * @Param: [id, state]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-15:57
     **/@Override
    public boolean updateState(Long id,Integer state) {
        ProprietorMarketEntity marketEntity = new ProprietorMarketEntity();
        if (state==STATE_ZERO){
            marketEntity.setState(STATE_ONE);
        }else
        {
            marketEntity.setState(STATE_ZERO);
        }
        return marketMapper.update(marketEntity,new UpdateWrapper<ProprietorMarketEntity>().eq("id",id)) == 1;
    }

    /**
     * @Description: 查询当前用户已发布商品
     * @Param: [baseQO, userId]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Tian
     * @Date: 2021/8/23-10:40
     **/
    @Override
    public Map<String, Object> selectMarketPage(BaseQO<ProprietorMarketEntity> baseQO, String userId) {
        Page<ProprietorMarketQO> page = new Page<>(baseQO.getPage(), baseQO.getSize());

        ProprietorMarketEntity query = baseQO.getQuery();

        query.setUid(userId);

        if (baseQO.getSize()==0 || baseQO.getSize()==null){
            baseQO.setSize(10l);
        }
        Long page1  = baseQO.getPage() ;
        if (page1 == 0){
            page1++;
        }

        page1 =(baseQO.getPage()-1)*baseQO.getSize();
        ArrayList<ProprietorMarketVO> arrayList = new ArrayList<>();
        List<ProprietorMarketEntity> list =  marketMapper.selectMarketPage(page1,baseQO.getSize(),query);
        for (ProprietorMarketEntity li : list){
            ProprietorMarketVO marketVO = new ProprietorMarketVO();
            BeanUtils.copyProperties(li,marketVO);
            arrayList.add(marketVO);
        }
        Long total = marketMapper.findTotal(query);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",arrayList);
        System.out.println(list);
        System.out.println(total);
        return map;

    }

    /**
     * @Description: 查询所有用户已发布的商品
     * @Param: [baseQO]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Tian
     * @Date: 2021/8/23-10:45
     **/
    @Override
    public Map<String, Object> selectMarketAllPage(BaseQO<ProprietorMarketQO> baseQO) {
        Page<ProprietorMarketQO> page = new Page<>(baseQO.getPage(), baseQO.getSize());

        if (baseQO.getSize()==0 || baseQO.getSize()==null){
            baseQO.setSize(10l);
        }
/*        ProprietorMarketQO query = baseQO.getQuery();*/


        Long page1  = baseQO.getPage() ;
        if (page1 == 0){
            page1++;
        }
        page1 =(baseQO.getPage()-1)*baseQO.getSize();


        ArrayList<ProprietorMarketVO> arrayList = new ArrayList<>();
        List<ProprietorMarketQO> list =  marketMapper.selectMarketAllPage(page1,baseQO.getSize());

        /*for (ProprietorMarketQO li : list){
            ProprietorMarketVO marketVO = new ProprietorMarketVO();
            BeanUtils.copyProperties(li,marketVO);
            arrayList.add(marketVO);
        }*/
        Long total = marketMapper.findTotals();
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",list);
        return map;
    }

    /**
     * @Description: 查询单条详细信息
     * @Param: [id]
     * @Return: com.jsy.community.vo.proprietor.ProprietorMarketVO
     * @Author: Tian
     * @Date: 2021/8/23-14:06
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
        return marketVO;
    }

}
