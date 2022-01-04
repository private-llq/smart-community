package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.entity.proprietor.VoteOptionEntity;
import com.jsy.community.entity.proprietor.VoteTopicEntity;
import com.jsy.community.entity.proprietor.VoteUserEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.IVoteService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: com.jsy.community
 * @description: 投票问卷
 * @author: DKS
 * @create: 2021-11-08 10:29
 **/
@Service
public class VoteServiceImpl extends ServiceImpl<VoteMapper,VoteEntity> implements IVoteService {

    @Resource
    private VoteMapper voteMapper;

    @Resource
    private VoteOptionMapper voteOptionMapper;

    @Resource
    private VoteTopicMapper voteTopicMapper;

    @Resource
    private VoteUserMapper voteUserMapper;

    @Resource
    private HouseMapper houseMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;
    
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;
    
    /**
     * @Description: 分页查询
     * @author: DKS
     * @since: 2021/11/8 10:54
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.VoteEntity>
     */
    @Override
    public PageInfo<VoteEntity> list(BaseQO<VoteEntity> baseQO) {
        VoteEntity query = baseQO.getQuery();
        Page<VoteEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<VoteEntity> wrapper = new QueryWrapper<>();
        if (!"".equals(query.getTheme())&&query.getTheme()!=null){
            wrapper.like("theme",query.getTheme());
        }
        if (query.getVoteStatus()!=null&&query.getVoteStatus()!=0){
            wrapper.like("vote_status",query.getVoteStatus());
        }
        Page<VoteEntity> pageData = voteMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        for (VoteEntity record : pageData.getRecords()) {
            // 查询已投票人数
            record.setVoteTotal(voteUserMapper.selectCount(new QueryWrapper<VoteUserEntity>().eq("vote_id", record.getId())));
            record.setVoteStatusName(record.getVoteStatus() == 1 ? "待发布" : record.getVoteStatus() == 2 ? "进行中" : "已发布");
            if (record.getCityId().equals("0")){
                record.setScope("全小区");
            } else {
                StringBuilder sb = new StringBuilder();
                String[] split = record.getCityId().split(",");
                for (String s : split) {
                    String cityName = redisTemplate.opsForValue().get("RegionSingle:" + s);
                    sb.append(cityName).append(",");
                }
                record.setScope(sb.deleteCharAt(sb.length()-1).toString());
            }
        }
        PageInfo<VoteEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
    
    /**
     * @Description: 新增投票问卷
     * @author: DKS
     * @since: 2021/11/8 11:15
     * @Param: [voteEntity]
     * @return: void
     */
    @Override
    @Transactional
    public void saveBy(VoteEntity voteEntity) {
        voteEntity.setId(SnowFlake.nextId());
        voteEntity.setCommunityId(0L);
        voteMapper.insert(voteEntity);

        VoteTopicEntity topicEntity = voteEntity.getVoteTopicEntity();
        topicEntity.setId(SnowFlake.nextId());
        topicEntity.setVoteId(voteEntity.getId());
        voteTopicMapper.insert(topicEntity);

        List<VoteOptionEntity> list = new LinkedList<>();
        List<VoteOptionEntity> options = topicEntity.getOptions();
        for (int i= 1;i<=options.size();i++) {
            VoteOptionEntity option = options.get(i-1);
            option.setId(SnowFlake.nextId());
            option.setVoteId(voteEntity.getId());
            option.setTopicId(topicEntity.getId().toString());
            option.setCode(i);
            list.add(option);
        }
        if (list.size()!=0){
            voteOptionMapper.saveAll(list);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("type",2);
        map.put("dataId",voteEntity.getId());

        //投票进行中
        map.put("status",2);
        rabbitTemplate.convertAndSend("exchange_activity_delay", "queue.activity.delay", map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay", LocalDateTime.now().until(voteEntity.getBeginTime(), ChronoUnit.MILLIS));
                return message;
            }
        });

        //投票已结束
        map.put("status",3);
        rabbitTemplate.convertAndSend("exchange_activity_delay", "queue.activity.delay", map, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay", LocalDateTime.now().until(voteEntity.getOverTime(), ChronoUnit.MILLIS));
                return message;
            }
        });
    }
    
    /**
     * @Description: 投票问卷详情
     * @author: DKS
     * @since: 2021/11/8 11:14
     * @Param: [id]
     * @return: java.util.List<com.jsy.community.entity.proprietor.VoteUserEntity>
     */
    @Override
    public List<VoteUserEntity> getOne(Long id) {
        Set<String> ids = new HashSet<>();
        HashMap<String, String> map = new HashMap<>();
        List<VoteUserEntity> entityList = voteUserMapper.selectList(new QueryWrapper<VoteUserEntity>().eq("vote_id", id));
        for (VoteUserEntity voteUserEntity : entityList) {
            ids.add(voteUserEntity.getUid());
        }
        Set<Long> idSet = ids.stream().map(Long::parseLong).collect(Collectors.toSet());
        if (idSet.size()!=0){
            List<RealUserDetail> realUserDetailsByUid = baseUserInfoRpcService.getRealUserDetailsByUid(idSet);
            for (RealUserDetail userDetail : realUserDetailsByUid) {
                map.put(String.valueOf(userDetail.getId()),userDetail.getNickName());
            }
            for (VoteUserEntity entity : entityList) {
                entity.setRealName(map.get(entity.getUid()));
            }
        }
        return entityList;
    }
    
    /**
     * @Description: 删除或撤销
     * @author: DKS
     * @since: 2021/11/8 11:12
     * @Param: [id]
     * @return: boolean
     */
    @Override
    public boolean delete(Long id) {
        int result;
        Integer integer = voteUserMapper.selectCount(new QueryWrapper<VoteUserEntity>().eq("vote_id", id));
        if (integer==0){
            VoteEntity entity = voteMapper.selectById(id);
            if (entity.getIssueStatus()==1){
                entity.setVoteStatus(1);
                entity.setIssueStatus(2);
                result = voteMapper.updateById(entity);
            }else {
                result = voteMapper.deleteById(id);
            }
        } else {
            throw new AdminException("当前问卷不能被撤销！");
        }
        return result > 0;
    }
    
    /**
     * @Description: 查图表
     * @author: DKS
     * @since: 2021/11/8 11:13
     * @Param: [id]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String, Object> getChart(Long id) {
        return getPlan(id);
    }
    
    /**
     * @Description: 投票进度
     * @author: DKS
     * @since: 2021/11/8 11:07
     * @Param: java.long.Long
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    private Map<String, Object> getPlan(Long id) {
        VoteEntity voteEntity = voteMapper.selectById(id);
        Map<String, Object> map = new HashMap<>();
        Set<String> set = voteUserMapper.getUserTotal(id);
        if (voteEntity!=null){
            List<VoteOptionEntity> list = voteOptionMapper.getPlan(id);
            map.put("choose",voteEntity.getChoose());
            map.put("total",voteEntity.getTotal());
            map.put("list",list);
            map.put("haveTotal",set.size());
        }
        return map;
    }
    
    /**
     * @Description: 查询一条详情
     * @author: DKS
     * @since: 2021/11/8 16:41
     * @Param: [id]
     * @return: com.jsy.community.entity.proprietor.VoteEntity
     */
    @Override
    public VoteEntity getVote(Long id) {
        VoteEntity voteEntity = voteMapper.selectById(id);
        VoteTopicEntity topicEntity = voteTopicMapper.selectOne(new QueryWrapper<VoteTopicEntity>().eq("vote_id", id));
        if (voteEntity != null){
            List<VoteOptionEntity> voteList = voteOptionMapper.selectList(new QueryWrapper<VoteOptionEntity>().eq("vote_id", id));
            topicEntity.setOptions(voteList);
            voteEntity.setVoteTopicEntity(topicEntity);
            return voteEntity;
        }
        throw new AdminException("当前活动不存在或者已结束！");
    }
}
