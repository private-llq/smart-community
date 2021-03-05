package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.annotation.EsImport;
import com.jsy.community.api.ICommunityFunService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.mapper.CommunityFunMapper;
import com.jsy.community.qo.CommunityFunQO;
import com.jsy.community.utils.es.ElasticSearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 社区趣事
 * @author: Hu
 * @create: 2020-12-09 10:51
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class CommunityFunServiceImpl extends ServiceImpl<CommunityFunMapper, CommunityFunEntity> implements ICommunityFunService {

    @Autowired
    private CommunityFunMapper communityFunMapper;

    @Override
    public Map<String,Object> findList(CommunityFunQO communityFunQO) {
        Map<String,Object> map = new HashMap<>();
        if (communityFunQO.getSize()==0||communityFunQO.getSize()==null)
        {
            communityFunQO.setSize(10l);
        }
        QueryWrapper<CommunityFunEntity> wrapper = new QueryWrapper<CommunityFunEntity>();
        if (communityFunQO.getHeadline()!=null&&!"".equals(communityFunQO.getHeadline())) {
            wrapper.like("title_name", communityFunQO.getHeadline());
        }
        IPage<CommunityFunEntity>  page = communityFunMapper.selectPage(new Page<CommunityFunEntity>(communityFunQO.getPage(), communityFunQO.getSize()),wrapper);
        List<CommunityFunEntity> list = page.getRecords();

        long total = page.getTotal();
        map.put("list",list);
        map.put("total",total);
        return map;
    }

    @Override
    @EsImport(operation = Operation.DELETE, recordFlag = RecordFlag.FUN)
    public void tapeOut(Long id) {
        CommunityFunEntity entity = communityFunMapper.selectById(id);
        if (entity.getStatus()==2){
            throw new PropertyException("该趣事已下线");
        }
        entity.setStatus(2);
        entity.setStartTime(LocalDateTime.now());
        communityFunMapper.updateById(entity);
    }

    @Override
    public void insetOne(CommunityFunEntity communityFunEntity) {
        communityFunMapper.insert(communityFunEntity);
    }


    @Override
    @EsImport( operation = Operation.UPDATE, recordFlag = RecordFlag.FUN, parameterType = CommunityFunEntity.class, importField = {"titleName","smallImageUrl"}, searchField = {"titleName"})
    public void updateOne(CommunityFunEntity communityFunEntity) {
        CommunityFunEntity entity = communityFunMapper.selectById(communityFunEntity.getId());

        entity.setContent(communityFunEntity.getContent());
        entity.setCoverImageUrl(communityFunEntity.getCoverImageUrl());
        entity.setSmallImageUrl(communityFunEntity.getSmallImageUrl());
        entity.setTitleName(communityFunEntity.getTitleName());
        communityFunMapper.updateById(entity);
    }

    @Override
    @EsImport(operation = Operation.DELETE, recordFlag = RecordFlag.FUN)
    public void deleteById(Long id) {
        communityFunMapper.deleteById(id);
    }

  @Override
  public CommunityFunEntity selectOne(Long id) {
    return communityFunMapper.selectById(id);
  }

  @Override
  public void popUpOnline(Long id) {
    CommunityFunEntity entity = communityFunMapper.selectById(id);
    if (entity.getStatus()==1){
      throw new PropertyException("该趣事已上线");
    }
    entity.setStatus(1);
    entity.setStartTime(LocalDateTime.now());
    communityFunMapper.updateById(entity);
    ElasticSearchImportProvider.elasticOperationSingle(id, RecordFlag.FUN, Operation.INSERT, entity.getTitleName(), entity.getSmallImageUrl());
  }

}
