package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityFunService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.CommunityFunMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CommunityFunOperationQO;
import com.jsy.community.qo.property.CommunityFunQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.es.ElasticsearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import com.jsy.community.vo.admin.AdminInfoVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

/**
 * @program: com.jsy.community
 * @description: 物业社区趣事
 * @author: Hu
 * @create: 2020-12-09 10:51
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class CommunityFunServiceImpl extends ServiceImpl<CommunityFunMapper, CommunityFunEntity> implements ICommunityFunService {

    //缓存key，分组+id
    private final String COMMUNITY_FUN_COUNT="community_fun_count:";
    @Autowired
    private CommunityFunMapper communityFunMapper;
    @Autowired
    private AdminUserMapper adminUserMapper;

    @Resource
    private RedisTemplate redisTemplate;


    /**
     * @Description: 每天查询所有已发布的社区趣事并更新浏览量
     * @author: Hu
     * @since: 2021/6/17 16:24
     * @Param: []
     * @return: void
     */
    @Override
    @Transactional
    public void listByUpdate() {
        List<CommunityFunEntity> entities = communityFunMapper.selectList(new QueryWrapper<CommunityFunEntity>().eq("status", 1));
        for (CommunityFunEntity entity : entities) {
            Object count = redisTemplate.opsForValue().get(COMMUNITY_FUN_COUNT + entity.getId());
            if (count!=null){
                int parseInt = Integer.parseInt(String.valueOf(count));
                entity.setViewCount(parseInt);
                communityFunMapper.updateById(entity);
            }
        }
    }

    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/5/21 11:14
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo
     */
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
            wrapper.like("tallys",communityFunQO.getTallys()).or().like("content",communityFunQO.getTallys());
        }
        if (communityFunQO.getStatus()!=null&&communityFunQO.getStatus()!=0){
            wrapper.eq("status",communityFunQO.getStatus());
        }
        if (communityFunQO.getRedactStatus()!=null&&communityFunQO.getRedactStatus()!=0){
            wrapper.eq("redact_status",communityFunQO.getRedactStatus());
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
            wrapper.le("start_time",communityFunQO.getIssueTimeOut());
        }
        wrapper.orderByDesc("create_time");
        IPage<CommunityFunEntity>  page = communityFunMapper.selectPage(new Page<CommunityFunEntity>(baseQO.getPage(), baseQO.getSize()),wrapper);
        PageInfo pageInfo=new PageInfo();
        List<CommunityFunEntity> list = page.getRecords();
        for (CommunityFunEntity entity : list) {
            if (entity.getTallys()!=null){
                entity.setTallyArrays(entity.getTallys().split(","));
            }
        }
        page.setRecords(list);
        BeanUtils.copyProperties(page,pageInfo);
        return pageInfo;
    }





    /**
     * @Description: 撤销
     * @author: Hu
     * @since: 2021/5/21 11:15
     * @Param: [id]
     * @return: void
     */
    @Override
    public void tapeOut(Long id) {
        CommunityFunEntity entity = communityFunMapper.selectById(id);
        if (entity.getStatus()==2){
            throw new PropertyException("该趣事已下线");
        }
        entity.setStatus(2);
        entity.setStartTime(LocalDateTime.now());
        communityFunMapper.updateById(entity);
        ElasticsearchImportProvider.elasticOperationSingle(id, RecordFlag.FUN, Operation.DELETE, null, null);
    }



    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/5/21 11:15
     * @Param: [communityFunOperationQO, adminInfoVo]
     * @return: void
     */
    @Override
    public void insetOne(CommunityFunOperationQO communityFunOperationQO, AdminInfoVo adminInfoVo) {
        Pattern pattern = compile("<.+?>", DOTALL);
        Matcher matcher = pattern.matcher(communityFunOperationQO.getContent());
        String string = matcher.replaceAll("");

        CommunityFunEntity entity = new CommunityFunEntity();
        entity.setCommunityId(adminInfoVo.getCommunityId());
        entity.setTitleName(communityFunOperationQO.getTitleName());
        entity.setViewCount(communityFunOperationQO.getViewCount());
        entity.setType(communityFunOperationQO.getType());
        entity.setCreateBy(adminInfoVo.getUid());
        entity.setCreateName(adminInfoVo.getRealName());
        entity.setContent(communityFunOperationQO.getContent());
        entity.setSmallImageUrl(communityFunOperationQO.getSmallImageUrl());
        entity.setCoverImageUrl(communityFunOperationQO.getCoverImageUrl());
        entity.setStatus(2);
        entity.setRedactStatus(2);
        entity.setOriginalContent(string);
        String tallys = Arrays.toString(communityFunOperationQO.getTallys());
        entity.setTallys(tallys.substring(1, tallys.length() - 1));
        entity.setId(SnowFlake.nextId());
        communityFunMapper.insert(entity);
    }


    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/5/21 11:15
     * @Param: [communityFunOperationQO, adminInfoVo]
     * @return: void
     */
    @Override
    public void updateOne(CommunityFunOperationQO communityFunOperationQO,AdminInfoVo adminInfoVo) {
        CommunityFunEntity entity = communityFunMapper.selectById(communityFunOperationQO.getId());
        entity.setUpdateBy(adminInfoVo.getUid());
        entity.setUpdateName(adminInfoVo.getRealName());
        String tallys = Arrays.toString(communityFunOperationQO.getTallys());
        entity.setTallys(tallys.substring(1, tallys.length() - 1));
        entity.setContent(communityFunOperationQO.getContent());
        entity.setCoverImageUrl(communityFunOperationQO.getCoverImageUrl());
        entity.setSmallImageUrl(communityFunOperationQO.getSmallImageUrl());
        entity.setTitleName(communityFunOperationQO.getTitleName());
        communityFunMapper.updateById(entity);
        ElasticsearchImportProvider.elasticOperationSingle(entity.getId(), RecordFlag.FUN, Operation.UPDATE, entity.getTitleName(), entity.getSmallImageUrl(),entity.getCommunityId());
    }



    /**
     * @Description: 删除
     * @author: Hu
     * @since: 2021/5/21 11:15
     * @Param: [id]
     * @return: void
     */
    @Override
    public void deleteById(Long id) {
        communityFunMapper.deleteById(id);
        ElasticsearchImportProvider.elasticOperationSingle(id, RecordFlag.FUN, Operation.DELETE, null, null);
    }



  /**
   * @Description: 查询一条
   * @author: Hu
   * @since: 2021/5/21 11:15
   * @Param: [id]
   * @return: com.jsy.community.entity.CommunityFunEntity
   */
  @Override
  public CommunityFunEntity selectOne(Long id) {
      CommunityFunEntity entity = communityFunMapper.selectById(id);
      if (entity!=null&&entity.getTallys()!=null){
          entity.setTallyArrays(entity.getTallys().split(","));
      }
      return entity;
  }


    /**
     * @Description: 发布
     * @author: Hu
     * @since: 2021/5/21 11:15
     * @Param: [id, adminInfoVo]
     * @return: void
     */
    @Override
    public void popUpOnline(Long id,AdminInfoVo adminInfoVo) {
        CommunityFunEntity entity = communityFunMapper.selectById(id);
        if (entity==null){
            throw new PropertyException("该趣事不存在！");
        }
        if (entity.getStatus()==1){
            throw new PropertyException("该趣事已上线！");
        }
        entity.setStartName(adminInfoVo.getRealName());
        entity.setRedactStatus(1);
        entity.setStatus(1);
        entity.setStartBy(adminInfoVo.getUid());
        entity.setStartTime(LocalDateTime.now());
        communityFunMapper.updateById(entity);
        ElasticsearchImportProvider.elasticOperationSingle(id, RecordFlag.FUN, Operation.INSERT, entity.getTitleName(), entity.getSmallImageUrl(),entity.getCommunityId());
    }



}
