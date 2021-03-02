package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommonService;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FullTextSearchEntity;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final RestHighLevelClient customElasticsearchClient;

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
        BulkResponse bulk = customElasticsearchClient.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);
        if(bulk.status() == RestStatus.OK){
            return "true";
        }
        return "false";
    }


}
