package com.jsy.community.utils.es;

import com.alibaba.fastjson.JSON;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import com.jsy.community.qo.property.ElasticsearchCarSearchQO;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-25 15:43
 **/
@Slf4j
@Component
public class ElasticsearchCarUtil {
    /**
     * @Description: 删除
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    public static void deleteData(String id,RestHighLevelClient restHighLevelClient){
        //第一个参数为操作索引名称、第二个参数为删除文档的id
        DeleteRequest deleteRequest = new DeleteRequest(
                BusinessConst.INDEX_CAR, id);
        DeleteResponse response = null;
        try {
            response = restHighLevelClient.delete(deleteRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("删除失败："+e.getMessage());
        }
        DocWriteResponse.Result result = response.getResult();
    }

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    public static void updateData(ElasticsearchCarQO elasticsearchCarQO,RestHighLevelClient restHighLevelClient) {
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(BusinessConst.INDEX_CAR);
        //docAsUpsert表示 如果文档不存在则创建
        updateRequest.docAsUpsert(true);
        updateRequest.id(elasticsearchCarQO.getId()+"");
        //转换为json串发给elasticsearch
        updateRequest.doc(JSON.toJSONString(elasticsearchCarQO), XContentType.JSON);
        UpdateResponse update = null;
        try {
            update = restHighLevelClient.update(updateRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("修改失败："+e.getMessage());
        }
        DocWriteResponse.Result result = update.getResult();
    }

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    public static void insertData(ElasticsearchCarQO elasticsearchCarQO,RestHighLevelClient restHighLevelClient){
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        IndexRequest indexRequest = new IndexRequest(BusinessConst.INDEX_CAR);
//        indexRequest.create(true);
        indexRequest.id(elasticsearchCarQO.getId()+"");
        //转换为json串发给elasticsearch
        indexRequest.source(JSON.toJSONString(elasticsearchCarQO), XContentType.JSON);
        //发送保存 第一个参数是构造的请求，第二个参数是请求头需要的一些操作如验证token
        IndexResponse indexResponse = null;
        try {
            indexResponse = restHighLevelClient.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("新增失败："+e.getMessage());
        }
        DocWriteResponse.Result result = indexResponse.getResult();
    }

    /**
     * @Description: 查询
     * @author: Hu
     * @since: 2021/3/26 9:11
     * @Param:
     * @return:
     */
    public static void search(BaseQO<ElasticsearchCarSearchQO> baseQO, RestHighLevelClient restHighLevelClient){
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        ElasticsearchCarSearchQO baseQOQuery = baseQO.getQuery();
        SearchRequest searchRequest = new SearchRequest(BusinessConst.INDEX_CAR);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if ("".equals(baseQOQuery.getCarPlate())&&baseQOQuery.getCarPlate()!=null){
            boolQuery.must(new MatchQueryBuilder("carPlate",baseQOQuery.getCarPlate()));
        }
        if ("".equals(baseQOQuery.getOwner())&&baseQOQuery.getOwner()!=null) {
            boolQuery.must(new MatchQueryBuilder("owner", baseQOQuery.getOwner()));
        }
        if (baseQOQuery.getCarPlate()!=null&&baseQOQuery.getCarType()!=0) {
            boolQuery.must(new TermQueryBuilder("carType", baseQOQuery.getCarType()));
        }
        sourceBuilder.sort(new FieldSortBuilder("createTime").order(SortOrder.DESC));
        sourceBuilder.from((int) (baseQO.getPage()*baseQO.getSize()-1));
        sourceBuilder.size(baseQO.getSize().intValue());
        sourceBuilder.query(boolQuery);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("查询失败："+e.getMessage());
        }
        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
        System.out.println( "总条数：" + totalHits );
        for (SearchHit searchHit : hits.getHits()) {
            String sourceAsString = searchHit.getSourceAsString();
            System.out.println( sourceAsString );
        }
    }
}
