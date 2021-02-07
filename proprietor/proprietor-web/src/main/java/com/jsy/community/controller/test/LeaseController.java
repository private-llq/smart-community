package com.jsy.community.controller.test;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommonService;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.utils.es.ElasticSearchImport;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author YuLF
 * @since 2021-02-02 14:33
 */
@RestController
@RequestMapping("/leaseHouse")
@ApiJSYController
@RequiredArgsConstructor
public class LeaseController {

    private final RestHighLevelClient elasticsearchClient;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ICommonService commonService;

    @GetMapping("/esBulkImport")
    public String test() throws IOException {
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
            return "true";
        }
        return "false";
    }


    @PostMapping()
    public String insertLeaseHouse(@RequestBody HouseLeaseQO qo){
        System.out.println("----------------房屋请求数据插入--------------------");
        ElasticSearchImport.elasticOperation( qo.getId(), RecordFlag.LEASE_HOUSE, Operation.INSERT, qo.getHouseTitle(), qo.getHouseImage()[0]);
        //在操作完成时调用
        return "true";
    }


    @DeleteMapping()
    public String deleteLeaseHouse(@RequestParam Long id){
        System.out.println("----------------删除数据插入--------------------");
        ElasticSearchImport.elasticOperation(id, RecordFlag.LEASE_HOUSE, Operation.DELETE, null, null);
        //在操作完成时调用
        return "true";
    }

    @PutMapping()
    public String deleteLeaseHouse(@RequestBody HouseLeaseQO qo){
        System.out.println("----------------更新数据插入--------------------");
        ElasticSearchImport.elasticOperation( qo.getId(), RecordFlag.LEASE_HOUSE, Operation.UPDATE, qo.getHouseTitle(), qo.getHouseImage()[0]);
        //在操作完成时调用
        return "true";
    }

    @GetMapping()
    public String getLeaseHouse(@RequestBody HouseLeaseQO qo){
        System.out.println("----------------更新数据插入--------------------");
        ElasticSearchImport.elasticOperation( qo.getId(), RecordFlag.LEASE_HOUSE, Operation.UPDATE, qo.getHouseTitle(), qo.getHouseImage()[0]);
        //在操作完成时调用
        return "true";
    }

}
