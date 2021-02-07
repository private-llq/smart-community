package com.jsy.community.task;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.DistributedLock;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.utils.DateUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author YuLF
 * @since 2021-1-9 10:17
 * 业主端定时任务执行器
 * 所有增加了@Scheduled的任务 都会 同步执行，
 */
@Service
public class ProprietorTaskActuator {
    /**
     * 社区定时清理社区消息 cron 表达式
     */
    private static final String INFORM_CRON = "0 0 1 ? * mon";
    /**
     * ES全量导入数据 cron 表达式
     */
    private static final String ES_IMPORT_CRON = "0 0 2 ? * mon";


    private static final Logger logger = LoggerFactory.getLogger(ProprietorTaskActuator.class);

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserInformService userInformService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ICommonService commonService;

    @Resource
    private  RestHighLevelClient elasticsearchClient;


    @Value("${jsy.sys.clear.inform.expire}")
    private Integer clearInformExpireDay;

    @Value("${jsy.sys.clear.inform.enable}")
    private boolean clearInformEnabled;

    @PostConstruct
    public void initSourceConst(){
        informActuator();
    }


    /**
     * 数据库全量导入数据至 elasticsearch 查询的表t_acct_push_inform、t_community_fun、t_house_lease、t_shop_lease
     */
    @Scheduled(cron = ES_IMPORT_CRON)
    public void esImportActuator() throws IOException {
        //如果当前执行时间已经超过 指定 执行时间
        if(DateUtils.notNeedImplemented(ES_IMPORT_CRON)){
            return;
        }
        List<FullTextSearchEntity> fullTextSearchEntities = commonService.fullTextSearchEntities();
        BulkRequest bulkRequest = new BulkRequest();
        fullTextSearchEntities.forEach( l -> {
            IndexRequest indexRequest = new IndexRequest(BusinessConst.FULL_TEXT_SEARCH_INDEX)
                    .id(String.valueOf(l.getId()))
                    .source(JSON.toJSONString(l), XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulk = elasticsearchClient.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);
        if(bulk.status() == RestStatus.OK){
            logger.info("全量数据导入数据：t_acct_push_inform、t_community_fun、t_house_lease、t_shop_lease表数据至Elasticsearch成功!");
        } else {
            logger.error("全量数据导入表数据至Elasticsearch失败!");
        }
    }



    /**
     * 定时清理t_acct_push_inform
     * 根据定义的设置 超出多少天 清理掉旧消息
     * 每周一 凌晨1点执行 定时任务
     * @author YuLF
     * @since  2020/1/9 14:19
     */
    @DistributedLock(lockKey = "communityInform", waitTimout = 50)
    @Scheduled(cron = INFORM_CRON)
    public void informActuator(){
        if(clearInformEnabled){
            //如果当前执行时间已经超过 指定 执行时间
            if(DateUtils.notNeedImplemented(INFORM_CRON)){
                return;
            }
            logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "：清理社区推送消息执行开始:");
            //1.获取 clearInformExpireTime 之前的时间
            String beforeTime = getClearInformExpireTime(clearInformExpireDay);
            //如果 数据库 推送消息 小于 beforeTime 的都是超过过期时间的消息 都将删除
            Integer delRow = userInformService.RegularCleaning(beforeTime);
            logger.info("本次清理社区推送消息" + beforeTime + "之前的数据共" + delRow + "条!");
        }
    }

    /**
     * 拿到多少天之前的时间
     * 根据 清理时间 秒 获取到  clearInformExpireTime 天之前的 时间
     * @author YuLF
     * @since  2021/1/11 11:31
     * @Param  clearInformExpireTime（过期时间）   超过多少天 将被清理掉推送消息
     */
    private  String getClearInformExpireTime(Integer clearInformExpireTime) {
        /* 转换为时间格式 */
        DateFormat fmt = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        /* 日历对象 */
        Calendar ca = Calendar.getInstance();
        /* 当前时间 -clearInformExpireTime */
        ca.add( Calendar.DATE, - clearInformExpireTime );
        Date expireTime = ca.getTime();
        return fmt.format(expireTime);
    }

}
