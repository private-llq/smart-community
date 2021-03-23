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
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.CommunityFunMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityFunOperationQO;
import com.jsy.community.qo.property.CommunityFunQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.es.ElasticsearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
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
    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public PageInfo findList(BaseQO<CommunityFunQO> baseQO) {
        CommunityFunQO communityFunQO = baseQO.getQuery();
        Map<String,Object> map = new HashMap<>();
        if (baseQO.getSize()==0||baseQO.getSize()==null)
        {
            baseQO.setSize(10L);
        }
        QueryWrapper<CommunityFunEntity> wrapper = new QueryWrapper<CommunityFunEntity>();
        if (!"".equals(communityFunQO.getHeadline())&&communityFunQO.getHeadline()!=null) {
            wrapper.like("title_name", communityFunQO.getHeadline());
        }
        if (!"".equals(communityFunQO.getTallys())&&communityFunQO.getTallys()!=null){
            wrapper.like("tallys",communityFunQO.getTallys());
        }
        if (communityFunQO.getStatus()!=null&&communityFunQO.getStatus()!=0){
                wrapper.like("status",communityFunQO.getStatus());
        }
        if (communityFunQO.getRedactStatus()!=null&&communityFunQO.getRedactStatus()!=0){
                wrapper.like("redact_status",communityFunQO.getRedactStatus());
        }
        if (communityFunQO.getCreatrTimeStart()!=null){
            wrapper.ge("create_time",communityFunQO.getCreatrTimeStart());
        }
        if (communityFunQO.getCreatrTimeOut()!=null){
            communityFunQO.setCreatrTimeOut(communityFunQO.getCreatrTimeOut().plusDays(1));
            wrapper.le("create_time",communityFunQO.getCreatrTimeOut());
        }
        if (communityFunQO.getIssueTimeStart()!=null){
            wrapper.ge("start_time",communityFunQO.getIssueTimeStart());
        }
        if (communityFunQO.getIssueTimeOut()!=null){
            communityFunQO.setIssueTimeOut(communityFunQO.getIssueTimeOut().plusDays(1));
            wrapper.le("start_time",communityFunQO.getCreatrTimeStart());
        }

        Page<CommunityFunEntity> communityFunEntityPage = new Page<>(baseQO.getPage(), baseQO.getSize());
        IPage<CommunityFunEntity>  page = communityFunMapper.selectPage(new Page<CommunityFunEntity>(baseQO.getPage(), baseQO.getSize()),wrapper);
        PageInfo pageInfo=new PageInfo();
        BeanUtils.copyProperties(page,pageInfo);
        return pageInfo;
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
    public void insetOne(CommunityFunOperationQO communityFunOperationQO, String uid) {
        AdminUserEntity userEntity = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().eq("uid", uid));
        CommunityFunEntity entity = new CommunityFunEntity();
        entity.setTitleName(communityFunOperationQO.getTitleName());
        entity.setViewCount(communityFunOperationQO.getViewCount());
        entity.setCreateBy(communityFunOperationQO.getUid());
        if (userEntity!=null){
            entity.setCreateName(userEntity.getRealName());
        }
        entity.setContent(communityFunOperationQO.getContent());
        entity.setSmallImageUrl(communityFunOperationQO.getSmallImageUrl());
        entity.setCoverImageUrl(communityFunOperationQO.getCoverImageUrl());
        entity.setStatus(2);
        entity.setRedactStatus(2);
        String tallys = Arrays.toString(communityFunOperationQO.getTallys());
        entity.setTallys(tallys.substring(1, tallys.length() - 1));
        entity.setId(SnowFlake.nextId());
        communityFunMapper.insert(entity);
    }


    @Override
    @EsImport( operation = Operation.UPDATE, recordFlag = RecordFlag.FUN, parameterType = CommunityFunEntity.class, importField = {"titleName","smallImageUrl"}, searchField = {"titleName"})
    public void updateOne(CommunityFunOperationQO communityFunOperationQO, String uid) {
        AdminUserEntity userEntity = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().eq("uid", uid));
        CommunityFunEntity entity = communityFunMapper.selectById(communityFunOperationQO.getId());
        entity.setUpdateBy(uid);
        if (userEntity!=null){
            entity.setUpdateName(userEntity.getRealName());
        }
        String tallys = Arrays.toString(communityFunOperationQO.getTallys());
        entity.setTallys(tallys.substring(1, tallys.length() - 1));
        entity.setContent(communityFunOperationQO.getContent());
        entity.setCoverImageUrl(communityFunOperationQO.getCoverImageUrl());
        entity.setSmallImageUrl(communityFunOperationQO.getSmallImageUrl());
        entity.setTitleName(communityFunOperationQO.getTitleName());
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
  public void popUpOnline(Long id,String uid) {
    AdminUserEntity userEntity = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().eq("uid", uid));
    CommunityFunEntity entity = communityFunMapper.selectById(id);
    if (entity.getStatus()==1){
      throw new PropertyException("该趣事已上线");
    }
    if (userEntity!=null){
        entity.setStartName(userEntity.getRealName());
    }
    entity.setRedactStatus(1);
    entity.setStatus(1);
    entity.setStartBy(uid);
    entity.setStartTime(LocalDateTime.now());
    communityFunMapper.updateById(entity);
    ElasticsearchImportProvider.elasticOperationSingle(id, RecordFlag.FUN, Operation.INSERT, entity.getTitleName(), entity.getSmallImageUrl());
  }

}
