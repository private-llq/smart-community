package com.jsy.community.utils.es;

import com.alibaba.fastjson.JSON;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import com.jsy.community.qo.property.ElasticsearchCarSearchQO;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.ElasticsearchCarVO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 物业车辆操作工具类
 * @author: Hu
 * @create: 2021-03-25 15:43
 **/
@Slf4j
@Component
public class ElasticsearchCarUtil {
    /**
     * @Description: 删除一条
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
     * @Description: 删除全部
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    public static void deleteDataAll(RestHighLevelClient restHighLevelClient){
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest(BusinessConst.INDEX_CAR);
        AcknowledgedResponse response = null;
        try {
            response = restHighLevelClient.indices().delete(deleteRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("删除失败："+e.getMessage());
        }
        boolean b = response.isAcknowledged();
    }

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    public static void updateData(List<ElasticsearchCarQO> cars,RestHighLevelClient restHighLevelClient) {
        for (ElasticsearchCarQO car : cars) {
            //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(BusinessConst.INDEX_CAR);
            //docAsUpsert表示 如果文档不存在则创建
            updateRequest.docAsUpsert(true);
            updateRequest.id(car.getId()+"");
            //转换为json串发给elasticsearch
            updateRequest.doc(JSON.toJSONString(car), XContentType.JSON);
            UpdateResponse update = null;
            try {
                update = restHighLevelClient.update(updateRequest, ElasticsearchConfig.COMMON_OPTIONS);
            } catch (IOException e) {
                e.printStackTrace();
                log.info("修改失败："+e.getMessage());
            }
            DocWriteResponse.Result result = update.getResult();
        }
    }

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    public static void insertData(List<ElasticsearchCarQO> cars, RestHighLevelClient restHighLevelClient){
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        for (ElasticsearchCarQO car : cars) {
            IndexRequest indexRequest = new IndexRequest(BusinessConst.INDEX_CAR);
            indexRequest.create(true);
            indexRequest.id(car.getId()+"");
            //转换为json串发给elasticsearch
            indexRequest.source(JSON.toJSONString(car), XContentType.JSON);
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
    }

    /**
     * @Description: 查询车辆
     * @author: Hu
     * @since: 2021/3/26 9:11
     * @Param:
     * @return:
     */
    public static Map<String, Object> search(BaseQO<ElasticsearchCarSearchQO> baseQO, AdminInfoVo info,RestHighLevelClient restHighLevelClient){
        ElasticsearchCarSearchQO query = baseQO.getQuery();
        SearchRequest searchRequest = new SearchRequest(BusinessConst.INDEX_CAR);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (!"".equals(query.getCarPlate())&&query.getCarPlate()!=null){
            boolQuery.must(new MatchQueryBuilder("carPlate",query.getCarPlate()));
        }
        if (!"".equals(query.getOwner())&&query.getOwner()!=null) {
            boolQuery.must(new MatchQueryBuilder("owner", query.getOwner()));
        }
        if (query.getCarType()!=null&&query.getCarType()!=0) {
            boolQuery.must(new TermQueryBuilder("carType", query.getCarType()));
        }
        //只查询当前的小区的车辆
        boolQuery.must(new TermQueryBuilder("communityId", info.getCommunityId()));

        //创建时间排序
        sourceBuilder.sort(new FieldSortBuilder("createTime").order(SortOrder.DESC));
        //分页
        Long size=(baseQO.getPage()-1)*baseQO.getSize();
        sourceBuilder.from(size.intValue());
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
        Map<String, Object> map = new HashMap<>();
        List<Object> list = new LinkedList<>();
        map.put("total",hits.getTotalHits().value);
        for (SearchHit searchHit : hits.getHits()) {
            String sourceAsString = searchHit.getSourceAsString();
//            JSONObject jsonObject = JSON.parseObject(sourceAsString);
//            Long time = (Long)jsonObject.get("createTime");
//            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(time/1000, 0, ZoneOffset.ofHours(8));
//            jsonObject.put("createTime",dateTime);
//            ElasticsearchCarVO elasticsearchCarVO = JSON.toJavaObject(jsonObject, ElasticsearchCarVO.class);
            ElasticsearchCarVO elasticsearchCarVO = JSON.toJavaObject(JSON.parseObject(sourceAsString), ElasticsearchCarVO.class);
            list.add(elasticsearchCarVO);
            log.info( elasticsearchCarVO +"");
        }
        map.put("list",list);
        return map;
    }

}
