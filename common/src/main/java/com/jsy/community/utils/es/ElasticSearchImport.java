package com.jsy.community.utils.es;

import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.utils.SpringContextUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;


/**
 * @author YuLF
 * @since 2021-02-02 14:41
 */
public class ElasticSearchImport {

    private static RabbitTemplate rabbitTemplate;
    /**
     * 往MQ队列里面生产消息，主要为往ES中导入数据
     * 增删改接口业务最后增加此方法、
     * @author YuLF
     * @since  2021/2/2 15:40
     * @Param recordFlag        数据标记：租赁、商铺、趣事、消息...，用于标识这条数据操作是请求对应接口
     * @Param recordTitle       数据标题：用于ES全文检索
     * @Param id                数据唯一ID
     * @Param operation         数据操作：告知ES中该数据是删除还是插入还是修改
     * @Param recordPicture     数据的列表icon：如果该数据存在
     */
    public static void elasticOperation(@NonNull Long id, @NonNull RecordFlag recordFlag, @NonNull Operation operation, @Nullable String recordTitle, @Nullable String recordPicture){
        FullTextSearchEntity fullTextSearchEntity = new FullTextSearchEntity();
        fullTextSearchEntity.setId(id);
        fullTextSearchEntity.setFlag(recordFlag);
        fullTextSearchEntity.setOperation(operation);
        fullTextSearchEntity.setTitle(recordTitle);
        fullTextSearchEntity.setPicture(recordPicture);
        RabbitTemplate rabbitTemplate = getRabbitTemplate();
        rabbitTemplate.convertAndSend(BusinessConst.APP_SEARCH_EXCHANGE_NAME, BusinessConst.APP_SEARCH_ROUTE_KEY, fullTextSearchEntity);
    }


    public static RabbitTemplate getRabbitTemplate(){
        if( rabbitTemplate == null ){
            synchronized (ElasticSearchImport.class){
                if( rabbitTemplate == null ){
                    rabbitTemplate = (RabbitTemplate) SpringContextUtils.getBean("customRabbitTemplate");
                }
            }
        }
        return rabbitTemplate;
    }



}
